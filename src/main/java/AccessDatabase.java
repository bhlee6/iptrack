
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;


/**
 * AccessDatabase is a class that is mainly used for database methods and queries
 */
@SuppressWarnings("ALL")
public class AccessDatabase {


    String driver = "com.mysql.jdbc.Driver";
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet resultSet = null;
    public  String dbName = "";
    public  String url = "";
    String tableName = "attempt";
    boolean dbIsCreated = false;
    public Path ipDbPath;
    public LocationOfIP loc;


    /**
     * Get connection to the database using the username and password provided
     * @param username given db username
     * @param password given db password
     */
    public void getConnection(String username, String password) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (dbIsCreated)
                conn = DriverManager.getConnection(url, username, password);
            else
                conn = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a Database on the SQL server with the given name
     *
     * @param dbname Name of the database to be created
     */
    public void createDB(String dbname) {
        try {
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
     */
    public void createTable() {

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
            e.printStackTrace();
        }
    }


    /**
     * Given a log line, formats the line into a valid SQL insertion, and inserts the data into the SQL table
     *
     * @param line String line containing the attempted login information
     */

    //POSSIBLE UPDATES/TODO BATCH, batch nonfunctionality atm

    public void insertLineIntoTable(String line) {
        try {
            ReadLineGrabRelevant rl = new ReadLineGrabRelevant();
            String[] relevantTokens = rl.collectRelevant(line);

            if (relevantTokens[0] == null){
                System.out.println("\nLine cannot be imported:" + line);
            }

            else {
                ArrayList<String> allTokens = listRelevantTokens(relevantTokens);
                pstmt.setString(1, allTokens.get(0));
                pstmt.setString(2, allTokens.get(1));
                pstmt.setString(3, allTokens.get(2));
                pstmt.setString(4, allTokens.get(3));
                pstmt.setString(5, allTokens.get(4));
                pstmt.setString(6, allTokens.get(5));
                pstmt.setString(7, allTokens.get(6));
                pstmt.setString(8, allTokens.get(7));
                // execute insert SQL stetement
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of the relevant token information, including the location data of an ip address
     * @param tokens String[] containing the relevant tokens
     * @return Array List of the relevant tokens including the proper location data if an ip address is present
     */
    public ArrayList<String> listRelevantTokens(String[] tokens) {
        ArrayList<String> allTokens = new ArrayList();

        if (tokens[2] == null) {
            System.out.println("No ipaddress found");
        }
        else {
            String[] loc = getLocationForSQL(tokens[2]);
            for (String s: tokens) {
                allTokens.add(s);
            }
            for (String sloc: loc) {
                allTokens.add(sloc);
            }
            for (String s: allTokens) {
                doubleUpQuote(s);
            }
        }
        return allTokens;
    }


    /**
     * Returns a String statement of an Insert into SQL table (Following standards for prepared statement)
     *
     * @return Insert into table SQL statement
     */
    public String createInsertStatement() {
        return "INSERT INTO " + tableName
                + "(user, protocol, ipaddress, date, eventlength, city, state, country) "
                + "VALUES"
                + "(?,?,?,?,?,?,?,?)";
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
            //Close the Maxmind database reader
            loc.closeDbReader();

            System.out.println("Closing MySQL and Maxmind database connections.");
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     * Sets up the Maxmind DB reader
     */
    public void setupLocationDbReader() {
        loc = new LocationOfIP();
        loc.setDbPath(ipDbPath);
        loc.buildDbReader();
    }

    /**
     * Gets the location of an ip address
     * @param ipAddress provided as a string
     * @return String[] containing the location of the ip address
     */
    public String[] getLocationForSQL(String ipAddress) {
        String[] locToModify = loc.findFullLocation(ipAddress);
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

    public void setIpDbPath(Path path) {
        this.ipDbPath = path;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }
}


/* QUERIES TO BE IMPLEMENTED ONCE DESIGN FURTHER ESTABLISHED
//////
count the number of times all ipaddress tries to login during each hour

SELECT ipaddress, HOUR(date), COUNT(*) FROM db.attempt
GROUP BY ipaddress, HOUR(date)
order by ipaddress

///SINGULAR 1 IP ADRESS
SELECT HOUR(date), COUNT(*) FROM attempt
where ipaddress = '103.207.39.140'
GROUP BY HOUR(date)
order by HOUR(date)

/////
count the number of times an ipaddress tries to login during each hour each day

SELECT ipaddress, DAY(date), HOUR(date), COUNT(*) FROM db.attempt
GROUP BY ipaddress, DAY(date), HOUR(date)
order by ipaddress

///SINGULAR
SELECT HOUR(date), COUNT(*) FROM attempt
where ipaddress = '103.207.39.140'
GROUP BY HOUR(date)
order by HOUR(date)
///
count where its greater than 2

SELECT ipaddress, DAY(date), HOUR(date), COUNT(*) as cnt FROM db.attempt
GROUP BY ipaddress, DAY(date), HOUR(date)
having cnt > 2
order by ipaddress



///////////UPDATED PHP QUERIES///////////
$mostFrequentIp = $db -> select(
	"select ipaddress, country, count(*) as occurrences
	 from attempt group by ipaddress order by occurrences desc limit " . $cnt,
	"Most attempts from IP Address:");

	$mostFrequentCountry = $db -> select(
	"select country, count(*) as occurrences from attempt group by country order by occurrences desc limit " . $cnt,
	"Attempted Logins from each Country");

	$mostIpPerCountry = $db -> select(
	"select country, count(distinct ipaddress) as cnt from attempt group by country order by cnt desc limit " . $cnt,
	"Countries with the most number of Unique IPs");

	$mostIpPerCity = $db -> select(
	"select city, country, count(*) as occurrences from attempt group by city order by occurrences desc limit " . $cnt,
	"Cities with the most number of IPs");

	$ipWithMostUsernames = $db -> select(
	"select ipaddress, country, COUNT(distinct user) as count from attempt group by ipaddress order by count desc limit ".$cnt,
	"IPs with the most number of Usernames");

	$attemptsEachHour =  $db -> select(
	"SELECT HOUR(date), COUNT(*) FROM attempt GROUP BY HOUR(date)",
	"Total attempts per hour");

	$uniqueUsernames = $db -> select(
	"select user, COUNT(distinct ipaddress) as cnt from attempt group by user order by cnt desc limit ".$cnt,
	"Number of times a username was used with a unique ipaddress"
	);

	$totalAttemptPerHour = $db -> select (
	"SELECT ipaddress, country, HOUR(date), COUNT(*) FROM attempt GROUP BY ipaddress, HOUR(date) order by ipaddress limit ".$cnt,
	"Total attempts per hour for each IP");

	$numberOfUniqueUsernames = $db -> select(
	"select ipaddress, country, COUNT(distinct user) as cnt from attempt group by ipaddress order by cnt desc limit ".$cnt,
	"Number of Unique Usernames for each IP");

	$numberOfAttemptsSameUsername = $db -> select(
	"select user, ipaddress, country, count(*) as cnt from attempt group by user, ipaddress order by cnt desc limit ".$cnt,
	"Number of Attempts an IP Address used the Same Username");

	//User provided IP Address


	$totalAttemptsPerHour = $db -> select(
	"SELECT HOUR(date), COUNT(*) FROM attempt where ipaddress = '".$userGivenIp."' GROUP BY HOUR(date) order by HOUR(date)",
	"Total Attempts for a Given IP per Hour");

	$attemptsPerHourPerDay = $db -> select(
	"SELECT HOUR(date), COUNT(*) FROM attempt where ipaddress = '".$userGivenIp."' GROUP BY HOUR(date) order by HOUR(date)",
	"Attempts for a Given IP at each hour each day");

*/
