import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;

public class DateTime {

    //Day of the Week (ie Mon, Tue, Wed...)
    String dow;
    //Abbreviated Month (Jan, Feb, Mar...)
    String month;
    //Numberical date
    String day;
    String startTime;
    String endTime;
    int year;

    public DateTime(String dow, String month, String day, String startTime, String endTime) {
        this.dow = dow;
        this.month = month;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = Year.now().getValue();
    }

    public String toStringForSql() throws ParseException {
        String given = this.dow+ " " + this.month+ " " + this.day + " " + this.startTime + " " + this.year;
        SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm yyyy");
        Date date = parser.parse(given);
        //Adds 00 for seconds value since the log does not include seconds, default set to 0
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:" + "00");
        String formattedDate = formatter.format(date);
        return formattedDate;

    }
/*
    public static void main(String[] argsv) throws ParseException{
        DateTime dt = new DateTime("Mon", "Mar", "1", "23:24", "22:25");
        System.out.println(dt.toStringForSql());
    }
    */
}


