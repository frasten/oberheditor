package oberheditor.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oberheditor.Canzone;
import oberheditor.Database;
import oberheditor.Scaletta;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;


public class WinScaletta {
	private int id_scaletta;
	Shell win; // La finestra stessa
	List listCanzoniDisponibili;
	List listCanzoniScaletta;
	Button btnAdd, btnMuoviSu, btnMuoviGiu, btnElimina;
	Text txtNome;
	
	ToolBar toolBar;
	Scaletta scaletta;
	Vector<Canzone> canzoni_disponibili;
	
	
	public WinScaletta(Shell parent, int ... id_scaletta) {
		win = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		win.setText("Scaletta");
		Display display = parent.getDisplay();
		
		
		int win_w = 700;
		int win_h = 500;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		FormLayout layout = new FormLayout();
		win.setLayout(layout);
		
		Menu bar = new Menu (win, SWT.BAR);
		win.setMenuBar (bar);
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("&File");
		Menu submenu = new Menu (win, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		MenuItem item = new MenuItem (submenu, SWT.PUSH);
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				scaletta.resetId();
				scaletta.salvaDB();
				win.close();
			}
		});
		item.setText ("Salva come nuovo"); // \tCtrl+A
		//item.setAccelerator (SWT.MOD1 + 'A');

		
		toolBar = new ToolBar (win, SWT.BORDER);
		for (int i=0; i<8; i++) {
			ToolItem toolitem = new ToolItem (toolBar, SWT.PUSH);
			toolitem.setText ("Item " + i);
		}
		FormData layToolBar = new FormData();
		layToolBar.left = new FormAttachment(0, 0);
		layToolBar.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(layToolBar);


		
		Label lblNome = new Label(win, SWT.NONE);
		lblNome.setText("Nome:");
		FormData layLblNome = new FormData();
		layLblNome.left = new FormAttachment(0, 10);
		layLblNome.top = new FormAttachment(toolBar, 10, SWT.BOTTOM);
		lblNome.setLayoutData(layLblNome);
		
		
		txtNome = new Text(win, SWT.BORDER);
		FormData layTxtNome = new FormData();
		layTxtNome.left = new FormAttachment(lblNome, 10, SWT.RIGHT);
		layTxtNome.top = new FormAttachment(lblNome, -4, SWT.TOP);
		layTxtNome.width = 250;
		txtNome.setLayoutData(layTxtNome);
		
		txtNome.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}

			public void focusLost(FocusEvent e) {
				scaletta.setNome(txtNome.getText());
			}
			
		});
		
		
		Label lblData = new Label(win, SWT.NONE);
		lblData.setText("Data:");
		FormData layLblData = new FormData();
		layLblData.left = new FormAttachment(txtNome, 10, SWT.RIGHT);
		layLblData.top = new FormAttachment(lblNome, 0, SWT.TOP);
		lblData.setLayoutData(layLblData);
		
		DateTime data = new DateTime (win, SWT.DATE | SWT.BORDER);
		FormData layData = new FormData();
		layData.left = new FormAttachment(lblData, 10, SWT.RIGHT);
		layData.top = new FormAttachment(txtNome, 0, SWT.TOP);
		data.setLayoutData(layData);
		
		
		/**************************************************
		 *             PULSANTI FINALI
		 *************************************************/
		Button btnSalva = new Button(win, SWT.PUSH);
		btnSalva.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				scaletta.salvaDB();
				win.close();
			}
		});
		Image imgSalva = new Image(display, "res/save.png");
		btnSalva.setImage(imgSalva);
		btnSalva.setText("Salva");
		FormData layBtnSalva = new FormData();
		layBtnSalva.right = new FormAttachment(100, -10);
		layBtnSalva.bottom = new FormAttachment(100, -10);
		btnSalva.setLayoutData(layBtnSalva);
		
		Button btnAnnulla = new Button(win, SWT.PUSH);
		btnAnnulla.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				win.close();
			}
		});
		Image imgAnnulla = new Image(display, "res/cancel.png");
		btnAnnulla.setImage(imgAnnulla);
		btnAnnulla.setText("Annulla");
		FormData layAnnulla = new FormData();
		layAnnulla.right = new FormAttachment(btnSalva, -10, SWT.LEFT);
		layAnnulla.top = new FormAttachment(btnSalva, 0, SWT.TOP);
		btnAnnulla.setLayoutData(layAnnulla);
		
		
		
		/*************************************************
		 *                  LISTE
		 *************************************************/
		listCanzoniScaletta = new List (win, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		FormData layListScaletta = new FormData();
		layListScaletta.left = new FormAttachment(lblNome, 0, SWT.LEFT);
		layListScaletta.top = new FormAttachment(lblNome, 20, SWT.BOTTOM);
		layListScaletta.width = 180;
		layListScaletta.bottom = new FormAttachment(btnAnnulla, -10, SWT.TOP);
		listCanzoniScaletta.setLayoutData(layListScaletta);
		
		
		listCanzoniScaletta.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				refreshTasti();
			}
		});
		
		
		listCanzoniDisponibili = new List (win, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		FormData layListDisponibili = new FormData();
		layListDisponibili.right = new FormAttachment(100, -10);
		layListDisponibili.top = new FormAttachment(listCanzoniScaletta, 0, SWT.TOP);
		layListDisponibili.width = 180;
		layListDisponibili.bottom = new FormAttachment(listCanzoniScaletta, 0, SWT.BOTTOM);
		listCanzoniDisponibili.setLayoutData(layListDisponibili);
		
		
		btnAdd = new Button(win, SWT.PUSH);
		Image imgAdd = new Image(display, "res/left.png");
		btnAdd.setText("Aggiungi");
		btnAdd.setImage(imgAdd);
		FormData layBtnAdd = new FormData();
		layBtnAdd.left = new FormAttachment(50, btnAdd.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / -2);
		layBtnAdd.top = new FormAttachment(listCanzoniDisponibili, 50, SWT.TOP);
		btnAdd.setLayoutData(layBtnAdd);
		

		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int startSelezione = listCanzoniScaletta.getSelectionIndex();
				for (int i = 0; i < listCanzoniDisponibili.getSelection().length; i++) {
					Canzone song = canzoni_disponibili.get(listCanzoniDisponibili.getSelectionIndices()[i]);
					listCanzoniScaletta.add(song.getNome(), startSelezione + 1 + i);
					scaletta.addCanzone(song, startSelezione + 1 + i);
				}
				listCanzoniScaletta.setSelection(startSelezione + 1, startSelezione + listCanzoniDisponibili.getSelection().length);
				refreshTasti();
			}
		});
		
		
		
		/********************************************
		 * CONTROLLI PER LA LISTA
		 ********************************************/
		btnMuoviSu = new Button(win, SWT.PUSH);
		btnMuoviSu.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String [] selezionati = listCanzoniScaletta.getSelection();
				// se non ho nessuna selezione, non faccio niente
				if (selezionati.length <= 0) return;
				int [] idSelezionati = listCanzoniScaletta.getSelectionIndices(); 
				// se sono gia' all'inizio della lista, non faccio niente
				if (idSelezionati[0] <= 0) return;
				
				for (int i = 0 ; i < selezionati.length; i++) {
					listCanzoniScaletta.add(listCanzoniScaletta.getItem(idSelezionati[i]-1), idSelezionati[i] + 1);
					scaletta.addCanzone(scaletta.getCanzoni().get(idSelezionati[i]-1), idSelezionati[i]+1);
					listCanzoniScaletta.remove(idSelezionati[i] -1);
					scaletta.rimuoviCanzone(idSelezionati[i] - 1);
				}
				refreshTasti();
			}
		});
		Image imgSu = new Image(display, "res/up.png");
		btnMuoviSu.setImage(imgSu);
		FormData layBtnMuoviSu = new FormData();
		layBtnMuoviSu.left = new FormAttachment(listCanzoniScaletta, 10, SWT.RIGHT);
		layBtnMuoviSu.top = new FormAttachment(listCanzoniScaletta, 30, SWT.TOP);
		btnMuoviSu.setLayoutData(layBtnMuoviSu);
		
		
		
		
		btnMuoviGiu = new Button(win, SWT.PUSH);
		btnMuoviGiu.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String [] selezionati = listCanzoniScaletta.getSelection();
				// se non ho nessuna selezione, non faccio niente
				if (selezionati.length <= 0) return;
				int [] idSelezionati = listCanzoniScaletta.getSelectionIndices(); 
				// se sono gia' alla fine della lista, non faccio niente
				if (idSelezionati[idSelezionati.length - 1] >= listCanzoniScaletta.getItemCount() - 1) return;
				
				for (int i = selezionati.length -1 ; i >= 0; i--) {
					listCanzoniScaletta.add(listCanzoniScaletta.getItem(idSelezionati[i]+1), idSelezionati[i]);
					scaletta.addCanzone(scaletta.getCanzoni().get(idSelezionati[i]+1), idSelezionati[i]);
					listCanzoniScaletta.remove(idSelezionati[i] + 2);
					scaletta.rimuoviCanzone(idSelezionati[i] + 2);
				}
				refreshTasti();
			}
		});
		Image imgGiu = new Image(display, "res/down.png");
		btnMuoviGiu.setImage(imgGiu);
		FormData layBtnMuoviGiu = new FormData();
		layBtnMuoviGiu.left = new FormAttachment(btnMuoviSu, 0, SWT.LEFT);
		layBtnMuoviGiu.top = new FormAttachment(btnMuoviSu, 10, SWT.BOTTOM);
		btnMuoviGiu.setLayoutData(layBtnMuoviGiu);
		
		
		btnElimina = new Button(win, SWT.PUSH);
		Image imgElimina = new Image(display, "res/delete.png");
		btnElimina.setImage(imgElimina);
		FormData layBtnElimina = new FormData();
		layBtnElimina.left = new FormAttachment(btnMuoviSu, 0, SWT.LEFT);
		layBtnElimina.top = new FormAttachment(btnMuoviGiu, 10, SWT.BOTTOM);
		btnElimina.setLayoutData(layBtnElimina);
		

		btnElimina.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] selezione = listCanzoniScaletta.getSelectionIndices();
				for (int i = selezione.length -1; i >= 0; i--) {
					// Elimino gli elementi, sia dalla scaletta che dalla lista
					scaletta.rimuoviCanzone(selezione[i]);
					listCanzoniScaletta.remove(selezione[i]);
				}
				refreshTasti();
			}
		});
		
		
		
		
		
		
		caricaCanzoniDisponibili();
		if (id_scaletta.length > 0) {
			this.id_scaletta = id_scaletta[0];
			caricaScalettaDatabase(this.id_scaletta);
		}
		else {
			scaletta = new Scaletta();
		}
		
		refreshTasti();
		
		/*
		DateTime calendario = new DateTime (win, SWT.CALENDAR | SWT.BORDER);
		calendario.pack();
		calendario.setLocation(450, 43);
		*/
		//listCanzoniScaletta
		
		/*
		Group grpCanzoni = new Group(win, SWT.NONE);
		grpCanzoni.pack();
		grpCanzoni.setLocation(10, 65);
		*/
		
		
		win.open();
		while (!win.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
	}

	protected void refreshTasti() {
		if (listCanzoniScaletta.getSelectionCount() <= 0) {
			btnMuoviSu.setEnabled(false);
			btnMuoviGiu.setEnabled(false);
			btnElimina.setEnabled(false);
		}
		else {
			btnElimina.setEnabled(true);
			btnMuoviSu.setEnabled(!listCanzoniScaletta.isSelected(0));
			btnMuoviGiu.setEnabled(!listCanzoniScaletta.isSelected(listCanzoniScaletta.getItemCount()-1));
		}
		System.out.print("Nuovo ordine: ");
		for (Canzone song : scaletta.getCanzoni()) {
			System.out.print(song.getId()+"|");
		}
		System.out.println();
	}

	private void caricaCanzoniDisponibili() {
		Database.creaTable(Database.TBL_CANZONE);
		ResultSet res = Database.query("SELECT id FROM canzone ORDER BY nome ASC;");
		canzoni_disponibili = new Vector<Canzone>();
		
		try {
			Vector<Integer> canzoni = new Vector<Integer>();
			while (res.next()) {
				// Le carico in memoria
				canzoni.add(res.getInt("id"));
				
			}
			res.close();
			res.getStatement().close();
			for (Integer id : canzoni) {
				Canzone song = new Canzone(id);
				canzoni_disponibili.add(song);
				listCanzoniDisponibili.add(song.getNome());
			}
			
			listCanzoniDisponibili.select(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void caricaScalettaDatabase(int id) {
		
		scaletta = new Scaletta(id);
		txtNome.setText(scaletta.getNome());
		for (Canzone song : scaletta.getCanzoni()) {
			listCanzoniScaletta.add(song.getNome());
		}
		listCanzoniScaletta.select(0);
	}

}
