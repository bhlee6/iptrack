import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;

/**
 * Class DateTime is used to create a DateTime object for the date and time of an attempted login.
 */
class DateTime {

    //Day of the Week (ie Mon, Tue, Wed...)
    private String dow;
    //Abbreviated Month (Jan, Feb, Mar...)
    private String month;
    //Numerical date
    private String day;
    private String startTime;
    private String endTime;
    private int year;

    /**
     * @param dow Day of the Week (ie Mon, Tue, Wed...)
     * @param month Abbreviated Month (Jan, Feb, Mar...)
     * @param day Date Numerical Date
     * @param startTime start time of the login, hour:min
     * @param endTime end time of the login, hour:min
     */
    DateTime(String dow, String month, String day, String startTime, String endTime) {
        this.dow = dow;
        this.month = month;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = Year.now().getValue();
    }

    /**
     * Takes the DateTime object and returns its information as a String that is properly
     * formatted for SQL DateTime insertions.  00 seconds added with the assumption that the startTime
     * does not include the seconds value.
     * @return String DateTime information formatted for SQL insertions
     */
    String toStringForSql() throws ParseException {
        String given = this.dow+ " " + this.month+ " " + this.day + " " + this.startTime + " " + this.year;
        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm yyyy");
        Date date = parser.parse(given);
        //Adds 00 for seconds value since the log does not include seconds, default set to 0
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:" + "00");
        return formatter.format(date);
    }
}


