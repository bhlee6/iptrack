import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;


/**
 * Class used to identify the location of a given ip address using the geoip2 database (city)
 */
public class LocationOfIP {

    public static void main(String[] argv) throws GeoIp2Exception, IOException {
        System.out.println(findLocation("177.125.243.163"));
    }

    public static String findLocation(String ip) throws IOException, GeoIp2Exception {
        String dbLocation = "C:\\CSPersonal\\iptrack\\GeoLite2-City.mmdb";

        File db = new File(dbLocation);
        DatabaseReader dbReader = new DatabaseReader.Builder(db).build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        String country = response.getCountry().getName();
        String city = response.getCity().getName();
        String state = response.getLeastSpecificSubdivision().getName();
        String location = city  + ", " + state + ", " +  country;
        //for testing purposes print
        System.out.println(location);
        return location;
    }
}