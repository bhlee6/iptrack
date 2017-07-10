import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;

class JdbcConnect {

    private AccessDatabase db;
    private String dbName;

    //MySQL Credentials
    private String username;
    private String password;
    private String host;
    private String port;
    private Path credentialsPath;
    private Path ipDbPath;
    private String linuxCommand;
    private static boolean loading = true;

    //Files to be added to the database
    private ArrayList<File> filesToUpload = new ArrayList<>();

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

            //Set up Access Database
            db = new AccessDatabase();
            db.setIpDbPath(ipDbPath);
            db.setupLocationDbReader();

            //Create the database name and table with the provided SQL credentials
            createDBandTable(db);

            progressBar();

            //Import the given input to the database
            methodInterface.myMethod();


            //Finished importing data, loading is now false
            loading = false;

            //Close DB connections
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
    void importFromCmd() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",linuxCommand});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            importFromReader(br); }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Imports the lastb log information into the database when it is provided as stdin
     */
    void importFromStdin() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        importFromReader(br);
    }


    private void importFromReader(BufferedReader br) {
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
    private void importSingleFileToSQL(File fileToUpload, AccessDatabase db) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToUpload));
            String line;
            //db.conn.setAutoCommit(false);
            String insertStmt = db.createInsertStatement();
            db.setPstmt(db.conn.prepareStatement(insertStmt));
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
    void importAllFilesToSQL() {
        for (File f : filesToUpload) {
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
    private String getURL(String host, String port) {
        return "jdbc:mysql://" + host + ":" + port;
    }

    /**
     * Creates the a database based on the given database name, and creates a table in the database to store
     * the log information
     *
     * @param db the given AccessDatabase object
     */
    private void createDBandTable(AccessDatabase db) {
        String sslFalse = "?useSSL=false";
        db.setUrl(getURL(host, port) + sslFalse); //Set db url
        db.getConnection(username, password); //Get DB Connection
        db.createDB(dbName); //Create DB with the provided name
        db.setUrl(getURL(host, port) +"/"+ dbName + sslFalse); //Reset db url
        db.getConnection(username, password); //Regrab connection to newly created DB
        db.createTable(); //Create the table
    }


    /**
     * Bar to display while running inserts
     */
    private static synchronized void progressBar() throws IOException, InterruptedException {
        System.out.println("Importing data...");
        Thread thread = new Thread(() -> {
            try {
                while(loading) {
                    System.out.write("-".getBytes());
                    Thread.sleep(500);
                }
                System.out.write("Done Inserting \r\n".getBytes());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    void setCredentialsPath(Path p) {
        this.credentialsPath = p;
    }
    void setIpDbPath(Path p) {
        this.ipDbPath = p;
    }
    void setLinuxCommand(String cmd) {
        this.linuxCommand = cmd;
    }
    void addFileToUpload(File f) {
        filesToUpload.add(f);
    }
}