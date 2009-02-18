package oberheditor.gui;

import java.util.Vector;

import javax.sound.midi.SysexMessage;

import oberheditor.Database;
import oberheditor.Scaletta;
import oberheditor.midi.CreatoreMessaggi;
import oberheditor.midi.SysexTransmitter;

import org.eclipse.swt.widgets.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.init();
		//Database.crea();
		
		Scaletta scaletta = new Scaletta(2);
		
		CreatoreMessaggi cm = new CreatoreMessaggi(scaletta);
		Vector<SysexMessage> messaggi = cm.creaMessaggi();
		
		SysexTransmitter transmitter = new SysexTransmitter();
		transmitter.invia(messaggi);
		
		if (true) mostraInterfaccia();
	}

	private static void mostraInterfaccia() {
		Display display = new Display();
		//new WinCanzone(display);
		new WinScaletta(display, 2);
	}

}
