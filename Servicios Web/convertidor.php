<?php
include ('functions.php');
$localizacion = $_GET['localizacion'];
$motivo=$_GET['motivo'];
$lat;
$lng;
if (isset($_GET['localizacion'])) 
	{
	if(!empty($localizacion)){
		$localizacion = $localizacion ." cerca de Cuenca";
		$google_maps_url = "https://maps.googleapis.com/maps/api/geocode/json?address=". urlencode($localizacion) ."&key=AIzaSyAohPiCl4CtYUg-7QsTFh2LjEAhi67nQAM";
		$google_maps_json = file_get_contents($google_maps_url);
		$google_maps_array = json_decode($google_maps_json, true);
			
		if (!empty($google_maps_array["results"][0]["geometry"]["location"]["lat"])){
			$lat = $google_maps_array["results"][0]["geometry"]["location"]["lat"];
			$lng = $google_maps_array["results"][0]["geometry"]["location"]["lng"];
			echo $lat  ;
			echo "" . $lng;
			ejecutarSQLCommand("INSERT INTO  `coordenas` (localizacion, lat, lng, motivo)
								VALUES ('$localizacion' ,'$lat', '$lng', '$motivo')

 								ON DUPLICATE KEY UPDATE `localizacion`= '$localizacion',
								`lat` ='$lat',
								`lng` = '$lng',
								`motivo` = '$motivo';");
		}else{
			echo "Lo siento ha ocurrido un error,\ncomprueba que la dirección sea válida";
		}
	}
}

?>

