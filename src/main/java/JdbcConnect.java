import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class JdbcConnect {

    private AccessDatabase db;
    private String dbName;

    //MySQL Credentials
    private String username;
    private String password;
    private String host;
    private String port;
    Path credentialsPath;

    //Files to be added to the database
    ArrayList<File> csvFiles = new ArrayList<>();

    //Starts false, user is still using the application
    //Used for currrent iteration of querying (TO BE REPLACED)
    private boolean userQuits = false;

    //Interface Used to help pass which method for generating input into database
    interface I {
        void myMethod() throws IOException, ParseException, GeoIp2Exception;
    }

    /**
     * Primary run function that handles database setup and importing the log information to the database
     * @param methodInterface method to use determined on whether the input is stdin or file
     *
     */
    void run(I methodInterface) throws IOException, ParseException, SQLException, GeoIp2Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("Ensure MySQL has an active connection.");
        setMySQLCredentials(); //Retrieve MySQL credentials from properties file

        db = new AccessDatabase();

        //Create the database name and table with the provided SQL credentials
        createDBandTable(db);

        //Import the given input to the database
        methodInterface.myMethod();

        //Basic instructions for querying. WILL BE REPLACED.
        //Current iteration only for testing.
        queryInstructions();
        userQuery(s);

        //Close statement and connections
        db.close();
        s.close();
    }

    /**
     * Imports the lastb log information into the database when it is provided as stdin
     */
    public void importFromStdin() throws IOException, GeoIp2Exception, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        importFromReader(br);
    }

    public void importFromReader(BufferedReader br) throws IOException, GeoIp2Exception, ParseException {
        String line;
        br.readLine();
        try {
            while ((line = br.readLine()) != null && line.length() != 0) {
                db.insertLineIntoTable(line);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Imports the information in a log file to the database.
     *
     * @param fileToUpload given log file
     * @param db           the given AccessDatabase
     */
    public void importSingleFileToSQL(File fileToUpload, AccessDatabase db) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToUpload));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    //Insert each line into the database
                    db.insertLineIntoTable(line);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Imports all the information in all log files in csvFiles to the database
     *
     */
    public void importAllFilesToSQL() throws IOException {
        for (File f : csvFiles) {
            importSingleFileToSQL(f, db);
        }
    }


    //////////////////////////////////////////////////////
    //USER COMMANDS BASIC
    //////////////////////////////////////////////////////


    public void runNextFunction(String[] command) throws SQLException, IOException, GeoIp2Exception {
        String first = command[0];
        if (command.length == 1) {
            //User wants to exit the application
            if (first.toLowerCase().equals("exit")) {
                userQuits = true;
            } else System.out.println("Invalid command. Try with a valid command.");
        } else if (command.length > 1) {
            String second = command[1];
            if (first.equals("1")) {
                db.numAttempts(Integer.parseInt(second));
            } else System.out.println("Invalid command. Try with a valid command.");
        }
    }

    public void queryInstructions() {
        System.out.println("Enter a command to query the database:");
        System.out.println("Example: 1)10 will perform the first query with 10 as the argument");
        System.out.println("1. Find the number of failed login attempts for the first \"x\" number ip addresses.");
        System.out.println("Exit: Exit the application");
    }

    public void userQuery(Scanner s) throws GeoIp2Exception, SQLException, IOException {
        do {
            String cmd = s.nextLine();
            if (cmd.equals("")) {
                System.out.println("Invalid command. Try with a valid command.");
            } else {
                runNextFunction(argParser(cmd));
            }
        } while (!userQuits);
    }

    public String[] argParser(String s) {
        return s.split("\\)");
    }


    //////////////////////////////////////////////////////
    //SQL CREDENTIALS
    //////////////////////////////////////////////////////


    /**
     * Sets the database credentials for this class from a properties file
     */
    private void setMySQLCredentials() {
        LoadDBProperties loadedProp = new LoadDBProperties();
        loadedProp.loadFrom(credentialsPath);
        DBProperties DBProp = loadedProp.getCurrentProperties();

        dbName = DBProp.getDatabase();
        username = DBProp.getUser();
        password = DBProp.getPassword();
        host = DBProp.getHost();
        port = DBProp.getPort();
    }

    /**
     * @param host host to be used in the url to connect to the db
     * @param port port to be used in the url to connect to the db
     * @return url to be used for mysql database connection
     */
    public String getURL(String host, String port) {
        return "jdbc:mysql://" + host + ":" + port + "/";
    }

    /**
     * Creates the a database based on the given database name, and creates a table in the database to store
     * the log information
     *
     * @param db the given AccessDatabase object
     */
    public void createDBandTable(AccessDatabase db) throws SQLException {
        db.url = getURL(host, port); //Set db url
        db.getConnection(username, password); //Get DB Connection
        db.createDB(dbName); //Create DB with the provided name
        db.getConnection(username, password); //Regrab connection to newly created DB
        db.createTable(); //Create the table
    }
}