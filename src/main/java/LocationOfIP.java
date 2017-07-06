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

     private String dbName = "GeoLite2-City.mmdb";
     private ClassLoader classLoader = LocationOfIP.class.getClassLoader();
     private File db = new File(classLoader.getResource(dbName).getFile());

    /**
     * Given a string Ip address, returns an array of the location of the IP address in the format City, State, Country
     * If the value is null, the value is changed to "Unknown".
     * @param ip IP address as a String
     * @return String[] of the ip address location [0] = City, [1] = State, [2] = Country
     */
    String[] findFullLocation(String ip) {
        String[] locArray = new String[3];
        try {
            //Location of the geoIP2 city database
            DatabaseReader dbReader = new DatabaseReader.Builder(db).build();

            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);

            //Retrieve the country, city and state names from the db
            String city = response.getCity().getName();
            String state = response.getLeastSpecificSubdivision().getName();
            String country = response.getCountry().getName();

            //Contain location info into an array

            locArray[0] = city;
            locArray[1] = state;
            locArray[2] = country;

            //Change any null values to String "Unknown"
            for (int i = 0; i < locArray.length; i++) {
                if (locArray[i] == null) {
                    locArray[i] = "Unknown";
                }
            }
            dbReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locArray;
    }

    //Extraneous methods if needed straight from an IP Address

    public String findCity(String ip) {
        String[] locArray = findFullLocation(ip);
        return locArray[0];
    }

    public String findState(String ip) {
        String[] locArray = findFullLocation(ip);
        return locArray[1];
    }

    public String findCountry(String ip) {
        String[] locArray = findFullLocation(ip);
        return locArray[2];
    }
}