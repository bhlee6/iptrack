import java.sql.*;


@SuppressWarnings("ALL")
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


    /**
     * Creates a Database on the SQL server with the given name
     * @param dbname Name of the database to be created
     * @throws SQLException
     */
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


    /**
     * Creates a table named "attempt" in the SQL database.  The table contains the following:
     * user: the username of the attempted logger
     * protocol: protocol used
     * ipaddress: the ip address used for the attempt
     * date: date and time of the attempted login
     * eventlength: length of the attempted login
     * @throws SQLException
     */
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


    /**
     * Given a CSV line, formats the line into a valid SQL insertion, and inserts the data into the SQL table
     * @param line  String line containing the attempted login information
     * @throws SQLException
     */

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


    /**
     * Parses the given line, and returns a String that is valid for an entry into the SQL table
     * @param line String of the attempted login (csv)
     * @return
     */
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

    /**
     * Closes the statement, connection, and resultSet
     */
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
