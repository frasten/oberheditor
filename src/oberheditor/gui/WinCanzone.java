package oberheditor.gui;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;



public class WinCanzone {
	Shell win; // La finestra stessa
	List listPatches;
	Spinner txtPatch;
	Combo cmbBanco;
	Text txtNome;
	
	
	/* costruttore */
	public WinCanzone(Display display) {
		win = new Shell(display);
		win.setText("Nuova canzone");
		
		int win_w = 400;
		int win_h = 400;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		Label lblNome = new Label(win, SWT.NONE);
		lblNome.setText("Nome canzone:");
		lblNome.pack();
		lblNome.setLocation(10, 14);
		
		txtNome = new Text(win, SWT.BORDER);
		txtNome.pack();

		txtNome.setSize(240, txtNome.getSize().y);
		txtNome.setLocation(120, 10);
		
		Label lblListaPatch = new Label(win, SWT.NONE);
		lblListaPatch.setText("Lista delle patch");
		lblListaPatch.pack();
		lblListaPatch.setLocation(10, 50);
		
		// Lista
		listPatches = new List (win, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		
		listPatches.setBounds (10, 80, 100, 280);
		listPatches.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				// Su cambio di selezione, imposto i controlli per una nuova patch
				String [] sel = listPatches.getSelection();
				String[] dati = sel[0].split("-");
				cmbBanco.select(cmbBanco.indexOf(dati[0]));
				txtPatch.setSelection(Integer.parseInt(dati[1]) + 1);
			}
		});
		
		/****** Zona di aggiunta *********/
		
		// Banco
		cmbBanco = new Combo (win, SWT.READ_ONLY);
		cmbBanco.setItems (new String [] {"A", "B", "C", "D", "E", "F", "G", "H"});
		cmbBanco.select(0);
		cmbBanco.pack();
		cmbBanco.setLocation(140, 80);

		// Patch
		txtPatch = new Spinner (win, SWT.BORDER);
		txtPatch.setMinimum(1);
		txtPatch.setMaximum(128);
		txtPatch.setSelection(1);
		txtPatch.setIncrement(1);
		txtPatch.setPageIncrement(10);
		txtPatch.pack();
		txtPatch.setLocation(195, 80);
		
		// Pulsante Add
		Button btnAdd = new Button(win, SWT.PUSH);
		Image imgAdd = new Image(display, "res/add.png");
		btnAdd.setImage(imgAdd);

		//btnAdd.setText("Aggiungi");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listPatches.add(cmbBanco.getItem(cmbBanco.getSelectionIndex()) + "-" +
						String.format("%03d", Integer.parseInt(txtPatch.getText()))
				);/*
				ScrollBar sb = listPatches.getVerticalBar();
//					sb.setMaximum(sb.getMaximum() + sb.getIncrement());
				listPatches.update();
				sb.setSelection(sb.getMaximum());
				System.out.println(sb.getMaximum());*/
				txtPatch.setSelection(txtPatch.getSelection() + 1);
				txtPatch.setFocus();
			}
		});
		btnAdd.pack();
		btnAdd.setLocation(250, 80);
		
		// Con invio, aggiunge questa patch
		win.setDefaultButton(btnAdd);
		
		/********************************************
		 * CONTROLLI PER LA LISTA
		 ********************************************/
		Button btnMuoviSu = new Button(win, SWT.PUSH);
		btnMuoviSu.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String [] selezionati = listPatches.getSelection();
				// se non ho nessuna selezione, non faccio niente
				if (selezionati.length <= 0) return;
				int [] idSelezionati = listPatches.getSelectionIndices(); 
				// se sono gia' all'inizio della lista, non faccio niente
				if (idSelezionati[0] <= 0) return;
				
				int idPrecedente = idSelezionati[0] - 1;
				for (int i = 0; i < selezionati.length; i++) {
					int nuovaposizione = idPrecedente + (idSelezionati[i] - idSelezionati[0]);
					listPatches.add(selezionati[i], nuovaposizione);
					listPatches.remove(idSelezionati[i] + 1);
					// Ripristino la selezione
					listPatches.select(nuovaposizione);
				}

			}
		});
		Image imgSu = new Image(display, "res/up.png");
		btnMuoviSu.setImage(imgSu);
		btnMuoviSu.pack();
		btnMuoviSu.setLocation(120, 170);
		
		Button btnMuoviGiu = new Button(win, SWT.PUSH);
		btnMuoviGiu.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String [] selezionati = listPatches.getSelection();
				// se non ho nessuna selezione, non faccio niente
				if (selezionati.length <= 0) return;
				int [] idSelezionati = listPatches.getSelectionIndices(); 
				// se sono gia' alla fine della lista, non faccio niente
				if (idSelezionati[idSelezionati.length - 1] >= listPatches.getItemCount() - 1) return;
				
				int idSuccessivo = idSelezionati[idSelezionati.length - 1] + 1;
				for (int i = selezionati.length -1 ; i >= 0; i--) {
					int nuovaposizione = idSuccessivo + (idSelezionati[i] - idSelezionati[idSelezionati.length - 1]) + 1;
					listPatches.add(selezionati[i], nuovaposizione);
					listPatches.remove(idSelezionati[i]);
					// Ripristino la selezione
					listPatches.select(nuovaposizione - 1);
				}
			}
		});
		Image imgGiu = new Image(display, "res/down.png");
		btnMuoviGiu.setImage(imgGiu);
		btnMuoviGiu.pack();
		btnMuoviGiu.setLocation(120, 210);
		
		Button btnElimina = new Button(win, SWT.PUSH);
		Image imgElimina = new Image(display, "res/delete.png");
		btnElimina.setImage(imgElimina);
		btnElimina.pack();
		btnElimina.setLocation(120, 250);
		btnElimina.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listPatches.remove(listPatches.getSelectionIndices());
			}
		});
		
		/*
		 * TODO: Insert
		Button btnInserisci = new Button(win, SWT.PUSH);
		Image imgInserisci = new Image(display, "res/add.png");
		btnInserisci.setImage(imgInserisci);
		btnInserisci.pack();
		*/
		
		
		
		Button btnAnnulla = new Button(win, SWT.PUSH);
		btnAnnulla.setText("Annulla");
		btnAnnulla.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				win.close();
			}
		});
		btnAnnulla.pack();
		btnAnnulla.setSize(new Point(80, btnAnnulla.getSize().y));
		btnAnnulla.setLocation(215, 330);
		
		
		Button btnSalva = new Button(win, SWT.PUSH);
		btnSalva.setText("Salva");
		btnSalva.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				salva();
				win.close();
			}
		});
		btnSalva.pack();
		// Un po' piu' grandi:
		btnSalva.setSize(new Point(80, btnSalva.getSize().y));
		btnSalva.setLocation(305, 330);
		
		txtPatch.setFocus();
		
		// Tasto esc per uscire
		win.addListener (SWT.Traverse, new Listener () {
			public void handleEvent (Event event) {
				switch (event.detail) {
					case SWT.TRAVERSE_ESCAPE:
						win.close ();
						event.detail = SWT.TRAVERSE_NONE;
						event.doit = false;
						break;
				}
			}
		});

		
		
		
		

		win.open();
		while (!win.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}


	protected void salva() {
		Database.creaTable(Database.TBL_CANZONE);
		
    // Creiamo la lista di patches
    StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listPatches.getItemCount(); i++) {
			if (i > 0)
				sb.append("|");
			sb.append(listPatches.getItems()[i]);
		}
    
		Database.queryUp(
				"INSERT INTO canzone(nome, lista_patch) VALUES (?, ?);",
				txtNome.getText(), sb.toString());
    		
	}
}
