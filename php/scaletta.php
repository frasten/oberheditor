#!/usr/bin/php
<?php
$nome = "SILENT MIDIAN";
$n_chain = 1;





////////// DEFINES
define('NAME_LEN', 12);

define('SYSEX_START', 0xF0);
define('SYSEX_END', 0xF7);


// Limito la lunghezza del nome
$nome = str_pad($nome, NAME_LEN);
$nome = substr($nome, 0, NAME_LEN);


$messaggio = array();

/**************************************
               HEADER
***************************************/

$messaggio[] = SYSEX_START;
// Metto l'header di ogni messaggio
$messaggio = array_merge($messaggio, array(0x7E, 0x7F, 0x00, 0x02, 0x01));
// Bytes fissi per questo tipo di messaggio
$messaggio[] = 0x50;
$messaggio[] = 0x7A;
// ID chain di inizio
$messaggio[] = ($n_chain - 1) * 12;
$messaggio[] = 0x75;
$testo = array();
for ($i = 0; $i < NAME_LEN; $i++) {
	$testo[] = ord($nome{$i});
}
$messaggio = array_merge($messaggio, maschera8($testo));

$messaggio[] = SYSEX_END;

/**************************************
               DATA n, 9 per chain
***************************************/
$byte_1 = 0x50;
$byte_2 = 2 * ($n_chain - 1);
$byte_3 = 0x00;

for ($n_data = 0; $n_data < 9; $n_data++) {
	$messaggio[] = SYSEX_START;
	// Metto l'header di ogni messaggio
	$messaggio = array_merge($messaggio, array(0x7E, 0x7F, 0x00, 0x02, 0x01));
	
	$messaggio[] = $byte_1;
	$messaggio[] = $byte_2;
	$messaggio[] = $byte_3;
	
	
	// 3 Bytes particolari
	$byte_3 += 56;
	if ($byte_3 >= 0x80) {
		// Ho overflow del byte 3, cambio 0x50<==>0x70 nel byte 1
		if ($byte_1 == 0x50)
			$byte_1 = 0x70;
		else {
			// Overflow pure del byte 1, incremento il byte 2
			$byte_1 = 0x50;
			$byte_2++;
		}
	}
	$byte_3 %= 0x80; // Tengo solo il modulo
	
	// Dati veri e propri
	
	
}


/*
echo "\n\n";
$n = 0;
for ($i = 0;$i < 18;$i++) {
	$n += 56;
	$modulo = $n % 128;
	echo dechex($modulo);
	if ($n >= 128) echo "*";
	$n = $modulo;
	echo "\n";
}
*/

// DEBUG ORIGINALE
$nomefile = "../dumps/chain1.syx";

$fp=fopen($nomefile,"rb");
$bin=fread($fp,sizeof($messaggio));
$data=unpack("H*",$bin);
fclose($fp);
echo "{$data[1]}\n";


// DEBUG MIO
$pkt = "";
foreach ($messaggio as $byte) {
    $pkt = $pkt . pack("C*", $byte);   
}
$messaggio = unpack("H*", $pkt);
echo "{$messaggio[1]}\n";

$uguali = true;
for ($i = 0;$i < strlen($data[1]); $i++) {
	if ($data[1]{$i} != $messaggio[1]{$i}) {
		$uguali = false;
		break;
	}
}
if ($uguali)
	echo "OK\n";
else echo "DIVERSI!!!\n";

//$arr = array(0x53,0x49,0x4C,0x45,0x4E,0x54,0x20,0x4D,0x49,0x44,0x49,0x41);
//maschera8($arr);

function maschera8($bytes, $num_mask = 8) {
	/* Gli passo 
	 * 0x53 0x49 0x4C 0x45 0x4E 0x54 0x20 0x4D 0x49 0x44 0x49 0x41
	 * e deve restituirimi:
	 * 0x00 0x53 0x49 0x4C 0x45 0x4E 0x54 0x20 0x00 0x4D 0x49 0x44 0x49 0x41
	 */
	//print_r($bytes);
	$result = array();
	for ($i = 0; $i < sizeof($bytes); $i++) {
		if (sizeof($result) % 8 == 0)
			$result[] = 0x00;
		$result[] = $bytes[$i];
	}
	//print_r($result);
	return $result;
}


/*
$nomefile = "../dumps/chain1.syx";

$fp=fopen($nomefile,"rb");
$bin=fread($fp,filesize($nomefile));
$data=unpack("C*",$bin);
fclose($fp);

print_r( $data);
echo 0xF7;
*/
?>
