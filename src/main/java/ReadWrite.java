import java.io.*;
import java.text.ParseException;

/**
 * ReadWrite Class provides basic functionalities for reading
 * a given name of the log file, parsing the file, and writing
 * out a csv formatted text file of the log in a new output folder.
 */
public class ReadWrite {

    BufferedReader reader = null;
    BufferedWriter writer = null;

    String currentDir = System.getProperty("user.dir");
    String outputDir = currentDir + File.separator + "output" + File.separator;
    String outputName = "logdata"; //base name of the new file to be output
    String newOutputName;
    File logdata;

    /**
     * ReadWrite reads the given name of the log file as a String, parses the file, and writes
     * out a csv formatted text file of the log in a new output folder.
     * @param userInput Given File as a String
     * @throws IOException
     * @throws ParseException
     */
    public ReadWrite(String userInput) throws IOException, ParseException {

        try {
            //Read from user directory input
            reader = new BufferedReader(new FileReader(currentDir + File.separator + userInput));
            logdata = new File(outputDir + outputName + ".txt");
            logdata.getParentFile().mkdirs();
            int version = 1;
            //If the file already exists, add a version to the file to create a new name for the file
            while (logdata.exists()) {
                newOutputName = outputName + version;
                logdata = new File(outputDir + newOutputName + ".txt");
                version++;
            }
            logdata.createNewFile();
            writer = new BufferedWriter(new FileWriter(logdata));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //CSV header
        writer.write("user, protocol, ipaddress, datetime, eventlength");
        writer.newLine();
        String line;
        //Each attempted login on individual lines
        while (!(line = reader.readLine()).equals("")) {
            writer.write(collectRelevant(line));
            writer.newLine();
        }

        //Close reader and writer
        reader.close();
        writer.close();
    }


    /**
     * Parses a string into tokens based on spaces, and returns only the strings to keep, separated by commas.
     * @param line String read from log
     * @return Relevant strings separated by commas
     */
    public static String collectRelevant(String line) throws ParseException {
        String delims = "[ ]+";
        //Replace all "-" characters with an empty String, and tokenize the String
        String[] tokens = line.replaceAll("[-]","").split(delims);
        /*
        for (String s: tokens) {
            System.out.println(s);
        }
        */
        StringBuilder toKeep = new StringBuilder();
        //Date converter
        String date = convertDate(tokens[3],tokens[4],tokens[5], tokens[6],tokens[7]);
        if (tokens.length >= 1) {
            for (int i = 0; i < 3; i++) {
                String s = tokens[i];
                toKeep.append(s);
                toKeep.append(",");
            }
            toKeep.append(date).append(",");
            toKeep.append(removeParenthesis(tokens[tokens.length - 1]));
        }
        return toKeep.toString();
    }

    /**
     * Given the string date values from lastb logs, convertDate creates a DateTime object with the values, and
     * returns a String of the DateTime object that matches the SQL DateTime format, valid for SQL insertions.
     * @param dow Day of the week
     * @param month Month
     * @param day Day (as a number)
     * @param start Start time
     * @param end End time
     * @return String of the date, valid for SQL database insertions
     * @throws ParseException
     */
    public static String convertDate(String dow, String month, String day, String start, String end) throws ParseException {
        DateTime date = new DateTime(dow, month, day, start, end);
        return date.toStringForSql();
    }

    /**
     * Given a string, this returns the string with all parentheses removed.
     * @param s The given string
     * @return The string with all parentheses removed.
     */
    public static String removeParenthesis(String s) {
        return s.replaceAll("[()]", "");
    }
}
