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
		
		mostraInterfaccia();
	}

	private static void mostraInterfaccia() {
		Display display = new Display();
		new WinMain(display);
	}

}
