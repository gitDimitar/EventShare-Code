<?php
 
     
include('db_connect.php');
      
	
	if (isset($_GET['fileName']) && isset($_GET['userName']))
	{
		$fileName = $_GET['fileName'];
		$userName = $_GET['userName'];
		
		$result = mysql_query("SELECT * FROM LikesNames WHERE fileName = '$fileName' and userName = '$userName'");
		if(mysql_num_rows($result) == 0) {
			$q1=mysql_query("UPDATE Likes SET imgLike = imgLike + 1 WHERE fileName = '$fileName'");
			$q2=mysql_query("INSERT INTO LikesNames(fileName, userName) values('$fileName' , '$userName')");
		} 
		else 
		{
            $q1=mysql_query("UPDATE Likes SET imgLike = imgLike - 1 WHERE fileName = '$fileName'");
			$q2=mysql_query("DELETE FROM LikesNames WHERE fileName = '$fileName' and userName = '$userName'");
			
		}

	}
      
    mysql_close();
   
     
?>					