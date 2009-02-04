package oberheditor;

/**
 * Voglio:
 * - leggere da una porta midi
 * - salvare su un file syx
 * - mostrare la scaletta ricevuta
 * - possibilità di modificarla etc
 * - boh
 * 
 */
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import java.io.IOException;
import java.util.Vector;
import java.io.*;

public class MidiReader {
	static Vector<MidiDevice> porte_in, porte_out;
	static Vector<SysexMessage> messaggi;
	public static final boolean DEBUG = true; 

	public static void main(String args[]) {
		porte_in = new Vector<MidiDevice>();
		porte_out = new Vector<MidiDevice>();
		
		MidiDevice.Info[] lista = MidiSystem.getMidiDeviceInfo();
		
		/*************************************************************
		 *              Ricerca porte disponibili
		 *************************************************************/
		for (MidiDevice.Info i : lista) {
			MidiDevice device = null;
			try {
				device = MidiSystem.getMidiDevice(i);
			} catch (MidiUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ( (device instanceof Sequencer) || (device instanceof Synthesizer)) {
				// Scarto le interfacce inutili, voglio solo vere porte MIDI.
				continue;
			}
			
			int numout = device.getMaxTransmitters();
			int numin = device.getMaxReceivers();
			if (numout == -1 || numout > 0) {
				/* Il contrario di quello che si potrebbe pensare, perche` 
				 * se un device ha un'uscita, e` quindi un ingresso. */
				porte_in.add(device);
				log(device.getDeviceInfo().getDescription());
				log("Porta " + device.getDeviceInfo().getName() + " aggiunta come MIDI IN.");
			}
			if (numin == -1 || numin > 0) {
				porte_out.add(device);
				log(device.getDeviceInfo().getDescription());
				log("Porta " + device.getDeviceInfo().getName() + " aggiunta come MIDI OUT.");
			}
		}
		
		/**
		 * Lettura dati
		 * */
		
		if (porte_in.size() <= 0) {
			System.out.println("Nessuna porta in ingresso disponibile.");
			return;
		}

		if (DEBUG) {
			System.out.println("PORTE IN:");
			for (MidiDevice dev : porte_in) {
				System.out.println(dev.getDeviceInfo());			
			}
			System.out.println("PORTE OUT:");
			for (MidiDevice dev : porte_out) {
				System.out.println(dev.getDeviceInfo());			
			}
		}
		
		// Leggo i dati dalla prima porta, per esempio
		SysexReceiver rcvr = new SysexReceiver();
		MidiDevice porta_in = porte_in.get(0);
		messaggi = new Vector<SysexMessage>();
		if (!(porta_in.isOpen())) {
		  try {
		  	porta_in.open();
		  	Transmitter tsmt = porta_in.getTransmitter();
		  	tsmt.setReceiver(rcvr);
		  	System.out.println("Lettura iniziata.");
		  	System.in.read(); //Questa e` la chiave!!! Istruzione bloccante
		  	resoconto();
		  	salvaSyx();
		  	
		  	porta_in.close();
			} catch (MidiUnavailableException e) {
				// Handle or throw exception...
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Non dovrei mostrare questo.");
		}
		
		//porta_in
		System.out.println("Bye Bye!");
	}
	
	private static void resoconto() {
		log("Salvati " + messaggi.size() + " messaggi SysEx.");
		int nBytes = 0;
		for (SysexMessage msg : messaggi) {
			nBytes += msg.getMessage().length;
		}
		log("Ricevuti " + nBytes + " bytes totali.");
	}

	private static void salvaSyx() {
		if (messaggi == null) return;
		if (messaggi.size() <= 0) {
			log("Nessun messaggio ricevuto da salvare.");
			return;
		}
		// Salvataggio
		FileOutputStream fos; 
    DataOutputStream dos;

    try {
      File file= new File("messaggi.syx");
      fos = new FileOutputStream(file);
      dos=new DataOutputStream(fos);

      // Salvo tutti i messaggi
			for (SysexMessage msg : messaggi) {
				dos.write(msg.getMessage());
			}
			dos.close();
			
    } catch (IOException e) {
      e.printStackTrace();
    }
		
	}

	public static void log(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
}
