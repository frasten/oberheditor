package oberheditor.gui;

import oberheditor.Database;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.init();
		//Database.crea();
		oberheditor.midi.MidiCommon.initPorteOut();
		
		mostraInterfaccia();
	}

	private static void mostraInterfaccia() {
		Display display = new Display();
		new WinMain(display);
	}
	
	
	protected static void errorBox(Shell win, String messaggio) {
		MessageBox boxChiedi = new MessageBox(win, SWT.ICON_ERROR | SWT.OK);
		boxChiedi.setText("Errore");
		boxChiedi.setMessage(messaggio);
		boxChiedi.open();
	}

}
