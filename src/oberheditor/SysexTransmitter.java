package oberheditor;

import java.util.Vector;

import javax.sound.midi.*;


public class SysexTransmitter {
	
	public void invia(Vector<SysexMessage> messaggi) {
		Vector<MidiDevice> porte_out = new Vector<MidiDevice>();
		
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
			
			int numin = device.getMaxReceivers();
			if (numin == -1 || numin > 0) {
				porte_out.add(device);
			}
		}
		
		/**
		 * Invio dati
		 * */
		
		if (porte_out.size() <= 0) {
			System.out.println("Nessuna porta in uscita disponibile.");
			return;
		}

		// Leggo i dati dalla prima porta, per esempio
		MidiDevice porta_out = porte_out.get(0);
		
		if (!(porta_out.isOpen())) {
		  try {
		  	porta_out.open();
		  	Receiver rcvr = porta_out.getReceiver();
		  	for (SysexMessage msg : messaggi) {
		  		rcvr.send(msg, -1);					
				}
		  	rcvr.close();
		  	if (porta_out != null)
		  		porta_out.close();
			} catch (MidiUnavailableException e) {
				// Handle or throw exception...
				e.printStackTrace();
			}
		} else {
			System.out.println("Non dovrei mostrare questo.");
		}
	}
}
