package oberheditor.gui;

import oberheditor.Database;

import org.eclipse.swt.widgets.Display;

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

}
