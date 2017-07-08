# IPtrack #

## *** Work in Progress *** ##

## Goals ##
The goal of this project is to upload the output of the linux lastb command (on linux servers) to a MySQL Database, find out the world location of the IP address, and to run various queries and analysis on the data to see if we can discover any interesting trends in the users or the way that users may maliciously attempt to login to your system (E.g. Which Cities or countries are attempting to log in to my system the most often? What time of the day has the most activity? Specific details regarding each IP Address etc.)

The current iteration of the program is a two step process.
1.  The user uploads the lastb data into the MySQL database.
2.  The user can view the pre-configured queries through php scripts.

## Setting Up ##
In order to properly ensure that the program runs correctly, the following is necessary prior to running:
-  MySQL service must be running.
-  You must have the proper MySQL login information and credentials with privileges for processing and executing SQL statements
-  The login information for MySQL should be stored in a properties file with the following format:

        database=databasename (database to store data in, will be created if it does not exist)
        user=yourusername
        password=yourpassword
        host=yourhost(e.g. localhost)
        port=port (e.g. 3306)

-  The files provided to the program must have appropriate read and write permissions
-  Download the GeoLite2 City database binary .mmd file (https://dev.maxmind.com/geoip/geoip2/geolite2/)
-  Capability to open and process php files (http://www.wikihow.com/Open-a-PHP-File)


## Running the Program ##
### Uploading the data ###
Run the java program supplying the appropriate files.  (an example executable Jar can be found in the artifacts folder) The program requires several arguments:

1.  The path to the GeoLite2 City database (REQUIRED)

        -db iptrack\sample\GeoLite2-City.mmdb

2.  The path to your properties file containing your MySQL login credentials (REQUIRED)

        -p iptrack\sample\config.properties
        
3.  Lastb Output, which can be of :
*       file format of the lastb (-f file), multiple files (-fs file1 file2 etc.), directory with the files (-d directorypath)
*       stdin (-stdin)
*       stdin running a specific lastb command (-stdin lastbcommand)

Run the Main.java class or jar with -h or --help to see options.

        java -jar iptrack.jar -h

Each lastb output line fed into the program must strictly adhere to the following format otherwise the program cannot parse the input correctly:
        
        user    protocol        ipaddress       date                    eventlength
        
        admin    ssh:notty    71.227.146.158   Wed Jun 28 17:51 - 17:51  (00:00)

Example this will run the jar on the file 'lastlogins' using the given Database path, and Properties path:
        
        java -jar -iptrack.jar -db iptrack\sample\GeoLite2-City.mmdb -p iptrack\sample\config.properties -f iptrack\sample\lastlogins

### Viewing Results and Queries ###

Run the login.php file through your preferred method of running php.  Enter the same login credentials to access your MySQL database, the Database name should be the same database you chose in your properties file previously.  Once properly logged in, you can search for details regarding a specific IP Address, or expand the number of results returned from each Query.

(Note: You must have a running server/web server that can process php files in order to properly view php)

### Comments/TODO/FUTURE Directions ###
Tried including the database file directly in jars to minimize files required, but resulted in massively slow data imports, which possibly can be optimized with the csv version of the database, but this could just be the slow processing of InputStream.  Further testing will be required to optimize speed and user friendliness.

Graphs to depict certain trends, transition results from pure php to HTML/Javascript to deploy to the web, additional interesting queries as data is being analyzed


References:
-  https://github.com/mvpjava/jcommander-tutorial
-  https://www.binpress.com/tutorial/using-php-with-mysql-the-right-way/17
-  https://www.maxmind.com/en/home (for GeoIp2 database)


