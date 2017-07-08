import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;

public class JdbcConnect {

    private AccessDatabase db;
    private String dbName;

    //MySQL Credentials
    private String username;
    private String password;
    private String host;
    private String port;
    public Path credentialsPath;
    public Path ipDb;
    public String cmd;
    private static boolean loading = true;
    private String sslFalse = "?useSSL=false";

    //Files to be added to the database
    ArrayList<File> csvFiles = new ArrayList<>();

    //Interface Used to help pass which method for generating input into database
    interface I {
        void myMethod() throws GeoIp2Exception, ParseException, IOException;
    }

    /**
     * Primary run function that handles database setup and importing the log information to the database
     *
     * @param methodInterface method to use determined on whether the input is stdin or file
     */
    void run(I methodInterface) {
        try {

            System.out.println("Ensure MySQL has an active connection.");
            setMySQLCredentials(); //Retrieve MySQL credentials from properties file

            db = new AccessDatabase();
            db.ipDbPath = ipDb;

            //Create the database name and table with the provided SQL credentials
            createDBandTable(db);

            progressBar("Importing data...");

            //Import the given input to the database
            methodInterface.myMethod();

            //Close statement and connections
            loading = false;
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////
    //IMPORT METHODS
    //////////////////////////////////////////////////////

    /**
     * Imports the lastb log information into the database when it is provided as stdin from given lastb command
     */
    public void importFromCmd() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",cmd});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            importFromReader(br); }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Imports the lastb log information into the database when it is provided as stdin
     */
    public void importFromStdin() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        importFromReader(br);
    }


    public void importFromReader(BufferedReader br) {
        try {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null && line.length() != 0) {
                if (line.trim().length() > 0)
                    db.insertLineIntoTable(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports the information in a log file to the database.
     *
     * @param fileToUpload given log file
     * @param db           the given AccessDatabase
     */
    public void importSingleFileToSQL(File fileToUpload, AccessDatabase db) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToUpload));
            String line;
            //db.conn.setAutoCommit(false);
            String insertStmt = db.createInsertStatement();
            db.pstmt = db.conn.prepareStatement(insertStmt);
            System.out.println("Inserting each log in attempt into the database...");
            while ((line = br.readLine()) != null) {
                //Insert each line into the database
                if (line.trim().length() > 0) {
                    db.insertLineIntoTable(line);
                }
            }
            //  db.conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports all the information in all log files in csvFiles to the database
     */
    public void importAllFilesToSQL() {
        for (File f : csvFiles) {
            importSingleFileToSQL(f, db);
        }
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
        return "jdbc:mysql://" + host + ":" + port;
    }

    /**
     * Creates the a database based on the given database name, and creates a table in the database to store
     * the log information
     *
     * @param db the given AccessDatabase object
     */
    public void createDBandTable(AccessDatabase db) {
        db.url = getURL(host, port) + sslFalse; //Set db url
        db.getConnection(username, password); //Get DB Connection
        db.createDB(dbName); //Create DB with the provided name
        db.url = getURL(host, port) +"/"+ dbName + sslFalse;
        db.getConnection(username, password); //Regrab connection to newly created DB
        db.createTable(); //Create the table
    }


    /**
     * Bar to display while running inserts
     * @param s String to be printed out
     * @throws IOException
     * @throws InterruptedException
     */
    private static synchronized void progressBar(String s) throws IOException, InterruptedException {
        System.out.println(s);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(loading) {
                        System.out.write("-".getBytes());
                        Thread.sleep(500);
                    }
                    System.out.write("Done Inserting \r\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}