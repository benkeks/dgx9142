<?php

  require_once('config.php');
  
  $db = mysql_connect($db_host, $db_name, $db_pw);
  mysql_select_db($db_name, $db);
  
  $validate = $_GET['validate'];
  $remove = $_GET['remove'];
  $sname = $_GET['sname'];
  $sport = $_GET['sport'];
  $smap = $_GET['smap'];

if ($validate!="") {
  // life pulse from registered server
  $validate = addslashes($validate);
  $ip = getenv ("REMOTE_ADDR");
  $request = "UPDATE $table_name SET active='1', lastcommunication=NOW() WHERE id='$validate' AND serverip='$ip'";
  mysql_query ($request,$db);
  echo "ok";
} elseif ($remove!="") {
  // registered server asking for removal from list
  $remove = addslashes($remove);
  $ip = getenv ("REMOTE_ADDR");
  $request = "DELETE FROM $table_name WHERE id='$remove' AND serverip='$ip'";
  mysql_query ($request,$db);
} else {
  // new server registering
  if ($sname=="") {
   echo "no name";
  } elseif ($sport=="") {
   echo "no port";
  } else {
   $sname = addslashes($sname);
   $sport = addslashes($sport);
   $smap = addslashes($smap);
   $ip = getenv ("REMOTE_ADDR");

   $request = "DELETE FROM $table_name WHERE serverip='$ip'";
   mysql_query ($request,$db);

   $sqlmsg = "INSERT INTO $table_name (servername, serverip, port, map, lastcommunication) VALUES ('$sname','$ip','$sport','$smap',  NOW())";
   mysql_query($sqlmsg,$db);

   $request = "SELECT *FROM `$table_name`  ORDER BY `id`  DESC LIMIT 0,1";
   $result = mysql_query ($request,$db);
   if (!$result) {
     echo "error";
     return ;
   } //endif

   $server = mysql_fetch_object($result);
   echo "$server->id,$server->lastcommunication";
  }
}
?>