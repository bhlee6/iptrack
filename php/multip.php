
<?php
include 'db.php';

/**
 * Queries regarding Multiple IPs
 */

/**
 *
 * Set the count for the return results from relevant queries
 */

if (!isset($_SESSION['count'])) {
	//Default count is set to 5
	$cnt = "5";
} else {
	$cnt = $_SESSION['count'];
}


/**
 * Set the IP address the user wants to focus on(Single IP address information)
 */

if (isset($_SESSION['ipaddress'])) {
	$givenIp = $_SESSION['ipaddress'];
}


$all = $db -> select("SELECT * FROM attempt limit " . $cnt, "All");

$mostFrequentIp = $db -> select(
	"select ipaddress, country, count(*) as occurrences
	 from attempt group by ipaddress order by occurrences desc limit " . $cnt,
	"Most attempts from IP Address:");

$mostFrequentCountry = $db -> select(
	"select country, count(*) as occurrences from attempt group by country order by occurrences desc limit " . $cnt,
	"Attempted Logins from each Country");

$mostIpPerCountry = $db -> select(
	"select country, count(distinct ipaddress) as cnt from attempt group by country order by cnt desc limit " . $cnt,
	"Countries with the most number of Unique IPs");

$mostIpPerCity = $db -> select(
	"select city, country, count(*) as occurrences from attempt group by city order by occurrences desc limit " . $cnt,
	"Cities with the most number of IPs");

$ipWithMostUsernames = $db -> select(
	"select ipaddress, country, COUNT(distinct user) as count from attempt group by ipaddress order by count desc limit ".$cnt,
	"IPs with the most number of Usernames");

$attemptsEachHour =  $db -> select(
	"SELECT HOUR(date), COUNT(*) FROM attempt GROUP BY HOUR(date)",
	"Total attempts per hour");

$uniqueUsernames = $db -> select(
	"select user, COUNT(distinct ipaddress) as cnt from attempt group by user order by cnt desc limit ".$cnt,
	"Number of times a username was used with a unique ipaddress"
	);

	$totalAttemptPerHour = $db -> select (
	"SELECT ipaddress, country, HOUR(date), COUNT(*) FROM attempt GROUP BY ipaddress, HOUR(date) order by ipaddress limit ".$cnt,
	"Total attempts per hour for each IP");

	$numberOfUniqueUsernames = $db -> select(
	"select ipaddress, country, COUNT(distinct user) as cnt from attempt group by ipaddress order by cnt desc limit ".$cnt,
	"Number of Unique Usernames for each IP");

	$numberOfAttemptsSameUsername = $db -> select(
	"select user, ipaddress, country, count(*) as cnt from attempt group by user, ipaddress order by cnt desc limit ".$cnt,
	"Number of Attempts an IP Address used the Same Username");

