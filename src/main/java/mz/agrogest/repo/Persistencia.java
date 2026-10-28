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
 * Grava/restaura o {@link DataStore} num ficheiro com serialização Java.
 *
 * Tolerância a falhas:
 * <ol>
 *   <li>escreve primeiro para {@code agrogest.ser.tmp} e força o flush para disco (fsync);</li>
 *   <li>a versão anterior passa a {@code agrogest.ser.bak};</li>
 *   <li>o .tmp é renomeado atomicamente para {@code agrogest.ser}.</li>
 * </ol>
 * Se o Tomcat morrer a meio da escrita, o ficheiro principal fica intacto; se o
 * principal estiver corrompido, o arranque tenta a cópia .bak.
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

    public synchronized void gravar(DataStore store) throws IOException {
        Files.createDirectories(ficheiro.getParent());
        Snapshot snapshot = store.snapshot();

        try (FileOutputStream fos = new FileOutputStream(temporario.toFile());
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            out.writeObject(snapshot);
            out.flush();
            fos.getFD().sync();
        }

        if (Files.exists(ficheiro)) {
            Files.copy(ficheiro, backup, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            Files.move(temporario, ficheiro, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporario, ficheiro, StandardCopyOption.REPLACE_EXISTING);
        }
        LOG.info("Estado gravado em " + ficheiro);
    }

    /** @return true se algum ficheiro (principal ou backup) foi carregado com sucesso. */
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

    /** Apaga o ficheiro de dados e o backup (os dados em memória mantêm-se). */
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
