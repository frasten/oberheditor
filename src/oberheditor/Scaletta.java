package oberheditor;

import java.util.Date;
import java.util.Vector;

public class Scaletta {
	Vector<Canzone> canzoni;
	private Date data;
	
	public Scaletta() {
		// TODO Auto-generated constructor stub
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
	
}
