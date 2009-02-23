package oberheditor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Entita' per rappresentare una canzone, con relativo elenco di patch.
 * @author frasten
 *
 */
public class Canzone {
	private String nome;
	private Vector<String> patches;
	private int id = 0;
	
	/**
	 * Crea una canzone con lista patch vuota, a partire dal nome.
	 * @param nome il nome della canzone.
	 */
	public Canzone(String nome) {
		this();
		this.nome = nome;
	}
	
	/**
	 * Crea una canzone con lista patch vuota.
	 */
	public Canzone() {
		
	}
	
	/**
	 * Creo una canzone caricandola dal database.
	 * @param id L'id della canzone.
	 */
	public Canzone(int id) {
		this();
		if (id <= 0) throw new IllegalArgumentException("L'id deve essere > 0.");
		
		Database.creaTable(Database.TBL_CANZONE);
		ResultSet rs;
		
		try {
			rs = Database.query("SELECT * FROM canzone WHERE id = ?", id + "");
			boolean trovato = false;
			while (rs.next()) {
				trovato = true;
				setId(rs.getInt("id"));
				setNome(rs.getString("nome"));
				String[] listaPatch = rs.getString("lista_patch").split("\\|");
				setPatches(listaPatch);
			}
			rs.close();
			rs.getStatement().close();

			if (!trovato) {
				 throw new IllegalArgumentException("Non c'è nessuna scaletta salvata con questo ID.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Restituisce il nome della canzone.
	 * @return il nome della canzone.
	 */
	public String getNome() {
		return nome;
	}


	/**
	 * Imposta il nome della canzone.
	 * @param nome il nome della canzone.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Imposta la lista delle patch. Ogni patch e' cosi' formata:
	 * <em>LETTERA_BANCO</em>-<em>TRE_CIFRE_PATCH</em>
	 * Esempio:
	 * A-020
	 * @param patches la lista delle patch di questa scaletta.
	 */
	public void setPatches(Vector<String> patches) {
		this.patches = patches;
	}
	
	/**
	 * Imposta la lista delle patch. Ogni patch e' cosi' formata:
	 * <em>LETTERA_BANCO</em>-<em>TRE_CIFRE_PATCH</em>
	 * Esempio:
	 * A-020
	 * @param patches la lista delle patch di questa scaletta.
	 */
	public void setPatches(String[] patches) {
		this.patches = new Vector<String>();
		for (int i = 0; i < patches.length; i++) {
			this.patches.add(patches[i]);
		}
	}

	/**
	 * Restituisce la lista delle patch di questa canzone.
	 * @return la lista delle patch.
	 */
	public Vector<String> getPatches() {
		return patches;
	}
	
	/**
	 * Salva nel database i dati di questa canzone. Se e' una nuova
	 * canzone, la inserisce, altrimenti la aggiorna.
	 * @return <em>true</em> in caso di successo, altrimenti <em>false</em>.
	 */
	public boolean salvaDB() {
		Database.creaTable(Database.TBL_CANZONE);
		
    // Creiamo la lista di patches
    StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.patches.size(); i++) {
			if (i > 0)
				sb.append("|");
			sb.append(this.patches.get(i));
		}
    if (getId() > 0) {
    	// Gia' salvata nel db, updato
    	Database.queryUp(
  				"UPDATE canzone SET nome=?, lista_patch=? WHERE id=?",
  				this.getNome(), sb.toString(), Integer.toString(getId()));
    }
    else {
    	// Nuova canzone, la inserisco
    	int id = Database.queryUp(
  				"INSERT INTO canzone(nome, lista_patch) VALUES (?, ?);",
  				this.getNome(), sb.toString());
  		setId(id);
    }
				
		return false; // TODO: ritornare qualcosa
	}

	/**
	 * Imposta l'ID del database di questa canzone.
	 * @param id l'id nel database di questa canzone.
	 */
	private void setId(int id) {
		if (id <= 0) throw new IllegalArgumentException();
		this.id = id;
	}

	/**
	 * Restituisce l'ID di questa canzone nel database.
	 * @return l'id.
	 */
	public int getId() {
		return id;
	}
}
