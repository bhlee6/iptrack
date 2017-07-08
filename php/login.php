<?php

/**
 * Login Page
 */

//Store user provided credentials for database access into the Session
if($_SERVER["REQUEST_METHOD"] == "POST") {
session_start();
$_SESSION['username'] = $_POST['username'];
$_SESSION['password'] = $_POST['password'];
$_SESSION['database'] = $_POST['database'];
$_SESSION['hostport'] = $_POST['hostport'];

//Go to main query page
header("location: multip.php");
}

//TBD Reminder message 
$remindermsg = "Ensure credentials are entered correctly and Check database privileges."
?>

<html>   
   <head>
      <title>Login Page</title>
      
      <style type = "text/css">
         body {
            font-family:Arial, Helvetica, sans-serif;
            font-size:14px;
         }
         
         label {
            font-weight:bold;
            width:100px;
            font-size:14px;
         }
         
         .box {
            border:#666666 solid 1px;
         }
      </style>
      
   </head>
   
   <body bgcolor = "#FFFFFF">
	
      <div align = "center">
         <div style = "width:300px; border: solid 1px #333333; " align = "left">
            <div style = "background-color:#333333; color:#FFFFFF; padding:3px;"><b>Login</b></div>
				
            <div style = "margin:30px">
               
               <form action = "" method = "post">
                  <label>UserName  </label><input type = "text" name = "username" class = "box"/><br/><br/>
                  <label>Password  </label><input type = "password" name = "password" class = "box" /><br/><br/>
                  <label>Host:Port (Example: localhost:3306)  </label><input type = "text" name = "hostport" class = "box"/><br/><br/>
                  <label>Database  </label><input type = "text" name = "database" class = "box" /><br/><br/>
                  <input type = "submit" value = " Submit "/><br />
               </form>
               
               <div style = "font-size:11px; color:#cc0000; margin-top:10px"><?php echo $remindermsg; ?></div>
            </div>
				
         </div>	
      </div>
   </body>
</html>