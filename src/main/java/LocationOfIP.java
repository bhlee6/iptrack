import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;


/**
 * Class used to identify the location of a given ip address using the geoip2 city database.
 * Uses the default file name of the database: GeoLite2-City.mmdb
 * The database should be in the root directory.
 */
class LocationOfIP {
    //private String dbName = "sample/GeoLite2-City.mmdb";
    //This Iteration provided for complete single file jar implementation, but very slow due to stream build
    // private InputStream stream = LocationOfIP.class.getClassLoader().getResourceAsStream(dbName);

     /* When including the geolite db into a packaged jar, it became necessary to build it as a stream,
     however, the performance is significantly diminished.   Consideration needed for best way to package.
     NOTE: Including the db into the Packaged Jar and reading from inputStream results not advised. TOO SLOW.
     */

    // private ClassLoader classLoader = LocationOfIP.class.getClassLoader();
    // private File db = new File(classLoader.getResource(dbName).getFile());

    private Path dbPath;
    private DatabaseReader dbReader;

    /**
     * Given a string Ip address, returns an array of the location of the IP address in the format City, State, Country
     * If the value is null, the value is changed to "Unknown".
     * @param ip IP address as a String
     * @return String[] of the ip address location [0] = City, [1] = State, [2] = Country
     */
    String[] findFullLocation(String ip) {
        String[] locArray = new String[3];
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locArray;
    }


    /**
     * Build the Database reader for the Maxmind GeoIP database
     */
    void buildDbReader() {
        try {
            File db = new File(dbPath.toString());
            dbReader = new DatabaseReader.Builder(db).build();

            //Previous Build
            //Location of the geoIP2 city database
            //DatabaseReader dbReader = new DatabaseReader.Builder(stream).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the MaxMind Database Reader
     */
    void closeDbReader() {
        try {
            dbReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Set the dp Path with the given path
     */
    void setDbPath(Path p) {
        this.dbPath = p;
    }

        /*
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
    */

}