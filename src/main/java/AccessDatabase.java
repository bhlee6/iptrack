import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;
import java.sql.*;


@SuppressWarnings("ALL")
public class AccessDatabase {

    static String driver = "com.mysql.jdbc.Driver";
    static Connection conn = null;
    static Statement stmt = null;
    private ResultSet resultSet = null;
    public static String dbName = "";
    public static String url = "";
    static String tableName = "attempt";
    static boolean dbIsCreated = false;


    public void getConnection(String username, String password) throws SQLException {
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
     *
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
     * For User simplification, a predetermined naming has been given for the table creation.
     * Creates a table named "attempt" in the SQL database.  The table contains the following:
     * user: the username of the attempted logger
     * protocol: protocol used
     * ipaddress: the ip address used for the attempt
     * date: date and time of the attempted login
     * eventlength: length of the attempted login
     * city: city location of the ip
     * state: state location of the ip
     * country: country location of the ip
     *
     * @throws SQLException
     */
    public static void createTable() throws SQLException {

        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (user VARCHAR(80),"
                + " protocol VARCHAR(30),"
                + " ipaddress VARCHAR(45),"
                + " date DATETIME,"
                + " eventlength VARCHAR(20),"
                + " city VARCHAR(80),"
                + " state VARCHAR(80),"
                + " country VARCHAR(80))";
        try {
            stmt = conn.createStatement();
            stmt.execute(sqlCreate);
            System.out.println("Table attempt is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Given a CSV line, formats the line into a valid SQL insertion, and inserts the data into the SQL table
     *
     * @param line String line containing the attempted login information
     * @throws SQLException
     */

    public static void insertLineIntoTable(String line) throws SQLException, IOException, GeoIp2Exception {
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
     *
     * @param line String of the attempted login (csv)
     * @return
     */
    public static String formatLineForSQL(String line) throws IOException, GeoIp2Exception {
        String[] tokens = line.split(",");
        String[] loc = getLocationForSQL(tokens[2]);

        //Add single quotes around each of the tokens to be added to the SQL table
        String[] logTokens = addSingleQuotes(tokens);
        String[] locTokens = addSingleQuotes(loc);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logTokens.length; i++) {
            sb.append(logTokens[i]);
            sb.append(",");
        }

        if (locTokens.length >= 1) {
            for (int i = 0; i < locTokens.length - 1; i++) {
                sb.append(locTokens[i]);
                sb.append(",");
            }
            sb.append(locTokens[locTokens.length - 1]);
        }
        String s = sb.toString();
        return createInsertStatement(s);
    }

    /**
     * Given a CSV line, splits the line based on a comma, and returns a String[] of each of the values surrounded
     * by single quotes
     *
     * @param line A CSV line
     * @return String[] containing each value from the CSV line with single quotes surrounding the value
     */
    public static String[] addSingleQuotes(String[] sarray) {
        for (int i = 0; i < sarray.length; i++) {
            sarray[i] = "'" + sarray[i] + "'";
        }
        return sarray;
    }

    /**
     * Returns a String statement of an Insert into SQL table
     *
     * @param s String of the values to be added to the SQL Table
     * @return Insert into table SQL statement
     */
    public static String createInsertStatement(String s) {
        return "INSERT INTO " + tableName
                + "(user, protocol, ipaddress, date, eventlength, city, state, country) "
                + "VALUES"
                + "(" + s + ")";
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
            System.out.println(e.getMessage());

        }
    }


    public static String[] getLocationForSQL(String ipAddress) throws IOException, GeoIp2Exception {
        LocationOfIP loc = new LocationOfIP();
        String[] locToModify = loc.findFullLocation(ipAddress);
        for (int i = 0; i < locToModify.length; i++) {
            locToModify[i] = doubleUpQuote(locToModify[i]);
        }
        return locToModify;
    }

    public static String doubleUpQuote(String s) {
        String curr;
        curr = s.replaceAll("'", "''");
        return curr;
    }


    //////////////////////////////////////////////////////QUERIES//////////////////////


    /**
     * Number of failed login attempts from a specific ip address, starting from the highest count
     * @param num the max number of items to return
     * @throws Exception
     */
    public void numAttempts(int num) throws SQLException, IOException, GeoIp2Exception {
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
             System.out.println(e.getMessage());
        }
    }

    /**
     * Number of different usernames an ipAddress has used, starting from the highest count
     * @param num the max number of items to return
     * @throws SQLException
     */
    public void numOfUsernames(int num) throws SQLException {
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("select ipaddress, COUNT(distinct user) as cnt from " + dbName + ".attempt " +
                    "group by ipaddress order by cnt desc limit " + num);
            System.out.println("The number of different usernames for each ipaddress");
            while (resultSet.next()) {
                String ipaddress = resultSet.getString("ipaddress");
                int count = resultSet.getInt("cnt");

                System.out.println(String.format(
                        "ipaddress: %5s  Username count: %5d", ipaddress, count));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /* QUERIES TO BE IMPLEMENTED ONCE DESIGN FURTHER ESTABLISHED
    /////
# of failed login attempts from an ip address

select ipaddress, count(*) as occurrences from db.attempt
group by ipaddress
order by occurrences desc
limit 10


/////
The total number of attempts each hour

SELECT HOUR(date), COUNT(*) FROM db.attempt
GROUP BY HOUR(date)


//////
count the number of times an ipaddress tries to login during each hour

SELECT ipaddress, HOUR(date), COUNT(*) FROM db.attempt
GROUP BY ipaddress, HOUR(date)
order by ipaddress


/////
count the number of times an ipaddress tries to login during each hour each day


SELECT ipaddress, DAY(date), HOUR(date), COUNT(*) FROM db.attempt
GROUP BY ipaddress, DAY(date), HOUR(date)
order by ipaddress

///
count where its greater than 2

SELECT ipaddress, DAY(date), HOUR(date), COUNT(*) as cnt FROM db.attempt
GROUP BY ipaddress, DAY(date), HOUR(date)
having cnt > 2
order by ipaddress


/////

number of times a username was used with a different ipaddress

select user, COUNT(distinct ipaddress) as cnt
from db.attempt
group by user
order by cnt desc


/////
number of different usernames an ipaddress used

select ipaddress, COUNT(distinct user) as cnt
from db.attempt
group by ipaddress
order by cnt desc


///
Count the number of times an ip address uses the same username to login

select user, ipaddress, count(*) as cnt
from db.attempt
group by user, ipaddress
order by cnt desc


//Find the number of attempts from each country
select country, count(*) as occurrences
from db.attempt
group by country
order by occurrences desc


//Find the number of attempts from each city
select city, count(*) as occurrences from db.attempt
 group by city
order by occurrences desc

//Find out which ip address was attempted the most frequently and the country of the ip
select ipaddress, country, count(*) as occurrences from db.attempt
group by ipaddress, country
order by occurrences desc

//Find which countries had the most number of distinct ipaddresses
select country, count(distinct ipaddress) as cnt from db.attempt
group by country
order by cnt desc

     */
}
