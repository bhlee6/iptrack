package clitools;
import com.beust.jcommander.Parameter;
import com.maxmind.geoip2.record.City;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CLIParameters is a class that contains the different parameters that the program can run with.
 */
public class CLIParameters {
    @Parameter(names = {"-h", "--help"},
            help = true,
            description = "Displays help information")
    public boolean help;

    @Parameter(names = {"-f", "--file"},
            validateWith = FileParameterValidator.class,
            converter = PathConverter.class,
            description = "File to be uploaded to the database.")
    public Path file;

    @Parameter(names = {"-fs", "--files"},
            variableArity = true,
            description = "Files to be uploaded to the database.  " +
                    "Multiple files to be uploaded. Do not enter directories.  " +
                    "Example: -fs file1 file2 file3")
    public List<String> files = new ArrayList<>();

    @Parameter(names = {"-d", "--dir"},
            validateWith = DirectoryParameterValidator.class,
            description = "Absolute path to directory containing all files to upload to the database.")
    public Path directory;

    @Parameter(names = {"-stdin", "--stdin"},
            description = "Data to be uploaded to the database is from stdin.")
    public boolean stdin = false;

    @Parameter(names = {"-stdincmd", "--stdincmd"},
            validateWith = CommandValidator.class,
            description = "Data to be uploaded to the database is from stdin using specified command.")
    public String stdincmd;

    @Parameter(names = {"-p", "--properties"},
            required = true,
            validateWith = FileParameterValidator.class,
            converter = PathConverter.class,
            description = "Absolute path to the *.properties file that contains the database credential.")
    public Path propertiesPath;


    @Parameter(names = {"-db", "--db"},
            required = true,
            validateWith = FileParameterValidator.class,
            converter = PathConverter.class,
            description = "Absolute path to the 'GeoLite2-City.mmdb' file.")
    public Path ipDb;

    public boolean isHelp() {
        return help;
    }

    @Override
    public String toString() {
        return "\nhelp=" + help +
                "\nfile=" + file +
                "\nfiles=" + files +
                "\ndirectory=" + directory +
                "\nstdin=" + stdin  +
                "\nstdincmd=" + stdincmd +
                "\npropertiesPath=" + propertiesPath ;
    }

}
