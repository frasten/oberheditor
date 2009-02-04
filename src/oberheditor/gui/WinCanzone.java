package oberheditor.gui;

import java.sql.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;

import com.sun.org.apache.bcel.internal.generic.LSTORE;


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

		txtNome.setSize(new Point(240, txtNome.getSize().y));
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
				String string = "";
				int [] selection = listPatches.getSelectionIndices ();
				for (int i=0; i<selection.length; i++) string += selection [i] + " ";
				System.out.println ("Selection={" + string + "}");
			}
		});
		listPatches.addListener (SWT.DefaultSelection, new Listener () {
			public void handleEvent (Event e) {
				String string = "";
				int [] selection = listPatches.getSelectionIndices ();
				for (int i=0; i<selection.length; i++) string += selection [i] + " ";
				System.out.println ("DefaultSelection={" + string + "}");
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
		btnAdd.setText("Aggiungi");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				listPatches.add(cmbBanco.getItem(cmbBanco.getSelectionIndex()) + "-" +
						String.format("%03d", Integer.parseInt(txtPatch.getText()))
				);
				txtPatch.setSelection(txtPatch.getSelection() + 1);
				txtPatch.setFocus();
			}
		});
		btnAdd.pack();
		btnAdd.setLocation(250, 80);
		
		win.setDefaultButton(btnAdd);
		
		
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
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Main.NOME_DB);
	    Statement stat = conn.createStatement();
	    
	    stat.executeUpdate("CREATE TABLE IF NOT EXISTS canzone(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, lista_patch TEXT, lista_desc TEXT);");
	    PreparedStatement prep = conn.prepareStatement(
	        "INSERT INTO canzone(nome, lista_patch) VALUES (?, ?);");

	    prep.setString(1, txtNome.getText());
	    // Creiamo la lista di patches
	    StringBuilder sb = new StringBuilder();
			for (int i = 0; i < listPatches.getItemCount(); i++) {
				if (i > 0)
					sb.append("|");
				sb.append(listPatches.getItems()[i]);
			}
	    prep.setString(2, sb.toString());

	    conn.setAutoCommit(false);
	    prep.execute();
	    conn.setAutoCommit(true);

	    conn.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
	}
}
