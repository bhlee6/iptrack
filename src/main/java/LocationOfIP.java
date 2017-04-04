import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;


/**
 * Class used to identify the location of a given ip address using the geoip2 city database.
 * Uses the default file name of the database: GeoLite2-City.mmdb
 * The database should be in the root directory.
 */
public class LocationOfIP {

    static String currentDir = System.getProperty("user.dir");
    static String dbName = "GeoLite2-City.mmdb";

/*

    public static void main(String[] argv) throws GeoIp2Exception, IOException {
        System.out.println(findLocation("177.125.243.163"));
    }
*/

    /**
     * Given a string Ip address, returns the location of the IP address in the format "City, State, Country"
     * @param ip IP address as a String
     * @return String of the ip address location "City, State, Country"
     * @throws IOException
     * @throws GeoIp2Exception
     */
    public static String findLocation(String ip) throws IOException, GeoIp2Exception {
        //Location of the geoIP2 city database
        String dbLocation = currentDir + File.separator + dbName;
        File db = new File(dbLocation);
        DatabaseReader dbReader = new DatabaseReader.Builder(db).build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        //Retrieve the country, city and state names from the db
        String country = response.getCountry().getName();
        String city = response.getCity().getName();
        String state = response.getLeastSpecificSubdivision().getName();
        return city  + ", " + state + ", " +  country;
    }
}