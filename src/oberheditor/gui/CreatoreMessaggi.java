package oberheditor.gui;

import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import oberheditor.Scaletta;
import oberheditor.SysexReceiver;

public class CreatoreMessaggi {
	private Scaletta scaletta;
	private Vector<Byte> HEAD_MSG; 
	
	
	public CreatoreMessaggi(Scaletta scaletta) {
		// TODO Auto-generated constructor stub
		this.scaletta = scaletta;
		HEAD_MSG = new Vector<Byte>();
		HEAD_MSG.add((byte) 0xF0);
		HEAD_MSG.add((byte) 0x7E);
		HEAD_MSG.add((byte) 0x7F);
		HEAD_MSG.add((byte) 0x00);
		HEAD_MSG.add((byte) 0x02);
		HEAD_MSG.add((byte) 0x01);
	}

	public Vector<SysexMessage> creaMessaggi() {
		if (scaletta == null) {
			System.out.println("Errore: scaletta non caricata.");
			return null;
		}
		Vector<SysexMessage> result = new Vector<SysexMessage>();
		
		/****** HEADER *******/
		boolean finito = false;
		
		int id_chain = 0;
//		String nomeChain = "Silent BlackRose";
		String nomeChain = "SILENT MIDIAN";
		nomeChain = nomeChain.substring(0,12);
		// Pad right
		String.format("%1$-" + 12 + "s", nomeChain);
		// pad left: String.format("%1$#" + n + "s", s);
		
		int contatore = 12 * id_chain;
		
		while (!finito) {
			Vector<Byte> messaggio = new Vector<Byte>();
			messaggio.addAll(HEAD_MSG);
			// Calcolo i 4 bytes ADDR
			messaggio.addAll(calcolaAddrBytes(contatore, 0x7A, 0x75));
			
			char[] nomi = nomeChain.toCharArray();
			for (int i = 0; i < nomi.length; i++) {
				if (i % 7 == 0) {
					// metto la maschera, che qui è sempre 0x00
					messaggio.add((byte) 0x00);
				}
				messaggio.add((byte) nomi[i]);
			}
			
			// Byte di chiusura
			messaggio.add((byte) 0xF7);
			
			for (Byte miobyte : messaggio) {
				System.out.print(SysexReceiver.hexDigits[(miobyte & 0xF0) >> 4]);
				System.out.print(SysexReceiver.hexDigits[(miobyte & 0x0F)] + " ");

			}
			byte[] msgByteArray = new byte[messaggio.size()];
			for (int i = 0; i < messaggio.size(); i++) {
				msgByteArray[i] = messaggio.get(i).byteValue();
			}
			SysexMessage sysex = new SysexMessage();
			try {
				sysex.setMessage(msgByteArray, msgByteArray.length);
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
	
	private Vector<Byte> calcolaAddrBytes(int contatore, int start2, int start4) {
		Vector<Byte> result = new Vector<Byte>();
		
		String binario = Integer.toBinaryString(contatore);
		binario = "0000000000000000000000".substring(0, 20 - binario.length ()) + binario;
		String d = binario.substring(binario.length() - 7, binario.length());
		String c = binario.substring(binario.length() - 8, binario.length() - 7);
		String b = binario.substring(binario.length() - 15, binario.length() - 8);
		String a = binario.substring(0, binario.length() - 15);
		
		// ADDR1
		result.add((byte) (c.equals("0") ? 0x50 : 0x70));
		// ADDR2
		int val_b = Integer.parseInt(b, 2);
		result.add((byte) (start2 + val_b));
		// ADDR3
		result.add((byte) Integer.parseInt(d, 2));
		// ADDR4
		int val_a = Integer.parseInt(a, 2);
		result.add((byte) (start4 + val_a));
		return result;
	}

	public void setScaletta(Scaletta scaletta) {
		this.scaletta = scaletta;
	}

	public Scaletta getScaletta() {
		return scaletta;
	}

}
