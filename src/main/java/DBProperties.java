public class DBProperties {
    String database;
    String user;
    String password;
    String host;
    String port;

    public DBProperties(String database, String user, String password, String host, String port) {
        this.database = database;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public String getDatabase() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String pw) {
        this.password = pw;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
}
