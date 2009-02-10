package oberheditor;

import java.util.Vector;

public class Canzone {
	private String nome;
	private Vector<String> patches;
	
	public Canzone(String nome) {
		this();
		this.nome = nome;
	}
	
	public Canzone() {
		
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
}
