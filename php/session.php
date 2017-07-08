<?php

//Start the session
session_start();

//If the user wants to increase the number of returned results per query
if ($_SERVER["REQUEST_METHOD"]=="POST") {
	//Dealing with 'count' (increasing number of results)
	if (!empty($_POST['count'])) {
		$value = $_POST['count'];
		$min = 1;
		$max = 100;
		if ((isInt($value)) && ((int)$value <= $max) && ((int)$value >= $min)) {
			$_SESSION['count'] = $value;
			$scount = $_SESSION['count'];
		} else {
			$_SESSION['count'] = "5";
			echo "<h3><font color='red'> Enter valid integer between 1 and 100</font></h3>";
		}
}

//Dealing with user input of a specific IP Address
if (!empty($_POST['ipaddress'])) {
	$_SESSION['ipaddress'] = $_POST['ipaddress'];}
	if(isset($_SESSION['ipaddress'])) {
		$givenIp = $_SESSION['ipaddress'];
		header("location: singleip.php?val=$givenIp");
		unset($_SESSION['ipaddress']);
	}
}
