<?php 
include 'db.php';

/**
 * Details for a Given Ip Address
 */

//User provided IP Address
$userGivenIP = $_GET['val'];

echo "<a href='multip.php'>Return to Main Page</a>";

echo "<h2> Detailed Information regarding IP Address:  " .$userGivenIP . "</h2>";


$ipDetails = $db -> select(
"SELECT city, state, country, count(*) as AttemptedLogins, count(distinct user) as NumberOfUsernames from attempt where ipaddress = '" .$userGivenIP ."'",
"IP location details");

$totalAttemptsPerHour = $db -> select(
"SELECT HOUR(date), COUNT(*) FROM attempt where ipaddress = '".$userGivenIP."' GROUP BY HOUR(date) order by HOUR(date)",
"Total Attempts per Hour");
	
$attemptsPerHourPerDay = $db -> select(
"SELECT date, HOUR(date), COUNT(*) FROM attempt where ipaddress = '".$userGivenIP."' GROUP BY HOUR(date) order by date",
"Attempts for each hour each day");

$userNameAttempts = $db -> select(
"select user, count(*) as cnt from attempt where ipaddress = '".$userGivenIP."'group by user order by user",
"Total Number of attempts on each Username");