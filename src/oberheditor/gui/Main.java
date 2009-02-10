package oberheditor.gui;

import java.util.Vector;

import javax.sound.midi.SysexMessage;

import oberheditor.Canzone;
import oberheditor.Scaletta;

import org.eclipse.swt.widgets.*;

public class Main {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.init();
		
		Scaletta scaletta = new Scaletta();
		for (int i = 1;i <= 10; i++) {
			Canzone song = new Canzone();
			scaletta.addCanzone(song);
		}
		
		CreatoreMessaggi cm = new CreatoreMessaggi(scaletta);
		Vector<SysexMessage> messaggi = cm.creaMessaggi();
		
		Display display = new Display();
		//new WinCanzone(display);
		//new WinScaletta(display);

	}

}
