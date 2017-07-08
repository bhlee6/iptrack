
<?php 
include 'htmlfunctions.php';
include 'session.php';


class Database {
	
	// The Database Connection
	protected static $connection;
	
	/**
	 * Connect to the database
	 * @return boolean false on connection failure, and
	 * mysqli MySQLi object instance on successful connection
	 */
	
	
	public function connect() {
		//Set the values from the Session
		$myusername = $_SESSION['username'];
		$mypassword = $_SESSION['password'];
		$mydbname = $_SESSION['database'];
		$hostport = $_SESSION['hostport'];
		
		
		//Attempt connection with mysqli from user provided information
		if(!isset(self::$connection)) {
			self::$connection = new mysqli($hostport,$myusername,$mypassword,$mydbname);
		}
		//Handle connection errors
		if(self::$connection === false) {
			displayConnectionErrorMessage();
			return false;
		}
		return self::$connection;
	}
	
	/**
	 * Query the database with the given query
	 *
	 * @param $query The given query string
	 * @return mixed The result of the mysqli::query() function
	 */
	public function query($query) {
		// Connect to the db
		$connection = $this -> connect();
		// Query the database
		$result = $connection -> query($query);
		return $result;
	}
	
	/**
	 * Fetch rows from the database (SELECT query)
	 *
	 * @param $query The query string
	 * @param $comment The comment detailing the query 
	 * @return boolean False if query fails, and displays Query results into a table on sucess
	 */
	public function select($query, $comment) {
		$result = $this -> query($query);
		if($result === false) {
			return false;
		}
		
		//Display Comment regarding the query
		displayComment($comment);
		
		//Fields used to grab MySQL Column Names
		$finfo = $result->fetch_fields();
		
		//Display Table
		displayTable($finfo, $result);
	}
	
	/**
	 * Fetch the database error 
	 * @return string database error
	 */
	public function error() {
		$connection = $this -> connect();
		return $connection -> error;
	}
	
	/**
	 * Quote and escape value for use in a database query
	 *
	 * @param string $value The value to be quoted and escaped
	 * @return string The quoted and escaped string
	 */
	public function quote($value) {
		$connection = $this -> connect();
		return "'" . $connection -> real_escape_string($value) . "'";
	}

	
	//Temporary error message to display for connection errors
	public function displayConnectionErrorMessage() {
		echo "<h1> <font color='FF0000'>Error Connecting to Database</font></h1>";
		echo "<h2> Check database login credentials and database access privileges.</h2>";
	}
}

$db = new Database();
