<?php
 
     
include('db_connect.php');

		$q=mysql_query("SELECT * from Likes");
		
		while($row=mysql_fetch_assoc($q))
                    $json_output[]=$row;
      
		print(json_encode($json_output));
      
    mysql_close();
   
     
?>	