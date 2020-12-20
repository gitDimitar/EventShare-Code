<?php
     
include('db_connect.php');
      
		if(isset($_GET['fileName']))
	{
		$fileName = $_GET['fileName'];	
		$q=mysql_query("INSERT INTO Likes(fileName , imgLike) VALUES('$fileName', 0)");	
	}
      
    mysql_close();
     
?>