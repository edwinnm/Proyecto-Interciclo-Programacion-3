<?php
	if (isset($_GET['direccion'])) 
	{
		$direccion = $_GET['direccion'];
		if(!empty($direccion)){
			$direccion = $direccion ." cerca de Cuenca";
			$google_maps_url = "https://maps.googleapis.com/maps/api/geocode/json?address=". urlencode($direccion) ."&key=AIzaSyAohPiCl4CtYUg-7QsTFh2LjEAhi67nQAM";
			$google_maps_json = file_get_contents($google_maps_url);
			$google_maps_array = json_decode($google_maps_json, true);
		
			if (!empty($google_maps_array["results"][0]["geometry"]["location"]["lat"])){
				$lat = $google_maps_array["results"][0]["geometry"]["location"]["lat"];
				$lng = $google_maps_array["results"][0]["geometry"]["location"]["lng"];
				echo $lat  ;
				echo "" . $lng;
			}else{
				echo "Lo siento ha ocurrido un error,\ncomprueba que la dirección sea válida";
			}
		
		}
		
	}
?>

<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<title>Ejemplo</title>
</head>
<body>
	<form action="" method="GET">
		<label for= "direccion"> Ingrese la dirección:</label>
		<input type="text" name="direccion">

		<button type="sumit">Consultar</button>
	</form>
</body>
</html>
