<?php

for ($i = 0; $i <= 0xff;$i++) {
	echo str_pad(strtoupper(dechex($i)), 2, '0', PAD_LEFT) . " " . str_pad(decbin($i),8,'0',PAD_LEFT) . "\n";
}

?>
