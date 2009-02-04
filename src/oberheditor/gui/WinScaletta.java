package oberheditor.gui;

import org.eclipse.swt.widgets.*;

public class WinScaletta {
	Shell win; // La finestra stessa
	
	public WinScaletta(Display display) {
		win = new Shell(display);
		win.setText("Scaletta");
		
		int win_w = 400;
		int win_h = 400;
		// La metto centrata
		int pos_x = (display.getBounds().width - win_w) / 2;
		int pos_y = (display.getBounds().height - win_h) / 2;
		win.setBounds(pos_x, pos_y, win_w, win_h);
		
		caricaCanzoniDisponibili();
		
		
		win.open();
		while (!win.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

	private void caricaCanzoniDisponibili() {
		Database.creaTable(Database.TBL_CANZONE | Database.TBL_SCALETTA | Database.TBL_SCALETTA_CANZONE);

	}

}
