<?php include ('functions.php');
$localizacion = $_GET['localizacion'];
$lat = $_GET['lat'];
$lng = $_GET['lng'];
$motivo=$_GET['motivo'];


ejecutarSQLCommand("INSERT INTO  `coordenas` (localizacion, lat, lng, motivo)
VALUES ('$localizacion' ,'$lat', '$lng', '$motivo')

 ON DUPLICATE KEY UPDATE `localizacion`= '$localizacion',
`lat` ='$lat',
`lng` = '$lng',
`motivo` = '$motivo';");

 ?>