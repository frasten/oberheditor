package oberheditor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Scaletta {
	private Vector<Canzone> canzoni;
	private GregorianCalendar data;
	private String nome;
	private int id;
	
	public Scaletta(String nome) {
		this();
		this.setNome(nome);
	}
	
	public Scaletta() {
		canzoni = new Vector<Canzone>();
		data = new GregorianCalendar();
	}

	/**
	 * Creo una scaletta caricandola dal database.
	 * @param id L'id della scaletta
	 * @throws SQLException 
	 */
	public Scaletta(int id) throws SQLException {
		this();
		if (id <= 0) throw new IllegalArgumentException("L'id deve essere > 0.");
		Database.creaTable(Database.TBL_CANZONE | Database.TBL_SCALETTA | Database.TBL_SCALETTA_CANZONE);
		ResultSet rs;
		
		try {
			rs = Database.query("SELECT * FROM scaletta WHERE id = ?", id + "");
			boolean trovato = false;
			while (rs.next()) {
				trovato = true;
				setId(rs.getInt("id"));
				setNome(rs.getString("nome"));
				String[] pezzi = rs.getString("data").split("-");
				if (pezzi.length == 3) {
					// Solo se ho una data regolare
					setData(new GregorianCalendar(Integer.parseInt(pezzi[0]),
						Integer.parseInt(pezzi[1]), Integer.parseInt(pezzi[2])));
				}
				else setData(new GregorianCalendar());
			}
			rs.close();
			rs.getStatement().close();

			if (!trovato) {
				 throw new IllegalArgumentException("Non c'è nessuna scaletta salvata con questo ID.");
			}
			
			// Carico la lista delle canzoni
			rs = Database.query("SELECT id_canzone FROM scaletta_canzone WHERE id_scaletta = ? ORDER BY ordine ASC", id + "");
			Vector<Integer> lista_id = new Vector<Integer>();
			while (rs.next()) {
				lista_id.add(rs.getInt("id_canzone"));
			}
			rs.close();
			rs.getStatement().close();
			for (Integer id_song : lista_id) {
				Canzone song = new Canzone(id_song);
				addCanzone(song);				
			}
			
			
		} catch (SQLException e) {
			throw e;
		}
	  

	}

	public Vector<Canzone> getCanzoni() {
		return canzoni;
	}

	public void setCanzoni(Vector<Canzone> canzoni) {
		this.canzoni = canzoni;
	}

	public void setData(GregorianCalendar data) {
		this.data = data;
	}

	public GregorianCalendar getData() {
		return data;
	}
	
	public void addCanzone(Canzone canzone) {
		canzoni.add(canzone);
	}
	
	/**
	 * 
	 * @param canzone
	 * @param indice la posizione nella scaletta, partendo da 0.
	 */
	public void addCanzone(Canzone canzone, int indice) {
		canzoni.add(indice, canzone);
	}
	
	public byte[] toByteArray() {
		byte[] result = new byte[512];// 256 posizioni * 2 bytes l'una
		/* Lo riempio con il valore 0xFF, che però essendo > 0x80 verrà
		 * trasformato in 0x7F in fase di codifica. */
		Arrays.fill(result, (byte) 0xFF);
		
		int contatore = 0;
		for (Canzone song : this.canzoni) {
			for (String patch : song.getPatches()) {
				if (contatore >= 512) {
					throw new IllegalArgumentException("Questa chain contiene piu` di 256 patches!");
				}
				// Banco
				result[contatore++] = (byte) ((int) patch.charAt(0) - (int) 'A' + 0x41);
				// Patch
				result[contatore++] = (byte) Integer.parseInt(patch.substring(2));
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
	
	public int getNumeroPatches() {
		int result = 0;
		for (Canzone song : this.canzoni) {
			result += song.getPatches().size();
		}
		return result;
	}
	
	
	public boolean salvaDB() throws SQLException {
		Database.creaTable(Database.TBL_CANZONE | Database.TBL_SCALETTA | Database.TBL_SCALETTA_CANZONE);
		
		// Salvo le canzoni
		for (Canzone canzone : getCanzoni()) {
			canzone.salvaDB();
		}
		// Salvo questa scaletta
		if (getId() > 0) {
    	// Gia' salvata nel db, updato
    	Database.queryUp(
  				"UPDATE scaletta SET nome=?, data=? WHERE id=?",
  				this.getNome(),
  				this.getData().get(Calendar.YEAR)+"-"+this.getData().get(Calendar.MONTH)+"-"+this.getData().get(Calendar.DATE),
  				getId()+"");
    }
    else {
    	// Nuova scaletta, la inserisco
    	int id = Database.queryUp(
  				"INSERT INTO scaletta(nome, data) VALUES (?, ?);",
  				this.getNome(),
  				this.getData().get(Calendar.YEAR)+"-"+this.getData().get(Calendar.MONTH)+"-"+this.getData().get(Calendar.DATE)		
    	);
  		setId(id);
    }
		if (getId() <= 0) {
			System.out.println("Errore nel salvataggio dei dati della scaletta nel database.");
			return false;
		}
		// Ordine della scaletta
		// Per prima cosa elimino il vecchio ordine, se ce n'era già uno
		Database.queryUp(
				"DELETE FROM scaletta_canzone WHERE id_scaletta = ?",
				Integer.toString(getId()));
		
		// Metto il nuovo ordine
		for (int i = 0; i < getCanzoni().size(); i++) {
			Database.queryUp(
					"INSERT INTO scaletta_canzone (id_scaletta, id_canzone, ordine) VALUES(?,?,?)",
					getId()+"", getCanzoni().get(i).getId()+"", i+"");
		}
		
		return true;
	}

	private void setId(int id) {
		if (id <= 0) throw new IllegalArgumentException();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void rimuoviCanzone(int indice) {
		this.canzoni.remove(indice);		
	}

	/**
	 * Serve per salvare la scaletta come una nuova.
	 */
	public void resetId() {
		this.id = 0;
	}
}
