<?php
//Destroys Session and return to main login page
   session_start();
   if(session_destroy()) {
      header("Location: login.php");
   }
?>