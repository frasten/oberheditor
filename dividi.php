#!/usr/bin/php
<?php
$dir = 'dumps';

$filename = "chain1.syx";

$path = "$dir/$filename";
$handle = fopen($path, "r");
$contents = fread($handle, filesize($path));
fclose($handle);


for ($i = 0;$i < strlen($contents);$i++) {
	$c = $contents{$i};
	if (ord($c) == 0xF0) {
		echo "nuovo\n";
	}
}

?>
