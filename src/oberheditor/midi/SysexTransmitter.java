package oberheditor.midi;

import java.util.Vector;

import javax.sound.midi.*;


public class SysexTransmitter {
	private Receiver rcvr = null;
	private MidiDevice porta_out = null;
	
	
	public boolean invia(SysexMessage messaggio) {
		if (rcvr == null) {
			if (!initReceiver()) return false;
		}
		rcvr.send(messaggio, -1);
		return true;
	}
	
	
	private boolean initReceiver() {
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
		
		if (porte_out.size() <= 0) {
			System.out.println("Nessuna porta in uscita disponibile.");
			return false;
		}
		
		// Leggo i dati dalla prima porta, per esempio
		porta_out = porte_out.get(0);
		
		if (!(porta_out.isOpen())) {
		  try {
		  	porta_out.open();
		  	rcvr = porta_out.getReceiver();
		  	if (rcvr != null) return true;
		  } catch (MidiUnavailableException e) {
		  	// Handle or throw exception...
		  	e.printStackTrace();
		  }
		} else {
			System.out.println("Non dovrei mostrare questo.");
		}		
		
		return false;
	}


	public boolean inviaTutti(Vector<SysexMessage> messaggi) {
		if (rcvr == null) {
			if (!initReceiver()) return false;
		}
		for (SysexMessage msg : messaggi) {
			rcvr.send(msg, -1);
		} 	
		close();
		return true;
	}
	
	public void close() {
		if (rcvr != null) rcvr.close();
  	if (porta_out != null)
  		porta_out.close();
	}
}
