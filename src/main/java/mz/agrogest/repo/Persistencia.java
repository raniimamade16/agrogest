package mz.agrogest.repo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Grava o estado da aplicação num ficheiro e volta a lê-lo, usando a serialização do Java
 * (ObjectOutputStream e ObjectInputStream).
 *
 * Para a gravação ser tolerante a falhas, nunca escrevemos diretamente por cima do ficheiro bom:
 * 1. escrevemos tudo para agrogest.ser.tmp e pedimos ao sistema operativo que passe os dados
 *    mesmo para o disco (fsync);
 * 2. guardamos o ficheiro anterior como agrogest.ser.bak, como cópia de segurança;
 * 3. só no fim trocamos o .tmp pelo agrogest.ser, numa operação atómica (rename).
 * Se o Tomcat for abaixo a meio da escrita, o ficheiro principal continua intacto. E se o ficheiro
 * principal estiver estragado, no arranque tentamos ler a cópia .bak.
 */
public class Persistencia {

    private static final Logger LOG = Logger.getLogger(Persistencia.class.getName());

    private final Path ficheiro;
    private final Path temporario;
    private final Path backup;

    public Persistencia(Path pasta) {
        this.ficheiro = pasta.resolve("agrogest.ser");
        this.temporario = pasta.resolve("agrogest.ser.tmp");
        this.backup = pasta.resolve("agrogest.ser.bak");
    }

    /** Grava todo o estado no ficheiro, seguindo os três passos descritos no topo da classe. */
    public synchronized void gravar(DataStore store) throws IOException {
        Files.createDirectories(ficheiro.getParent());
        Snapshot snapshot = store.snapshot();

        // Passo 1: escrever para o ficheiro temporário.
        try (FileOutputStream fos = new FileOutputStream(temporario.toFile());
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            out.writeObject(snapshot);
            out.flush();
            // Garante que os bytes chegaram ao disco e não ficaram só na memória do sistema operativo.
            fos.getFD().sync();
        }

        // Passo 2: guardar a versão anterior como cópia de segurança.
        if (Files.exists(ficheiro)) {
            Files.copy(ficheiro, backup, StandardCopyOption.REPLACE_EXISTING);
        }
        // Passo 3: trocar os ficheiros de uma só vez. Se o sistema de ficheiros não suportar
        // a troca atómica, fazemos uma troca normal.
        try {
            Files.move(temporario, ficheiro, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporario, ficheiro, StandardCopyOption.REPLACE_EXISTING);
        }
        LOG.info("Estado gravado em " + ficheiro);
    }

    /**
     * Tenta ler primeiro o ficheiro principal e, se falhar, a cópia de segurança. Devolve true se conseguiu carregar algum.
     */
    public synchronized boolean carregar(DataStore store) {
        for (Path p : new Path[] {ficheiro, backup}) {
            Optional<Snapshot> s = ler(p);
            if (s.isPresent()) {
                store.restaurar(s.get());
                LOG.info("Estado restaurado de " + p);
                return true;
            }
        }
        return false;
    }

    /**
     * Apaga o ficheiro de dados e a cópia de segurança. Os dados em memória não mudam
     * e voltam a ser gravados quando o Tomcat encerrar.
     */
    public synchronized boolean eliminar() throws IOException {
        boolean apagou = Files.deleteIfExists(ficheiro);
        Files.deleteIfExists(backup);
        Files.deleteIfExists(temporario);
        return apagou;
    }

    public boolean existe() {
        return Files.exists(ficheiro);
    }

    public Path getFicheiro() {
        return ficheiro;
    }

    public Optional<Instant> ultimaGravacao() {
        try {
            return existe() ? Optional.of(Files.getLastModifiedTime(ficheiro).toInstant()) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public long tamanhoBytes() {
        try {
            return existe() ? Files.size(ficheiro) : 0;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Lê um ficheiro .ser. Se estiver estragado ou for de uma versão incompatível, devolve vazio em vez de impedir o arranque.
     */
    private Optional<Snapshot> ler(Path p) {
        if (!Files.exists(p)) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(p)))) {
            return Optional.of((Snapshot) in.readObject());
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            LOG.log(Level.WARNING, "Ficheiro de dados inválido/corrompido: " + p, e);
            return Optional.empty();
        }
    }
}
