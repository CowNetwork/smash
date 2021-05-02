FROM ghcr.io/cownetwork/paperspigot:1.16.5-631

COPY ${PWD}/run/plugins/smash/ /opt/spigot/plugins/smash/
COPY ${PWD}/run/configs/ops.json /opt/spigot/ops.json
COPY ${PWD}/run/configs/server.properties /opt/spigot/server.properties
COPY ${PWD}/target/smash-1.0.0.jar /opt/spigot/plugins/smash-1.0.0.jar
COPY noma-spigot-1.0.0.jar /opt/spigot/plugins/noma-spigot-1.0.0.jar
COPY spigot-extensions-1.0.0.jar /opt/spigot/plugins/spigot-extensions-1.0.0.jar
COPY messages-spigot-1.0.0.jar /opt/spigot/plugins/messages-spigot-1.0.0.jar