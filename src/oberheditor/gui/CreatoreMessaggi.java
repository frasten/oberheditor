package oberheditor.gui;

import java.util.Arrays;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import oberheditor.Scaletta;
import oberheditor.SysexReceiver;

public class CreatoreMessaggi {
	private Scaletta scaletta;
	private byte[] HEAD_MSG; 
	
	
	public CreatoreMessaggi(Scaletta scaletta) {
		// TODO Auto-generated constructor stub
		this.scaletta = scaletta;
		
		HEAD_MSG = new byte[] {(byte) 0xF0, (byte) 0x7E, (byte) 0x7F, (byte) 0x00, (byte) 0x02, (byte) 0x01};
	}

	public Vector<SysexMessage> creaMessaggi() {
		int contatore;
		if (scaletta == null) {
			System.out.println("Errore: scaletta non caricata.");
			return null;
		}
		Vector<SysexMessage> result = new Vector<SysexMessage>();
		
		/********** TEEEEEEEEMPPPP ************/
		int id_chain = 11;
		//String nomeChain = "Silent BlackRose";
		String nomeChain = "SILENT MIDIAN";
		nomeChain = nomeChain.substring(0,12);
		String.format("%1$-" + 12 + "s", nomeChain); // Pad right
		// pad left: String.format("%1$#" + n + "s", s);
		/********** TEEEEEEEEMPPPP ************/
		
		
		/****** HEADER *******/
		boolean finito = false;
		
		
		contatore = 12 * id_chain;
		
		while (!finito) {
			int puntatore = 0;
			byte[] bytes = new byte[75];
			Arrays.fill(bytes, (byte)0xFF); // Valore non valido, per capire dove finisce
			for (int i = 0; i <= 5; i++) {
				bytes[i] = HEAD_MSG[i];
			}
			
			// Calcolo i 4 bytes ADDR
			byte[] addr = calcolaAddrBytes(contatore, 0x7A, 0x75);
			for (int i = 0; i < addr.length; i++) {
				bytes[6 + i] = addr[i];
			}
			
			puntatore = 10; // 6 head + 4 addr
			// Nome scaletta
			char[] nomi = nomeChain.toCharArray();
			for (int i = 0; i < nomi.length; i++) {
				if (i % 7 == 0) {
					// metto la maschera, che qui è sempre 0x00
					bytes[puntatore] = (byte) 0x00;
					puntatore++;
				}
				bytes[puntatore] = (byte) nomi[i];
				puntatore++;
			}
			
			// Byte di chiusura
			bytes[puntatore] = (byte) 0xF7;
			
			System.out.println(SysexReceiver.getHexString(bytes));
			
			SysexMessage sysex = new SysexMessage();
			try {
				sysex.setMessage(bytes, puntatore + 1);
				System.out.println(SysexReceiver.getHexString(sysex.getMessage()));
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			result.add(sysex);
			
			finito = true; // TODO non è vero, è solo il caso con una chain
		}
		
		/****** DATA *******/
		
		
		/****** FOOTER *******/
		
		
		return null;
	}
	
	private byte[] calcolaAddrBytes(int contatore, int start2, int start4) {
		byte[] result = new byte[4];
		int a = (contatore & ~0x7FFF) >> 15;
		int b = (contatore & 0x7F00) >> 8;
		int c = (contatore & 0x80) >> 7;
		int d = contatore & 0x7F;
		
		// ADDR1
		result[0] = (byte) (c== 0 ? 0x50 : 0x70);
		// ADDR2
		result[1] = (byte) (start2 + b);
		// ADDR3
		result[2] = (byte) d;
		// ADDR4
		result[3] = (byte) (start4 + a);
		return result;
	}

	public void setScaletta(Scaletta scaletta) {
		this.scaletta = scaletta;
	}

	public Scaletta getScaletta() {
		return scaletta;
	}

}
