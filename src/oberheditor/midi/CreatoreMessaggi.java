package oberheditor.midi;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import oberheditor.Scaletta;

public class CreatoreMessaggi {
	private Scaletta scaletta;
	private byte[] HEAD_MSG;

	public CreatoreMessaggi(Scaletta scaletta) {
		this.scaletta = scaletta;

		HEAD_MSG = new byte[] { (byte) 0xF0, (byte) 0x7E, (byte) 0x7F, (byte) 0x00,
				(byte) 0x02, (byte) 0x01 };
	}

	public Vector<SysexMessage> creaMessaggi(int id_chain) {
		int contatore, puntatoreDati;
		byte[] dati;

		if (scaletta == null) {
			System.out.println("Errore: scaletta non caricata.");
			return null;
		}
		Vector<SysexMessage> result = new Vector<SysexMessage>();

		String nomeChain = this.scaletta.getNome();
		nomeChain = String.format("%1$-" + 12 + "s", nomeChain); // Pad right
		nomeChain = nomeChain.substring(0, 12);


		/****** HEADER *******/
		boolean finito = false;
		char[] nomi = nomeChain.toCharArray();
		dati = new byte[nomi.length];
		for (int i = 0; i < nomi.length; i++) {
			dati[i] = (byte) nomi[i];
		}

		contatore = 12 * id_chain;
		puntatoreDati = 0;
		while (!finito) {
			int puntatore = 0;
			byte[] bytes = new byte[75];
			for (int i = 0; i < 6; i++) {
				bytes[i] = HEAD_MSG[i];
			}

			// Calcolo i 4 bytes ADDR
			byte[] addr = calcolaAddrBytes(contatore, 0x7A, 0x75);
			for (int i = 0; i < addr.length; i++) {
				bytes[6 + i] = addr[i];
			}

			puntatore = 10; // 6 head + 4 addr

			for (int i = 0; i < dati.length; i++) {
				if (i % 7 == 0) {
					// metto il byte di codifica
					byte codifica = 0x00;
					for (int j = 1; j < 8; j++) {
						int n = 0;
						if ((puntatoreDati + j - 1) < dati.length
								&& dati[puntatoreDati + j - 1] >> 7 != 0)
							n = 0x01 << 8 - j - 1;
						codifica |= n;
					}
					bytes[puntatore++] = (byte) codifica;
				}
				// Aggiungo il dato, ponendone il primo bit a 0
				bytes[puntatore++] = (byte) (dati[puntatoreDati++] & 0x7F);

				if (puntatore >= bytes.length - 1) {
					// -1 perche` l'ultimo e' il byte di chiusura
					// Continuo al prossimo giro
					break;
				}
				if (puntatoreDati >= dati.length) {
					finito = true;
					break; // finito
				}
			}

			// Byte di chiusura
			bytes[puntatore] = (byte) 0xF7;

			// System.out.println(SysexReceiver.getHexString(bytes));

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
		}

		/****** DATA *******/
		finito = false;
		contatore = 512 * id_chain;

		// Prendo i dati veri e propri
		dati = scaletta.toByteArray();
		if (dati.length > 512) {
			throw new IllegalArgumentException("Questa chain contiene piu` di 256 patches!");
		}
		puntatoreDati = 0;

		while (!finito) {
			int puntatore = 0;
			byte[] bytes = new byte[75];
			for (int i = 0; i < 6; i++) {
				bytes[i] = HEAD_MSG[i];
			}

			// Calcolo i 4 bytes ADDR
			byte[] addr = calcolaAddrBytes(contatore, 0x00, 0x70);
			for (int i = 0; i < addr.length; i++) {
				bytes[6 + i] = addr[i];
			}

			puntatore = 10; // 6 head + 4 addr
			// Dati veri e propri
			for (int i = 0; i < dati.length; i++) {
				if (i % 7 == 0) {
					// metto il byte di codifica
					byte codifica = 0x00;
					for (int j = 1; j < 8; j++) {
						int n = 0;
						if ((puntatoreDati + j - 1) < dati.length
								&& dati[puntatoreDati + j - 1] >> 7 != 0)
							n = 0x01 << 8 - j - 1;
						codifica |= n;
					}
					bytes[puntatore++] = (byte) codifica;
				}
				// Aggiungo il dato, ponendone il primo bit a 0
				bytes[puntatore++] = (byte) (dati[puntatoreDati++] & 0x7F);

				if (puntatore >= bytes.length - 1) { // -1 perche` l'ultimo e' il byte
					// di chiusura
					// Continuo al prossimo giro
					break;
				}
				if (puntatoreDati >= dati.length) {
					finito = true;
					break; // finito
				}
			}

			// Byte di chiusura
			bytes[puntatore] = (byte) 0xF7;

			contatore += 0x38;

			// System.out.println(SysexReceiver.getHexString(bytes));

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
		}

		/****** FOOTER *******/
		finito = false;
		contatore = 2 * id_chain;

		dati = new byte[2]; // fisso, sarà 2 * quantità chains
		for (int i = 0; i < dati.length; i++) {
			// Controllo per il cambio: 0x02 = pedale 2
			dati[i] = (byte) (i % 2 == 0 ? 0x02 : 0x00);
		}

		puntatoreDati = 0;
		while (!finito) {
			int puntatore = 0;
			byte[] bytes = new byte[75];
			for (int i = 0; i < 6; i++) {
				bytes[i] = HEAD_MSG[i];
			}

			// Calcolo i 4 bytes ADDR
			byte[] addr = calcolaAddrBytes(contatore, 0x79, 0x75);
			for (int i = 0; i < addr.length; i++) {
				bytes[6 + i] = addr[i];
			}

			puntatore = 10; // 6 head + 4 addr
			// Dati veri e propri
			for (int i = 0; i < dati.length; i++) {
				if (i % 7 == 0) {
					// metto il byte di codifica
					byte codifica = 0x00;
					for (int j = 1; j < 8; j++) {
						int n = 0;
						if ((puntatoreDati + j - 1) < dati.length
								&& dati[puntatoreDati + j - 1] >> 7 != 0)
							n = 0x01 << 8 - j - 1;
						codifica |= n;
					}
					bytes[puntatore++] = (byte) codifica;
				}
				// Aggiungo il dato, ponendone il primo bit a 0
				bytes[puntatore++] = (byte) (dati[puntatoreDati++] & 0x7F);

				if (puntatore >= bytes.length - 1) { // -1 perche` l'ultimo e' il byte
					// di chiusura
					// Continuo al prossimo giro
					break;
				}
				if (puntatoreDati >= dati.length) {
					finito = true;
					break; // finito
				}
			}

			// Byte di chiusura
			bytes[puntatore] = (byte) 0xF7;

			contatore += 0x38;

			// System.out.println(SysexReceiver.getHexString(bytes));

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
		}

		return result;
	}

	public void salvaSyx(Vector<SysexMessage> messaggi, String path) {
		// Salvataggio
		FileOutputStream fos;
		DataOutputStream dos;

		try {
			File file = new File(path);
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);

			// Salvo tutti i messaggi
			for (SysexMessage msg : messaggi) {
				dos.write(msg.getMessage());
			}
			dos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private byte[] calcolaAddrBytes(int contatore, int start2, int start4) {
		byte[] result = new byte[4];
		int a = (contatore & ~0x7FFF) >> 15;
		int b = (contatore & 0x7F00) >> 8;
		int c = (contatore & 0x80) >> 7;
		int d = contatore & 0x7F;

		// ADDR1
		result[0] = (byte) (c == 0 ? 0x50 : 0x70);
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
