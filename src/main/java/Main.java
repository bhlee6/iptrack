import clitools.CLIParameters;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Main Class to run the program
 */
public class Main {

    //The given command line input arguments
    private final CLIParameters inputArgs = new CLIParameters();
    private static JdbcConnect jc;

    public static void main(String[] args) throws GeoIp2Exception, SQLException, ParseException, IOException {
        Main trackip = new Main();
        jc = new JdbcConnect();
        trackip.handleInputArgs(args);
    }

    private void handleInputArgs(String args[]) throws SQLException, IOException, ParseException, GeoIp2Exception {
        JCommander jCommander = new JCommander(inputArgs);
        jCommander.setProgramName("trackip");

        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            showUsage(jCommander);
        }
        //The user given path to properties file is passed to the credentials path
        jc.credentialsPath = inputArgs.propertiesPath;

        //The user given path to Geolite2 database is passed to the database path
        jc.ipDb = inputArgs.ipDb;

        //When user invokes help command
        if (inputArgs.isHelp()) {
            showUsage(jCommander);
        }
        //If the data is provided through stdin
        if (inputArgs.stdin) {
            runFromStdin();
        }

        //If the data is provided through stdin and a given lastb command
        if (inputArgs.stdincmd != null) {
            jc.cmd = inputArgs.stdincmd;
            runFromCmd();
        }
        //If the data is provided through files and/or directories
        else {
            if (inputArgs.file != null) {
                System.out.println("Uploading Given File:" + inputArgs.file.getFileName());
                jc.csvFiles.add(inputArgs.file.toFile());
            }
            if (inputArgs.files.size() != 0) {
                StringBuilder sb = new StringBuilder();
                for (String s: inputArgs.files) {
                    File f = new File(s);
                    jc.csvFiles.add(f);
                    sb = sb.append(f.getName()).append(" ");
                }
                System.out.println("Uploading Given Files:" + sb.toString());
            }
            if (inputArgs.directory != null) {
                System.out.println("Uploading Given Files in directory...");
                for (File f: inputArgs.directory.toFile().listFiles()) {
                    System.out.println("Found file:" + f.getName());
                    jc.csvFiles.add(f);
                }
            }
            runFromFiles();
        }
    }

    /**
     * Displays the usage of the program.  Exits after.
     * @param jCommander Given JCommander
     */
    private void showUsage(JCommander jCommander) {
        jCommander.usage();
        System.exit(0);
    }

    /**
     * Runs the run method in JdbcConnect when the command line input is from stdin
     */
    private void runFromStdin() {
        jc.run(jc::importFromStdin);
    }


    /**
     * Runs the run method in JdbcConnect when the command line input is from stdin
     */
    private void runFromCmd() {
        jc.run(jc::importFromCmd);
    }

    /**
     * Runs the run method in JdbcConnect when the command line arguments are files
     */
    private void runFromFiles() {
        jc.run(jc::importAllFilesToSQL);
    }
}
