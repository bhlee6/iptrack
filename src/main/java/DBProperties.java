/**
 * DBProperties is a class to hold the information regarding the database credentials to ensure proper
 * connection to the database
 */
public class DBProperties {
    private String database;
    private String user;
    private String password;
    private String host;
    private String port;

    /**
     * @param database The database name
     * @param user username for the database
     * @param password password for the database
     * @param host the host
     * @param port the port number
     */
    DBProperties(String database, String user, String password, String host, String port) {
        this.database = database;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    String getDatabase() {
        return database;
    }

    public void setDatabase(String db) {
        this.database = db;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    String getPassword() {
        return password;
    }

    public void setPassword(String pw) {
        this.password = pw;
    }

    String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
