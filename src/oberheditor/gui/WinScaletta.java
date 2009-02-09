package oberheditor.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.sun.org.apache.bcel.internal.generic.LSTORE;

public class WinScaletta {
	Shell win; // La finestra stessa
	List listCanzoniScaletta;
	List listCanzoniDisponibili;
	ToolBar toolBar;
	Hashtable<Integer, String> canzoni_disponibili;
	
	
	public WinScaletta(Display display) {
		win = new Shell(display);
		win.setText("Scaletta");
		canzoni_disponibili = new Hashtable<Integer, String>();
		
		int win_w = 700;
		int win_h = 500;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		win.setLayout(layout);

		caricaCanzoniDisponibili();
		// Oltre alle due liste, ho anche due vettori
		//listCanzoniDisponibili.getVerticalBar().
		/*Vector<String> gino = null;
		gino.add(123, new String());
		*/
		Menu bar = new Menu (win, SWT.BAR);
		win.setMenuBar (bar);
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("&File");
		Menu submenu = new Menu (win, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		MenuItem item = new MenuItem (submenu, SWT.PUSH);
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				System.out.println ("Select All");
			}
		});
		item.setText ("Select &All\tCtrl+A");
		item.setAccelerator (SWT.MOD1 + 'A');

		
		toolBar = new ToolBar (win, SWT.BORDER);
		for (int i=0; i<8; i++) {
			ToolItem toolitem = new ToolItem (toolBar, SWT.PUSH);
			toolitem.setText ("Item " + i);
		}
		toolBar.pack ();

		
		
		Label lblNome = new Label(win, SWT.NONE);
		lblNome.setText("Nome:");
		lblNome.pack();
		lblNome.setLocation(10,43);
		
		Text txtNome = new Text(win, SWT.BORDER);
		txtNome.pack();
		txtNome.setSize(300, txtNome.getSize().y);
		txtNome.setLocation(60, 40);
		
		Label lblData = new Label(win, SWT.NONE);
		lblData.setText("Data:");
		lblData.pack();
		lblData.setLocation(380, 43);
		
		DateTime data = new DateTime (win, SWT.DATE | SWT.BORDER);
		data.pack();
		data.setLocation(450, 43);
		
		
		DateTime calendario = new DateTime (win, SWT.CALENDAR | SWT.BORDER);
		calendario.pack();
		calendario.setLocation(450, 43);
		
		//listCanzoniScaletta
		
		Group grpCanzoni = new Group(win, SWT.NONE);
		grpCanzoni.pack();
		grpCanzoni.setLocation(10, 65);
		
		
		
		win.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				Rectangle rect = win.getClientArea ();
				Point size = toolBar.computeSize (rect.width, SWT.DEFAULT);
				toolBar.setSize (size);
			}
		});

		
		
		win.open();
		while (!win.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

	private void caricaCanzoniDisponibili() {
		Database.creaTable(Database.TBL_CANZONE | Database.TBL_SCALETTA | Database.TBL_SCALETTA_CANZONE);
		ResultSet res = Database.query("SELECT * FROM canzone;");
		
		try {
			while (res.next()) {
				// Le carico in memoria
				canzoni_disponibili.put(new Integer(res.getInt("id")), res.getString("nome"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
