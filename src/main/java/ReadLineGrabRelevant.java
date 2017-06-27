
import java.text.ParseException;

/**
 * ReadLineGrabRelevant Class has various methods to collect the relevant
 * information needed from a String line of a lastb output
 */
class ReadLineGrabRelevant {

    /**
     * Given a String of a lastb output line, return the relevant data as a String[] with Strings valid for SQL
     * @param line String of the lastb line
     * @return String[] containing all the relevant Strings for SQL insertion
     *
     */
    String[] collectRelevant(String line) throws ParseException {
        String delims = "[ ]+";
        //Replace all "-" characters with an empty String, and tokenize the String
        String[] tokens = line.replaceAll("[-]","").split(delims);
        String[] toKeep = new String[5];

        //Date converter
        String date = convertDate(tokens[3],tokens[4],tokens[5], tokens[6],tokens[7]);

        toKeep[0] = tokens[0];
        toKeep[1] = tokens[1];
        toKeep[2] = tokens[2];
        toKeep[3] = date;
        toKeep[4] = removeParenthesis(tokens[8]);
        return toKeep;
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
     */
    private String convertDate(String dow, String month, String day, String start, String end)
            throws ParseException {
        DateTime date = new DateTime(dow, month, day, start, end);
        return date.toStringForSql();
    }

    /**
     * Given a string, this returns the string with all parentheses removed.
     * @param s The given string
     * @return The string with all parentheses removed.
     */
    private String removeParenthesis(String s) {
        return s.replaceAll("[()]", "");
    }

}
