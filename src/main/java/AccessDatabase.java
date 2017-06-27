import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;


/**
 * AccessDatabase is a class that is mainly used for database methods and queries
 */
@SuppressWarnings("ALL")
public class AccessDatabase {

    String driver = "com.mysql.jdbc.Driver";
    Connection conn = null;
    Statement stmt = null;
    ResultSet resultSet = null;
    public  String dbName = "";
    public  String url = "";
    String tableName = "attempt";
    boolean dbIsCreated = false;


    /**
     * Get connection to the database using the username and password provided
     * @param username given db username
     * @param password given db password
     * @throws SQLException
     */
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

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Creates a Database on the SQL server with the given name
     *
     * @param dbname Name of the database to be created
     * @throws SQLException
     */
    public void createDB(String dbname) throws SQLException {
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
     * city: city location of the ip (least specific subdivision)
     * state: state location of the ip (less specific subdivision, if there is no state in the country,
     * the city is provided)
     * country: country location of the ip
     *
     * @throws SQLException
     */
    public void createTable() throws SQLException {

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
     * Given a log line, formats the line into a valid SQL insertion, and inserts the data into the SQL table
     *
     * @param line String line containing the attempted login information
     * @throws SQLException
     */

    //POSSIBLE UPDATES/TODO BATCH, batch nonfunctionality atm

    public void insertLineIntoTable(String line)
            throws SQLException, IOException, GeoIp2Exception, ParseException {
        ReadLineGrabRelevant rl = new ReadLineGrabRelevant();
        String[] relevantTokens = rl.collectRelevant(line);
        String insertTableSQL = formatLineForSQL(relevantTokens);
        try {
            System.out.println(insertTableSQL);
            stmt = conn.createStatement();
            // execute insert SQL stetement
            stmt.executeUpdate(insertTableSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }


    /**
     * Adds single quotes around each of the tokens to be added to the sql statements,
     * and returns a String that is valid for an entry into the SQL table
     *
     * @param line String of the attempted login
     * @return String SQL insertion statement of the log in attempt
     */
    public String formatLineForSQL(String[] tokens) throws IOException, GeoIp2Exception {
        String[] loc = getLocationForSQL(tokens[2]);

        //Add single quotes around each of the tokens to be added to the SQL insertion statement
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
     * Given a String array, returns a String[] of each of the values surrounded
     * by single quotes
     *
     * @param sarray Given String array
     * @return String[] containing each value from the array with single quotes surrounding the value
     */
    public String[] addSingleQuotes(String[] sarray) {
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
    public String createInsertStatement(String s) {
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


    /**
     * Gets the location of an ip address
     * @param ipAddress provided as a string
     * @return String[] containing the location of the ip address
     * @throws IOException
     * @throws GeoIp2Exception
     */
    public String[] getLocationForSQL(String ipAddress) throws IOException, GeoIp2Exception {
        LocationOfIP loc = new LocationOfIP();
        String[] locToModify = loc.findFullLocation(ipAddress);
        for (int i = 0; i < locToModify.length; i++) {
            locToModify[i] = doubleUpQuote(locToModify[i]);
        }
        return locToModify;
    }

    /**
     * Doubles single quotes in a String.
     * Function is primarily here to prevent insertion errors into the SQL database where a single quote
     * can cause errors
     * @param s a Given String
     * @return a String where all instances of a single quote are replaced with double single quotes
     */
    public String doubleUpQuote(String s) {
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
select city, country, count(*) as occurrences from attempt group by city order by occurrences desc limit 10


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
