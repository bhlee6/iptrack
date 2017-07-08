# IPtrack #

## *** Work in Progress *** ##

## Goals ##
The goal of this project is to upload the output of the linux lastb command to a MySQL Database, and to run various queries and analysis on the data to see if we can discover any interesting trends in the users or the way that users may maliciously attempt to login to your system.

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
-  Download the GeoLite2 City database (https://dev.maxmind.com/geoip/geoip2/geolite2/)
-  Capability to open and process php files (http://www.wikihow.com/Open-a-PHP-File)


## Running the Program ##
### Uploading the data ###
Run the java program supplying the appropriate files.


References:
-  https://github.com/mvpjava/jcommander-tutorial
-  https://www.binpress.com/tutorial/using-php-with-mysql-the-right-way/17
-  https://www.maxmind.com/en/home (for GeoIp2 database)


