package oberheditor.gui;

import org.eclipse.swt.widgets.*;

public class Main {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.init();
		
		Display display = new Display();
		//WinCanzone winCanzone = new WinCanzone(display);
		WinScaletta win = new WinScaletta(display);

	}

}
