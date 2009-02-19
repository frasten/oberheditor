package oberheditor.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oberheditor.Database;
import oberheditor.Scaletta;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;


public class WinMain {
	Shell win;
	ToolBar toolBar;
	private Vector<Scaletta> scalette;
	private Table listScalette;
	
	public WinMain(Display display) {
		win = new Shell(display);
		win.setText("OberhEditor");
		
		int win_w = 780;
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
			}
		});
		item.setText ("Boh"); // \tCtrl+A
		//item.setAccelerator (SWT.MOD1 + 'A');
		
		Label lblScalette = new Label(win, SWT.NONE);
		lblScalette.setText("Scalette:");
		FormData layLblScalette = new FormData();
		layLblScalette.left = new FormAttachment(0, 10);
		layLblScalette.top = new FormAttachment(0, 10);
		lblScalette.setLayoutData(layLblScalette);
		
		
		
		
		listScalette = new Table (win, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		listScalette.setLinesVisible (true);
		listScalette.setHeaderVisible (true);
		// Imposto le intestazioni
		String[] titles = {"Nome", "Data"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (listScalette, SWT.NONE);
			column.setText (titles [i]);
		}
		
		listScalette.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				// TODO: aggiornare i pulsanti
			}
		});
		
		
		FormData layListScalette = new FormData();
		layListScalette.left = new FormAttachment(lblScalette, 0, SWT.LEFT);
		layListScalette.top = new FormAttachment(lblScalette, 10, SWT.BOTTOM);
		layListScalette.width = 180;
		layListScalette.bottom = new FormAttachment(100, -30);
		listScalette.setLayoutData(layListScalette);
		// Imposto la dimensione delle colonne
		listScalette.getColumn(1).pack();
		listScalette.getColumn(0).setWidth(110);

		/******************************************
		 *      PULSANTI SCALETTE
		 ******************************************/
		Button btnNuovaScaletta = new Button(win, SWT.PUSH);
		btnNuovaScaletta.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WinScaletta(win);
			}
		});
		Image imgNuovo = new Image(display, "res/add.png");
		btnNuovaScaletta.setImage(imgNuovo);
		btnNuovaScaletta.setText("Nuova Scaletta");
		FormData layBtnNuovaScaletta = new FormData();
		layBtnNuovaScaletta.left = new FormAttachment(listScalette, 10, SWT.RIGHT);
		layBtnNuovaScaletta.top = new FormAttachment(listScalette, 20, SWT.TOP);
		layBtnNuovaScaletta.width = 150;
		btnNuovaScaletta.setLayoutData(layBtnNuovaScaletta);
		
		Button btnModificaScaletta = new Button(win, SWT.PUSH);
		btnModificaScaletta.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (listScalette.getSelectionCount() <= 0) return;
				new WinScaletta(win, scalette.get(listScalette.getSelectionIndex()).getId());
			}
		});
		Image imgModifica = new Image(display, "res/edit.png");
		btnModificaScaletta.setImage(imgModifica);
		btnModificaScaletta.setText("Modifica Scaletta");
		FormData layBtnModificaScaletta = new FormData();
		layBtnModificaScaletta.left = new FormAttachment(btnNuovaScaletta, 0, SWT.LEFT);
		layBtnModificaScaletta.top = new FormAttachment(btnNuovaScaletta, 10, SWT.BOTTOM);
		layBtnModificaScaletta.width = 150;
		btnModificaScaletta.setLayoutData(layBtnModificaScaletta);
		
		Button btnInviaScaletta = new Button(win, SWT.PUSH);
		btnInviaScaletta.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (listScalette.getSelectionCount() <= 0) return;
				new WinInviaSysex(win, scalette.get(listScalette.getSelectionIndex()));
			}
		});
		Image imgInvia = new Image(display, "res/send.png");
		btnInviaScaletta.setImage(imgInvia);
		btnInviaScaletta.setText("Invia alla tastiera");
		FormData layBtnInviaScaletta = new FormData();
		layBtnInviaScaletta.left = new FormAttachment(btnNuovaScaletta, 0, SWT.LEFT);
		layBtnInviaScaletta.top = new FormAttachment(btnModificaScaletta, 10, SWT.BOTTOM);
		layBtnInviaScaletta.width = 150;
		btnInviaScaletta.setLayoutData(layBtnInviaScaletta);
		
		
		Button btnEliminaScaletta = new Button(win, SWT.PUSH);
		btnEliminaScaletta.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		Image imgElimina = new Image(display, "res/delete.png");
		btnEliminaScaletta.setImage(imgElimina);
		btnEliminaScaletta.setText("Elimina Scaletta");
		FormData layBtnEliminaScaletta = new FormData();
		layBtnEliminaScaletta.left = new FormAttachment(btnNuovaScaletta, 0, SWT.LEFT);
		layBtnEliminaScaletta.top = new FormAttachment(btnInviaScaletta, 10, SWT.BOTTOM);
		layBtnEliminaScaletta.width = 150;
		btnEliminaScaletta.setLayoutData(layBtnEliminaScaletta);
		
		caricaScalette();
		
		
		
		
		/**********************************************************
		 *                  CANZONI
		 **********************************************************/
		
		
		List listCanzoni = new List(win, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		FormData layListCanzoni = new FormData();
		layListCanzoni.left = new FormAttachment(50, 0);
		layListCanzoni.top = new FormAttachment(listScalette, 0, SWT.TOP);
		layListCanzoni.width = 180;
		layListCanzoni.bottom = new FormAttachment(listScalette, 0, SWT.BOTTOM);
		listCanzoni.setLayoutData(layListCanzoni);
		
		
		listCanzoni.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				
			}
		});
		
		
		
		win.open();
		while (!win.isDisposed())
			if (!display.readAndDispatch()) display.sleep();
		display.dispose();
	}

	private void caricaScalette() {
		// TODO Auto-generated method stub
		Database.creaTable(Database.TBL_CANZONE);
		ResultSet res = Database.query("SELECT id FROM scaletta ORDER BY data DESC;");
		scalette = new Vector<Scaletta>();
		
		try {
			Vector<Integer> id_scalette = new Vector<Integer>();
			while (res.next()) {
				// Le carico in memoria
				id_scalette.add(res.getInt("id"));
			}
			res.close();
			res.getStatement().close();
			for (Integer id : id_scalette) {
				Scaletta sc = new Scaletta(id);
				scalette.add(sc);
				TableItem item = new TableItem (listScalette, SWT.NONE);
				item.setText (0, sc.getNome());
				item.setText (1, ""/*sc.getData().toString()*/);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
