<?php
  require_once('config.php');

  echo "$game_version\n";

  $db = mysql_connect($db_host, $db_name, $db_pw);
  mysql_select_db($db_name, $db);

  $request = "DELETE FROM dgx_server WHERE ABS(TIME_TO_SEC(NOW())-TIME_TO_SEC(lastcommunication)) > 80";
  mysql_query ($request,$db);

  $request = "SELECT * FROM dgx_server WHERE active='1' ORDER BY id";
  $result = mysql_query ($request,$db);
  if (!$result) {
    echo "error";
  } else {
    while ($server = mysql_fetch_object($result)) {
      echo "$server->servername
$server->serverip
$server->port   
$server->map
";
    }
    mysql_free_result($result);
  }


?>