<?php
 
     
include('db_connect.php');
      
	
	if (isset($_GET['userName']))
	{
		$userName = $_GET['userName'];
		
		$q=mysql_query("SELECT fileName FROM LikesNames WHERE userName = '$userName'");
		
		while($row=mysql_fetch_assoc($q))
            $json_output[]=$row;
      
		print(json_encode($json_output));
	}
      
    mysql_close();
   
     
?>