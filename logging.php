<?php
     
include('db_connect.php');
      
		if(isset($_GET['fileName']) && isset($_GET['city']) && isset($_GET['place']))
	{
		$fileName = $_GET['fileName'];	
		$city = $_GET['city'];
		$place = $_GET['place'];
		$q1=mysql_query("INSERT INTO PlacerLogger(fileName , city, place) VALUES('$fileName', '$city', '$place')");	
	}
      
    mysql_close();
     
?>