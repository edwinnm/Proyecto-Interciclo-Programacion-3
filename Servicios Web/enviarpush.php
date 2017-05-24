<?php
$conn = new mysqli("localhost", "id1286017_edwinnm", "82468246e", "id1286017_coordenadas");
$sql = "SELECT token FROM firebase";
$result = $conn->query($sql);
$tokens = array();
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $tokens[] = $row["token"];
    }
}
$message = array("message" => "Message from server",
    "customKey" => "customValue");
$url = 'https://fcm.googleapis.com/fcm/send';
$fields = array(
    //'registration_ids' => $tokens, //tokens
    //"condition" => "'dogs' in topics || 'cats' in topics",
    "to" => "/topics/news",
    'data' => $message
);
$headers = array('Content-Type: application/json',
    'Authorization:key=AAAAmMWTLio:APA91bGjofrMbN1YuwDJ4kXdiCC3jwWjGNMWcYTkGNZz7tMhw1-n5m_0AL_1KcgWPkLb7FJKM8j4kyV8exBrm0fwA3yM1lzUuoQSGZaben_WiCVvarzOhgVeM_LX4bnnZ-c6AkIjyDID'
);
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
$result = curl_exec($ch);
if ($result == FALSE)
    die('Curl failed: ' . curl_error($ch));
curl_close($ch);
$conn->close();
?>