import java.io.*;
import java.text.ParseException;

/**
 * ReadWrite Class provides basic functionalities for reading
 * a given name of the log file, parsing the file, and writing
 * out a csv formatted text file of the log in a new output folder.
 */
public class ReadLogWriteCSV {

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
     * @param file Given File
     * @throws IOException
     * @throws ParseException
     */
    public ReadLogWriteCSV(File file) throws IOException, ParseException {

        try {
            //Read from user directory input
            reader = new BufferedReader(new FileReader(file));

            File LogCSV = createLogCSV();
            writer = new BufferedWriter(new FileWriter(LogCSV));

            //CSV header
            writer.write("user, protocol, ipaddress, datetime, eventlength");
            writer.newLine();
            String line;
            //Each attempted login on individual lines
            while (!(line = reader.readLine()).equals("")) {
                writer.write(collectRelevant(line));
                writer.newLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        //Close reader and writer
        finally{
            reader.close();
            writer.close();
        }
    }

    public File createLogCSV() throws IOException {
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
        return logdata;
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
    public static String convertDate(String dow, String month, String day, String start, String end)
            throws ParseException {
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
