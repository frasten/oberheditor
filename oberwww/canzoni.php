<html>
	<head>
		<title></title>
		<link rel="stylesheet" type="text/css" href="css/stili.css" />
		<script type='text/javascript' src='js/jquery.js'></script>
		<script type='text/javascript' src='js/jquery.tablednd_0_5.js'></script>
		<script type='text/javascript' src='js/jquery.jeditable.mini.js'></script>
	</head>
</html>
<body>
<?php

require_once('db.inc.php');

$mode = (array_key_exists('mode', $_REQUEST) ? $_REQUEST['mode'] : NULL);

echo "<h4>Lista di canzoni</h4>";
/* ogni canzone ha:
 * id
 * nome
 * lista_patch, che è una stringa, con i numeri delle patch separate da |
 * lista_desc, lista delle descrizioni di ogni patch, separate da |
 * */





/*
CREATE TABLE `oberheim`.`canzone` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
	`nome` varchar(30)  NOT NULL,
  `lista_patch` text  NOT NULL,
  `lista_desc` text  NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = MyISAM;

*/


if (!$mode) {
	$query = "SELECT * FROM canzone";
	$result = mysql_query($query);
	if (!$result) {
		echo "Errore nella query.";
		return;
	}

	if (mysql_num_rows($result)) {
		echo "<ul>\n";
		while ($riga = mysql_fetch_assoc($result)) {
			echo "<li>";
			echo "<a href='?mode=edit&id={$riga['id']}'>{$riga['nome']}</a>";
			echo "</li>\n";
		}
		echo "</ul>\n";
	}
	else {
		echo "Nessuna canzone salvata.";
		// TODO: link a crea nuova
	}
}
else if ($mode == 'edit') {
	$id = $_GET['id'];
	$id = intval($id);
	
	$query = "SELECT * FROM canzone WHERE id='$id' LIMIT 1";
	$result = mysql_query($query);
	if (!$result) {
		echo "Errore nella query.";
		return;
	}
	if (!mysql_num_rows($result)) {
		echo "ID non valido.";
		return;
	}
	$riga = mysql_fetch_assoc($result);
	echo "<h2 id='nome_song' class='edit'>{$riga['nome']}</h2>";
	
	$arr_patch = explode('|', $riga['lista_patch']);
	$arr_desc = explode('|', $riga['lista_desc']);
	
	?>
	<table id='table-patches'>
		<thead>
			<tr>
				<td></td>
				<td>Numero</td>
				<td>Patch</td>
				<td>Descrizione</td>
			</tr>
		</thead>
		<tbody>
	<?php
	for ($i = 0;$i < sizeof($arr_patch); $i++) {
		echo "<tr id='patch_$i'>";
		echo "<td class='dragHandle'> </td>";
		echo "<td>" . ($i+1) ."</td><td class='patcheditor'>{$arr_patch[$i]}</td>";
		echo "<td class='edit'>{$arr_desc[$i]}</td></tr>\n";
	}
	echo "</tbody></table>";
	?>
<script type="text/javascript">
	var nome_table = "table-patches";
	function salva_lista(tabella, riga) {
		var righe = tabella.tBodies[0].rows;
		
		var ordine_patches = '';
		$(tabella.tBodies[0]).find('td').filter(':nth-child(' + (2 + 1) + ')').each(function() {
			if (ordine_patches.length > 0) ordine_patches += '|';
			ordine_patches += $.trim(this.innerHTML);
		});
		
		var ordine_desc = '';
		$(tabella.tBodies[0]).find('td').filter(':nth-child(' + (3 + 1) + ')').each(function() {
			if (ordine_desc.length > 0) ordine_desc += '|';
			ordine_desc += $.trim(this.innerHTML);
		});
		
		if (ordine_desc.indexOf("<form class=") >= 0) return;
		
		$.post('ajax.php', {
			'id_canzone': <?php echo $id ?>,
			'mode': 'order_patches',
			'patches': ordine_patches,
			'desc': ordine_desc
			},
			function(data) {
				// TODO: Whatever you want to do here
			}
		);
	}
	
	
	$(document).ready(function() {
			// Initialise the table
			$("#"+nome_table).tableDnD({
				onDragClass: 'tr_drag',
				onDrop: salva_lista,
				dragHandle: "dragHandle"
			});
			$("#" +nome_table+" tr").hover(function() {
				$(this.cells[0]).addClass('showDragHandle');
			}, function() {
				$(this.cells[0]).removeClass('showDragHandle');
			});
	});
	
	// Editable
	$(document).ready(function() {
		$('.edit').editable(function(value, settings) {
			if (this.id == 'nome_song') {
				// Ho editato il nome della canzone
				$.post('ajax.php', {
					'id_canzone': <?php echo $id ?>,
					'mode': 'nome_canzone',
					'nome': value
					},
					function(data) {
						// TODO: Whatever you want to do here
					}
				);
			}
			else {
				// Ho editato qualcosa sotto, chiamo il salvataggio della lista
				this.innerHTML = value;
				salva_lista($("#"+nome_table)[0], null);
			}
			return(value);
			}, {
			indicator : 'Salvataggio...',
      tooltip   : 'Click per modificare...',
			cssclass: 'input_editabile'
		});
		
		$('.patcheditor').editable(function(value,settings) {
			
			}, {
			type: 'patcheditor'
		});
 	});
	
	/***** EDITOR PATCHES *****/ 
	$.editable.addInputType('patcheditor', {
    /* create input element */
    element : function(settings) {
			var banco = $("<select id='edit_banco' name=''>"+
			"<option value='A'>A</option>"+
			"<option value='B'>B</option>"+
			"<option value='C'>C</option>"+
			"<option value='D'>D</option>"+
			"<option value='E'>E</option>"+
			"<option value='F'>F</option>"+
			"<option value='G'>G</option>"+
			"<option value='H'>H</option>"+
			"</select>");
			$(this).append(banco);
			// Text di 3 cifre
			var numpatch = $("<input type='text'name='' id='edit_patch' size='3'>");
			$(this).append(banco);
			$(this).append(numpatch);
			
			var input = jQuery('<input type="hidden">');
			$(this).append(input);
			return(input);
    },
		submit : function(settings, original) {
			var value = $("#edit_banco").val() + '-' + $("#edit_patch").val();
			$("input", this).filter(":hidden").val(value);
		},
		
    content : function(stringa, settings, original) {
			/* do nothing */
			var pezzi = stringa.split('-');
			console.log($("#edit_banco", this));
			$("#edit_banco", this).val(pezzi[0]);
			$("#edit_patch", this).val(pezzi[1]);
    },
    plugin : function(settings, original) {
			settings.onblur = 'ignore';
			// Banco
    }
	});


</script>
	<?php
}

?>
</body>
