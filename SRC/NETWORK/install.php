<?php
  /*
    This script initializes the dgx_server table in your database.
  */
  
  require_once('config.php');

  $db = mysql_connect($db_host, $db_name, $db_pw);
  mysql_select_db($db_name, $db);
  
  $request =
    "CREATE TABLE IF NOT EXISTS `$table_name` (
      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
      `servername` tinytext NOT NULL,
      `serverip` tinytext NOT NULL,
      `port` smallint(5) unsigned NOT NULL DEFAULT '0',
      `map` tinytext NOT NULL,
      `lastcommunication` time NOT NULL DEFAULT '00:00:00',
      `active` tinyint(1) NOT NULL DEFAULT '0',
      PRIMARY KEY (`id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;";
  
  if (!mysql_query($request, $db)) {
    die('Installation failed: ' . mysql_error());
  } else {
    echo "Installation completed.";
  }
?>