#!/usr/bin/env bash
# Instala Tomcat 10.1 na porta 8081 como serviço systemd e publica o AgroGest.
# Testado em Ubuntu/Debian. Uso:  sudo ./deploy/instalar.sh [caminho/para/agrogest.war]
set -euo pipefail

TOMCAT_VERSAO="10.1.34"
TOMCAT_DIR="/opt/tomcat"
DADOS_DIR="/var/lib/agrogest"
PORTA="8081"
WAR="${1:-$(dirname "$0")/../dist/agrogest.war}"
AQUI="$(cd "$(dirname "$0")" && pwd)"

[ "$(id -u)" -eq 0 ] || { echo "Execute com sudo."; exit 1; }
[ -f "$WAR" ] || { echo "WAR não encontrado: $WAR (corra 'mvn clean package' primeiro)"; exit 1; }

echo ">> Java 17"
command -v java >/dev/null || { apt-get update && apt-get install -y openjdk-17-jdk-headless; }
JAVA_HOME_REAL="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"

echo ">> Utilizador de sistema 'tomcat'"
id tomcat >/dev/null 2>&1 || useradd -r -m -U -d "$TOMCAT_DIR" -s /bin/false tomcat

if [ ! -x "$TOMCAT_DIR/bin/startup.sh" ]; then
    echo ">> A descarregar Tomcat $TOMCAT_VERSAO"
    TMP="$(mktemp -d)"
    curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSAO}/bin/apache-tomcat-${TOMCAT_VERSAO}.tar.gz" \
        | tar xz -C "$TMP"
    mkdir -p "$TOMCAT_DIR"
    cp -r "$TMP/apache-tomcat-${TOMCAT_VERSAO}/." "$TOMCAT_DIR/"
    rm -rf "$TMP" "$TOMCAT_DIR"/webapps/{docs,examples,host-manager,manager}
fi

echo ">> Porta HTTP $PORTA"
sed -i "s/port=\"8080\"/port=\"$PORTA\"/" "$TOMCAT_DIR/conf/server.xml"

echo ">> Pasta de dados $DADOS_DIR"
mkdir -p "$DADOS_DIR"
chown -R tomcat:tomcat "$TOMCAT_DIR" "$DADOS_DIR"
chmod +x "$TOMCAT_DIR"/bin/*.sh

echo ">> Serviço systemd"
sed "s#/usr/lib/jvm/java-17-openjdk-amd64#$JAVA_HOME_REAL#" "$AQUI/tomcat.service" > /etc/systemd/system/tomcat.service
systemctl daemon-reload
systemctl enable tomcat

echo ">> Deploy do WAR"
systemctl stop tomcat || true
rm -rf "$TOMCAT_DIR/webapps/agrogest" "$TOMCAT_DIR/work/Catalina/localhost/agrogest"
install -o tomcat -g tomcat -m 644 "$WAR" "$TOMCAT_DIR/webapps/agrogest.war"
systemctl start tomcat

echo
echo "Pronto: http://$(hostname -I | awk '{print $1}'):$PORTA/agrogest/"
echo "Estado: systemctl status tomcat   |   Logs: journalctl -u tomcat -f  /  $TOMCAT_DIR/logs/catalina.out"
