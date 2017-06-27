import clitools.CLIParameters;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Main Class to run the program
 */
public class Main {

    //The given command line input arguments
    private final CLIParameters inputArgs = new CLIParameters();
    private static JdbcConnect jc;

    public static void main(String[] args) throws GeoIp2Exception, ParseException, IOException, SQLException {
        Main trackip = new Main();
        jc = new JdbcConnect();
        trackip.handleInputArgs(args);
    }

    private void handleInputArgs(String args[]) throws GeoIp2Exception, ParseException, IOException, SQLException {
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

        //When user invokes help command
        if (inputArgs.isHelp()) {
            showUsage(jCommander);
        }
        //If the data is provided through stdin
        if (inputArgs.stdin) {
            runFromStdin();
        }
        //If the data is provided through files and/or directories
        else {
            if (inputArgs.file != null) {
                System.out.println("Uploading Given File");
                jc.csvFiles.add(inputArgs.file.toFile());
            }
            if (inputArgs.files.size() != 0) {
                System.out.println("Uploading Given Files...");
                for (String s: inputArgs.files) {
                    File f = new File(s);
                    jc.csvFiles.add(f);
                }
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
    private void runFromStdin() throws SQLException, GeoIp2Exception, ParseException, IOException {
        jc.run(jc::importFromStdin);
    }

    /**
     * Runs the run method in JdbcConnect when the command line arguments are files
     */
    private void runFromFiles() throws SQLException, GeoIp2Exception, ParseException, IOException {
        jc.run(jc::importAllFilesToSQL);
    }
}
