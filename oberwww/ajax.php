<?php
require_once('db.inc.php');

$mode = (array_key_exists('mode', $_REQUEST) ? $_REQUEST['mode'] : NULL);

if ($mode == 'order_patches') {
	$id_canzone = intval($_POST['id_canzone']);
	$patches = $_POST['patches'];
	$desc = $_POST['desc'];
	if (strpos($desc,'<form class=')) return;
	
	$query = "UPDATE canzone SET lista_patch='$patches', lista_desc='$desc' WHERE id='$id_canzone' LIMIT 1";
	$result = mysql_query($query);
	// TODO: in caso d'errore, mostrare errore
	// TODO: far qualcosa per dire che l'ho fatto
	// TODO: controllare apostrofi etc
}
else if ($mode == 'nome_canzone') {
	$nome = $_POST['nome'];
	$id_canzone = intval($_POST['id_canzone']);
	$query = "UPDATE canzone SET nome='$nome' WHERE id='$id_canzone' LIMIT 1";
	$result = mysql_query($query);
	// TODO: in caso d'errore, mostrare errore
	// TODO: far qualcosa per dire che l'ho fatto
	// TODO: controllare apostrofi etc
}
else if ($mode == '') {
	
}

?>
