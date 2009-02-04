<?php
require_once('config.inc.php');

$db = @mysql_connect($config['mysql']['host'],
											$config['mysql']['user'],
											$config['mysql']['password']);
		@mysql_select_db($config['mysql']['db'], $db);

if (!isset($db) || !$db) {
	echo "Errore di connessione al database.";
	exit;
}

?>
