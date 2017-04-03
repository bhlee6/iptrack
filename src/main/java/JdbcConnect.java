import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner;

public class JdbcConnect {

    static String username;
    static String password;
    static AccessDatabase db;
    static boolean userQuits = false;
    static String cmd;

    public static void main(String[] argv) throws IOException, ParseException, SQLException, GeoIp2Exception {
        //READ + WRITE STUFF
        Scanner s = new Scanner(System.in);
        System.out.println("Enter filename to parse: ");
        String userInput = s.nextLine();
        ReadWrite rw = new ReadWrite(userInput);
        File fileToUpload = rw.logdata;
        String fileName = fileToUpload.getName();

        System.out.println("File successfully parsed.");
        System.out.println(fileName + " has been created.");
        System.out.println("Upload file into the database? Enter \"y\" to upload or any other key to cancel.");
        String parseInput = s.nextLine();
        if (parseInput.equals("y")) {

            System.out.println("Enter MySQL username:");
            username = s.nextLine();

            System.out.println("Enter password:");
            password = s.nextLine();

            System.out.println("Enter database name to upload to:");
            String dbName = s.nextLine();

            db = new AccessDatabase();
            db.getConnection(username, password); //Get DB Connection
            db.createDB(dbName); //Create DB with the provided name
            db.getConnection(username, password); //Regrab connection to newly created DB
            db.createTable(); //Create the table

            //
            importToSQL(fileToUpload, db);

            queryInstructions();

            do {
                cmd = s.nextLine();
                if ((cmd == "") | (cmd == null)) {
                    System.out.println("Invalid command. Try with a valid command.");
                }
                else {
                runNextFunction(argParser(cmd));
                }
            } while (!userQuits);
            //Close statement and connections
            db.close();
        } else {
            System.out.println("Nothing to upload");
        }
        s.close();
    }

    private static void importToSQL(File fileToUpload, AccessDatabase db) throws IOException {
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

    public static void runNextFunction(String[] command) throws SQLException, IOException, GeoIp2Exception {
        String first = command[0];
        if (command.length == 1) {
            if (first.toLowerCase().equals("exit")) {
                userQuits = true;
            }
            else System.out.println("Invalid command. Try with a valid command.");
        } else if (command.length > 1) {
            String second = command[1];
            if (first.equals("1")) {
                db.numAttempts(Integer.parseInt(second));
            } else if (first.equals("2")) {
                LocationOfIP loc = new LocationOfIP();
                loc.findLocation(second);
            } else System.out.println("Invalid command. Try with a valid command.");
        }
    }

    public static void queryInstructions() {
        System.out.println("Enter a command to query the database:");
        System.out.println("Example: 1)10 will perform the first query with 10 as the argument");
        System.out.println("1. Find the number of failed login attempts for the first \"x\" number ip addresses.");
        System.out.println("2. Find the location of an IP address. Example: 2)177.125.243.163");
        System.out.println("Exit: Exit the application");
    }

    public static String[] argParser(String s) {
        String[] tokens = s.split("\\)");
        return tokens;
    }

}