import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class JdbcConnect {

    static AccessDatabase db;
    static String cmd;
    static String path;
    static String dbName;
    static File fileToUpload;

    //MySQL Credentials
    static String username;
    static String password;
    static String host;
    static String port;

    static ArrayList<File> csvFiles = new ArrayList<>();

    static boolean userQuits = false; //Starts false, user is still using the application


    public static void main(String[] argv) throws IOException, ParseException, SQLException, GeoIp2Exception {
        //Read the given file, and write a new file containing the data as a csv
        Scanner s = new Scanner(System.in);
        System.out.println("Ensure MySQL has an active connection.");

        enterFileOrPath(s);

        System.out.println("Enter path to file with db credentials:");
        path = s.nextLine();
        setMySQLCredentials(path); //Retrieve MySQL credentials from properties file

        db = new AccessDatabase();
        createDBandTable(db);

        //Import the file to the database
        importAllFilesToSQL(db);

        //Basic instructions for querying
        queryInstructions();
        userQuery(s);

        //Close statement and connections
        db.close();
        s.close();
    }

    public static void importSingleFileToSQL(File fileToUpload, AccessDatabase db) throws IOException {
        try {
            BufferedReader parsedText = new BufferedReader(new FileReader(fileToUpload));
            String line;
            parsedText.readLine();
            try {
                while ((line = parsedText.readLine()) != null) {
                    db.insertLineIntoTable(line);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //OVERLOADING importToSQL for Multiple files
    public static void importAllFilesToSQL(AccessDatabase db) throws IOException {
        for (File f: csvFiles) {
            importSingleFileToSQL(f, db);
        }
    }

    public static void runNextFunction(String[] command) throws SQLException, IOException, GeoIp2Exception {
        String first = command[0];
        if (command.length == 1) {
            //User wants to exit the application
            if (first.toLowerCase().equals("exit")) {
                userQuits = true;
            }
            else System.out.println("Invalid command. Try with a valid command.");
        } else if (command.length > 1) {
            String second = command[1];
            if (first.equals("1")) {
                db.numAttempts(Integer.parseInt(second));
            } else System.out.println("Invalid command. Try with a valid command.");
        }
    }

    public static void queryInstructions() {
        System.out.println("Enter a command to query the database:");
        System.out.println("Example: 1)10 will perform the first query with 10 as the argument");
        System.out.println("1. Find the number of failed login attempts for the first \"x\" number ip addresses.");
        System.out.println("Exit: Exit the application");
    }

    public static void userQuery(Scanner s) throws GeoIp2Exception, SQLException, IOException {
        do {
            cmd = s.nextLine();
            if ((cmd == "") | (cmd == null)) {
                System.out.println("Invalid command. Try with a valid command.");
            }
            else {
                runNextFunction(argParser(cmd));
            }
        } while (!userQuits);
    }

    public static String[] argParser(String s) {
        String[] tokens = s.split("\\)");
        return tokens;
    }

    public static void setMySQLCredentials(String path) {
        LoadDBProperties loadedProp = new LoadDBProperties();
        loadedProp.loadFrom(path);
        DBProperties DBProp = loadedProp.getCurrentProperties();

        dbName = DBProp.getDatabase();
        username = DBProp.getUser();
        password = DBProp.getPassword();
        host = DBProp.getHost();
        port = DBProp.getPort();
    }

    public static String getURL(String host, String port) {
        String url ="jdbc:mysql://"+host+":"+port+"/";
        return url;
    }

    public static void createDBandTable(AccessDatabase db) throws SQLException {
        db.url = getURL(host, port); //Set db url
        db.getConnection(username, password); //Get DB Connection
        db.createDB(dbName); //Create DB with the provided name
        db.getConnection(username, password); //Regrab connection to newly created DB
        db.createTable(); //Create the table
    }

    public static void createParsedFile(File file) throws IOException, ParseException {
        ReadLogWriteCSV rw = new ReadLogWriteCSV(file);
        fileToUpload = rw.logdata;
        csvFiles.add(fileToUpload);
        String fileName = fileToUpload.getName();
        System.out.println("File successfully parsed.");
        System.out.println(fileName + " has been created.");
    }

    //If File is selected, parse file, if a directory/folder is selected, parse all files in the given directory

    public static void checkFileOrFolder(String path, Scanner s) throws IOException, ParseException {
        try {
            File file = new File(path);
            boolean isFile = file.isFile(); // Check for regular file
            boolean isDirectory = file.isDirectory(); //Check for directory

            if (isFile) {
                createParsedFile(file);
            }
            else if (isDirectory) {
                File[] fileList = file.listFiles();
                System.out.println("You have selected a directory. Creating CSV files for all files in" +
                        "directory. Proceed? (y/n)");
                String proceed = s.nextLine();
                if (proceed.equals("y")) {
                for (File f: fileList) {
                    createParsedFile(f);
                }
                } else {
                    enterFileOrPath(s);
                }
            }
            else { System.out.println("File does not exist or invalid path.");
            enterFileOrPath(s);
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void enterFileOrPath(Scanner s) throws IOException, ParseException {
        System.out.println("Enter filename to parse: ");
        String userInput = s.nextLine();
        checkFileOrFolder(userInput, s);
    }
}