package oberheditor;

import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

public class Scaletta {
	private Vector<Canzone> canzoni;
	private Date data;
	private String nome;
	
	public Scaletta(String nome) {
		this();
		this.setNome(nome);
	}
	
	public Scaletta() {
		canzoni = new Vector<Canzone>();
	}

	public Vector<Canzone> getCanzoni() {
		return canzoni;
	}

	public void setCanzoni(Vector<Canzone> canzoni) {
		this.canzoni = canzoni;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getData() {
		return data;
	}
	
	public void addCanzone(Canzone canzone) {
		canzoni.add(canzone);
	}
	
	public byte[] toByteArray() {
		byte[] result = new byte[512];// 256 posizioni * 2 bytes l'una
		/* Lo riempio con il valore 0xFF, non valido, cosi' posso capire
		 * quando son finiti i valori veri e quando iniziare a riempire con 0x7F */
		Arrays.fill(result, (byte) 0xFF);
		
		int contatore = 0;
		for (Canzone song : this.canzoni) {
			for (String patch : song.getPatches()) {
				// Banco
				result[contatore++] = (byte) ((int) patch.charAt(0) - (int) 'A' + 0x41);
				// Patch
				result[contatore++] = (byte) Integer.parseInt(patch.substring(2)); // FIXME: +1???
			}
		}
		return result;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
	
}
