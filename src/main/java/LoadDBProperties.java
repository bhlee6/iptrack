import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * LoadDBProperties is mainly used to grab the database credentials used to login to a database
 * from a Properties file that contains the following properties:
 * "database"
 * "user"
 * "password"
 * "host"
 * "port"
 */
class LoadDBProperties {

    private DBProperties currentProperties;

    /**
     * Set the class' DBProperties currentProperties to the information contained
     * in the properties file from the given path
     * @param path path leading to the properties file that contains the database credentials
     */
    void loadFrom(Path path){

        Properties property = new Properties();
        InputStream inputStream = null;
        try {
            //CLI parameter guarantees path to the property is given
            inputStream = new FileInputStream(path.toString());
            property.load(inputStream);
            currentProperties =
                    new DBProperties(property.getProperty("database"),
                            property.getProperty("user"),
                            property.getProperty("password"),
                            property.getProperty("host"),
                            property.getProperty("port"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return the DBProperties object containing the information for the db
     */
    DBProperties getCurrentProperties() {
        return currentProperties;
    }
}