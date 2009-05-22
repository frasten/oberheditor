package oberheditor.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import oberheditor.Canzone;
import oberheditor.Database;
import oberheditor.Scaletta;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;


public class WinScaletta {
	private int idScaletta;
	private Shell win, parent; // La finestra stessa
	private List listCanzoniDisponibili;
	private List listCanzoniScaletta;
	private Button chkNascondiUsate;
	private Button btnAdd, btnMuoviSu, btnMuoviGiu, btnElimina;
	private Text txtNome, data;
	
	private ToolBar toolBar;
	private Scaletta scaletta;
	private Vector<Canzone> canzoniDisponibili;
	private DateTime calendario;
	private Label statusBar;
	public boolean hoFattoModifiche = false;
	private Vector<Canzone> canzoniDisponibiliOriginale;
	
	
	public WinScaletta(Shell _parent, int ... id_scaletta) {
		this.parent = _parent;
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
				salvaScalettaEdEsci();
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


		
		statusBar = new Label(win, SWT.BORDER);
		FormData layStatusBar = new FormData();
		
		layStatusBar.left = new FormAttachment(0);
		layStatusBar.right = new FormAttachment(100);
		layStatusBar.bottom = new FormAttachment(100);
		statusBar.setLayoutData(layStatusBar);
		
		
		
		
		
		
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
		
		txtNome.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				scaletta.setNome(txtNome.getText());
			}
		});
		
		
		Label lblData = new Label(win, SWT.NONE);
		lblData.setText("Data:");
		FormData layLblData = new FormData();
		layLblData.left = new FormAttachment(txtNome, 10, SWT.RIGHT);
		layLblData.top = new FormAttachment(lblNome, 0, SWT.TOP);
		lblData.setLayoutData(layLblData);
		
		/****************************************************
		 *                  DATA
		 ****************************************************/
		calendario = new DateTime (win, SWT.CALENDAR | SWT.BORDER);
		calendario.setVisible(false);
		calendario.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				boolean chiudi = true;
				GregorianCalendar newData = new GregorianCalendar(calendario.getYear(),
      			calendario.getMonth(), calendario.getDay()); 
				// Evito la chiusura del calendario se sto solo cambiando mese o anno
				if (scaletta.getData() != null &&
						(scaletta.getData().get(Calendar.MONTH) != calendario.getMonth() ||
							scaletta.getData().get(Calendar.YEAR) != calendario.getYear()))
					chiudi = false;
				
				scaletta.setData(newData);
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				data.setText(format.format(scaletta.getData().getTime()));
				
				calendario.setVisible(!chiudi);
			}
		});
		
		calendario.addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event event) {
				calendario.setVisible(false);				
			}
		});
		
		
		data = new Text(win, SWT.BORDER);
		data.setEditable(false);
		// Mostro il calendario, su click
		data.addListener(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
				calendario.setVisible(true);
				calendario.forceFocus();
			}
		});
		
		FormData layData = new FormData();
		layData.left = new FormAttachment(lblData, 10, SWT.RIGHT);
		layData.top = new FormAttachment(txtNome, 0, SWT.TOP);
		layData.width = 80;
		data.setLayoutData(layData);
		
		
		
		FormData layCalendario = new FormData();
		layCalendario.left = new FormAttachment(data, 0, SWT.LEFT);
		layCalendario.top = new FormAttachment(data, 2, SWT.BOTTOM);
		calendario.setLayoutData(layCalendario);
		
		
		
				
		/**************************************************
		 *             PULSANTI FINALI
		 *************************************************/
		Button btnSalva = new Button(win, SWT.PUSH);
		btnSalva.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				salvaScalettaEdEsci();
			}
		});
		Image imgSalva = new Image(display, "res/save.png");
		btnSalva.setImage(imgSalva);
		btnSalva.setText("Salva");
		FormData layBtnSalva = new FormData();
		layBtnSalva.right = new FormAttachment(100, -10);
		layBtnSalva.bottom = new FormAttachment(statusBar, -10, SWT.TOP);
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
				refreshControlli();
			}
		});
		
		
		chkNascondiUsate = new Button(win, SWT.CHECK);
		chkNascondiUsate.setText("Nascondi già utilizzate");
		FormData layChkNascondiUsate = new FormData();
		layChkNascondiUsate.left = new FormAttachment(100, -217);
		layChkNascondiUsate.bottom = new FormAttachment(listCanzoniScaletta, 0, SWT.BOTTOM);
		chkNascondiUsate.setLayoutData(layChkNascondiUsate);
		
		chkNascondiUsate.addListener(SWT.Selection, new Listener () {
			@Override
			public void handleEvent(Event event) {
				refreshCanzoniDisponibili();
			}
		});
		
		// Di default, nascondo quelle già utilizzate
		chkNascondiUsate.setSelection(true);
		
		
		listCanzoniDisponibili = new List (win, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		FormData layListDisponibili = new FormData();
		layListDisponibili.right = new FormAttachment(100, -10);
		layListDisponibili.top = new FormAttachment(listCanzoniScaletta, 0, SWT.TOP);
		layListDisponibili.width = 180;
		layListDisponibili.bottom = new FormAttachment(chkNascondiUsate, -3, SWT.TOP);
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
					Canzone song = canzoniDisponibili.get(listCanzoniDisponibili.getSelectionIndices()[i]);
					listCanzoniScaletta.add(song.getNome(), startSelezione + 1 + i);
					scaletta.addCanzone(song, startSelezione + 1 + i);
				}
				listCanzoniScaletta.setSelection(startSelezione + 1, startSelezione + listCanzoniDisponibili.getSelection().length);
				
				if (chkNascondiUsate.getSelection()) {
					int[] selezione = listCanzoniDisponibili.getSelectionIndices();
					for (int i = selezione.length - 1; i >= 0; i--) {
						canzoniDisponibili.remove(selezione[i]);
						listCanzoniDisponibili.remove(selezione[i]);
					}
				}
				refreshControlli();
				updateStatusBar();
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
				refreshControlli();
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
				refreshControlli();
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
				refreshCanzoniDisponibili();
				updateStatusBar();
			}
		});
		
		
		
		
		caricaCanzoniDisponibili();
		if (id_scaletta.length > 0) {
			this.idScaletta = id_scaletta[0];
			caricaScalettaDatabase(this.idScaletta);
		}
		else {
			scaletta = new Scaletta();
		}
		
		refreshCanzoniDisponibili();
		updateStatusBar();
		
		win.open();
		while (!win.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep ();
		}
	}

	private void updateStatusBar() {
		int num_canzoni = listCanzoniScaletta.getItemCount();
		StringBuffer testo = new StringBuffer();
		testo.append(num_canzoni);
		testo.append(" canzon");
		testo.append(num_canzoni == 1 ? 'e' : 'i');
		testo.append(" nella scaletta.");

		if (scaletta.getNumeroPatches() > 256) {
			testo.append(" Attenzione: troppe patches!!!!");
			statusBar.setForeground(win.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		else {
			statusBar.setForeground(win.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		}
		
		statusBar.setText(testo.toString());
		
	}

	private void salvaScalettaEdEsci() {
		// Controlliamo che la scaletta non contenga piu' di 256 patches
		int numPatches = scaletta.getNumeroPatches();
		if (numPatches > 256) {
			System.out.println("troppe patches: "+ numPatches);
			return;
		}
		
		if (scaletta.salvaDB()) {
			this.hoFattoModifiche = true;
			win.close();
		}
	}

	protected void refreshControlli() {
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
		btnAdd.setEnabled(listCanzoniDisponibili.getItemCount() > 0);
		
	}

	private void refreshCanzoniDisponibili() {
		canzoniDisponibili.removeAllElements();
		listCanzoniDisponibili.removeAll();
		
		for (Canzone song : canzoniDisponibiliOriginale) {
			boolean gia_presente = false;
			if (scaletta != null) {
				for (Canzone tmp : scaletta.getCanzoni()) {
					if (tmp.getId() == song.getId()) {
						gia_presente = true;
						break;
					}
				}
			}
			
			if (!chkNascondiUsate.getSelection() || !gia_presente) {
				canzoniDisponibili.add(song);
				listCanzoniDisponibili.add(song.getNome());
			}
		}
		refreshControlli();
	}
	
	private void caricaCanzoniDisponibili() {
		Database.creaTable(Database.TBL_CANZONE);
		ResultSet res = Database.query("SELECT id FROM canzone ORDER BY nome ASC;");
		canzoniDisponibili = new Vector<Canzone>();
		canzoniDisponibiliOriginale = new Vector<Canzone>();
		
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
				canzoniDisponibiliOriginale.add(song);
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
		
		// Imposto la data
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		data.setText(format.format(scaletta.getData().getTime()));
		calendario.setDate(scaletta.getData().get(GregorianCalendar.YEAR),
				scaletta.getData().get(GregorianCalendar.MONTH),
				scaletta.getData().get(GregorianCalendar.DATE)
		);

		for (Canzone song : scaletta.getCanzoni()) {
			listCanzoniScaletta.add(song.getNome());
		}
		listCanzoniScaletta.select(0);
	}

}
