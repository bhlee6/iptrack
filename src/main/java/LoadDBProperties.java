import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadDBProperties {

    DBProperties currentProperties;

    public void loadFrom(String path){

        Properties property = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            if(inputStream==null){
                System.out.println("Unable to find file.");
                return;
            }

            property.load(inputStream);
            currentProperties =
                    new DBProperties(property.getProperty("database"),
                            property.getProperty("user"),
                            property.getProperty("password"),
                            property.getProperty("host"),
                            property.getProperty("port"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public DBProperties getCurrentProperties() {
        return currentProperties;
    }
}