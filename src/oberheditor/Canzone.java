package oberheditor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Canzone {
	private String nome;
	private Vector<String> patches;
	private int id = 0;
	
	public Canzone(String nome) {
		this();
		this.nome = nome;
	}
	
	public Canzone() {
		
	}
	
	/**
	 * Creo una canzone caricandola dal database.
	 * @param id L'id della canzone.
	 */
	public Canzone(int id) {
		this();
		if (id <= 0) throw new IllegalArgumentException("L'id deve essere > 0.");
		
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

	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPatches(Vector<String> patches) {
		this.patches = patches;
	}
	
	public void setPatches(String[] patches) {
		this.patches = new Vector<String>();
		for (int i = 0; i < patches.length; i++) {
			this.patches.add(patches[i]);
		}
	}

	public Vector<String> getPatches() {
		return patches;
	}
	
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

	private void setId(int id) {
		if (id <= 0) throw new IllegalArgumentException();
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
