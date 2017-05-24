<?php
include('functions.php');

if($resultset=getSQLResultSet("SELECT * FROM `coordenas` ")){
	while ($row = $resultset->fetch_array(MYSQLI_NUM)){
		echo json_encode($row);
	}
}

?>