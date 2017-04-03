import com.mysql.jdbc.PreparedStatement;

import java.sql.*;


public class AccessDatabase {

    static String driver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/";
    static Connection conn = null;
    static Statement stmt = null;
    private ResultSet resultSet = null;
    public static String dbName = "";
    static String tableName = "attempt";
    static boolean dbIsCreated = false;


    public void getConnection(String username, String password)
            throws SQLException{
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            if (dbIsCreated)
                conn = DriverManager.getConnection(url, username, password);
            else
                conn = DriverManager.getConnection(url, username, password);
            //return conn;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
       // return conn;
    }


    public static void createDB(String dbname) throws SQLException {
        dbName = dbname;

        String dbCreate = "CREATE DATABASE IF NOT EXISTS " + dbname;
        try {
            stmt = conn.createStatement();
            stmt.execute(dbCreate);
            System.out.println("DB " + dbname + " is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //DB is now created, set dbIsCreated to true, and add db to the URL
        dbIsCreated = true;
        url = url + dbname;
    }


    public static void createTable() throws SQLException {

        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (user VARCHAR(80),"
                + " protocol VARCHAR(30),"
                + " ipaddress VARCHAR(45),"
                + " date DATETIME,"
                + " eventlength VARCHAR(20))";
        try {
            stmt=conn.createStatement();
            stmt.execute(sqlCreate);
            System.out.println("Table attempt is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void insertLineIntoTable(String line) throws SQLException {
        String insertTableSQL = formatLineForSQL(line);
        try {
            System.out.println(insertTableSQL);
            stmt = conn.createStatement();
            // execute insert SQL stetement
            stmt.executeUpdate(insertTableSQL);
            // STRING TO PRINT OUT INSERTS
          //  System.out.println("Record is inserted into " + tableName + " table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }


    public static String formatLineForSQL(String line) {
        String[] tokens = line.split(",");
        StringBuilder sb = new StringBuilder();

        if (tokens.length >= 1) {
            for (int i = 0; i < tokens.length - 1; i++) {
                String s = "'" + tokens[i] + "'";
                sb.append(s);
                sb.append(",");
            }
            sb.append("'"+ tokens[tokens.length-1] + "'");
        }

        return "INSERT INTO " + tableName
                + "(user, protocol, ipaddress, date, eventlength) "
                + "VALUES"
                + "(" + sb + ")";
    }

    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (stmt != null) {
                stmt.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        }
    }


    //////////////////////////////////////////////////////QUERIES//////////////////////


    /**
     * Number of failed login attempts from a specific ip address
     * @param num the max number of items to return
     * @throws Exception
     */
    public void numAttempts(int num) throws SQLException {
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("select ipaddress, count(*) as cnt from " + dbName + ".attempt " +
                    "group by ipaddress order by cnt desc limit " + num);
            System.out.println("IP addresses with the most number of attempted logins");
            while (resultSet.next()) {
                String ipaddress = resultSet.getString("ipaddress");
                int count = resultSet.getInt("cnt");

                System.out.println(String.format(
                        "ipaddress: %5s  count: %5d", ipaddress, count));
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
