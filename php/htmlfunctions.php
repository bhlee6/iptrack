<?php
/**
 * HTML Information, styles, and functions to display some page data
 */


/**
 * Display the given comment
 * @param $comment String comment
 */
function displayComment($comment) {
	echo "<h2>" . $comment ."</h2>";
};

/**
 * 
 * Displays the Table information
 * @param $finfo String field information
 * @param $result the query result
 */
function displayTable($finfo, $result) {
	echo "<table>";
	echo "<tr>";

	//Display the Column Names
	foreach ($finfo as $val) {
		echo "<th><strong>". ucfirst($val->name). "</strong></th>" ;
	}
	echo "</tr>";

	//Display each row
	displayRows($result);

	echo "</table>";
}
/**
 * 
 * Display the results of each row
 * @param $result the query result
 */

function displayRows($result) {
	while ($row = $result -> fetch_assoc()) {
		echo '<tr>';
		foreach($row as $field => $value) {
			if ($field === "ipaddress") {
				$valueString = (string)$value;
				echo '<td>' . "<a href='singleip.php?val=$valueString'>$valueString</a>" . '</td>';
			} else {
				echo '<td>' . htmlspecialchars($value) . '</td>' ;
			}
		}
		echo '</tr>';
	}
}

/**
 * Check to make sure a given input is an integer
 * @param $input String user input input
 * @return boolean true if an integer, false otherwise
 */
function isInt($input) {
	$input = filter_var($input, FILTER_VALIDATE_INT);
	return ($input !== FALSE);
}
?>

<html>
<head>
<Title>Queries</Title>
<style>
body {
	padding-top: 50px;
	background-color: silver;
	font-family:Arial, Helvetica, sans-serif;
	font-size:14px;
}

table {
	border-collapse: collapse;
}

table,td,th {
	border: 2px solid black;
	padding: 5px;
}

th {
	text-align: left;
}
</style>
</head>
<body>

<p align="right">
<form action="logout.php"><input type="submit" value=" Log Out " /></form>
</p>

<form action="" method="post"><label>Enter Specific IP Address</label> <input
	type="text" name="ipaddress" class="box" /> <input type="submit"
	value=" Find " /><br />
</form>

<form action="" method="post"><label>Enter integer to increase results
(limit 100)</label> <input type="text" name="count" class="box" /> <input
	type="submit" value=" Submit " /><br />
</form>
