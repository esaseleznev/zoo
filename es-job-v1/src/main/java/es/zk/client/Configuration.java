package es.zk.client;

import java.util.List;

public class Configuration {

    private Connection zkConnection;

    Boolean getBooleanProperty(String key) {
        return null;
    }
    Long getLongProperty(String key) {
        return null;
    }

    String getStringProperty(String key) {
        return null;
    }

    List<String> getListOfPropeties(String key) {
        return null;
    }

    public Connection getZkConnection() {
        return zkConnection;
    }

    public void setZkConnection(Connection zkConnection) {
        this.zkConnection = zkConnection;
    }
}
