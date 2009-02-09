#!/usr/bin/php
<?php

$path = $argv[1];
if (!$path) {
	$dir = 'dumps';
	$filename = "pg_tables_u17_u38.syx";
	$path = "$dir/$filename";
}

$handle = fopen($path, "r");
$contents = fread($handle, filesize($path));
fclose($handle);


for ($i = 0;$i < strlen($contents);$i++) {
	$c = $contents{$i};
	if (ord($c) == 0xF0) {
		echo "\n";
	}
	echo (str_pad(strtoupper(dechex(ord($c))), 2, '0', PAD_LEFT) . ' ');
}

?>
