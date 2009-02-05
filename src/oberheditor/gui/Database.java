package oberheditor.gui;

import java.sql.*;

public class Database {
	public final static int TBL_CANZONE = 1;
	public final static int TBL_SCALETTA = 2;
	public final static int TBL_SCALETTA_CANZONE = 4;
	final static String NOME_DB = "oberheim.db";
	
	
	private static Connection conn;
	
	public static void init() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection thisconn = DriverManager.getConnection("jdbc:sqlite:" + NOME_DB);
			conn = thisconn;
		} catch (ClassNotFoundException e) {
			System.out.println("Libreria sqlitejdbc mancante.");
			System.exit(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ResultSet query(String query, String ... args) {
		try {
			PreparedStatement prep = conn.prepareStatement(query);
			for (int i = 0; i < args.length; i++) {
				prep.setString(i+1, new String(args[i]));
			}
			
			ResultSet res = prep.executeQuery();
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static int queryUp(String query, String ... args) {
		try {
			PreparedStatement prep = conn.prepareStatement(query);
			for (int i = 0; i < args.length; i++) {
				prep.setString(i+1, new String(args[i]));
			}
			
			conn.setAutoCommit(false);
	    int result = prep.executeUpdate();
	    conn.setAutoCommit(true);
	    return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void creaTable(int tabella) {
		
		
		if ((tabella & TBL_CANZONE) != 0) {
			queryUp("CREATE TABLE IF NOT EXISTS canzone(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, lista_patch TEXT, lista_desc TEXT);");
		}
		if ((tabella & TBL_SCALETTA) != 0) {
			queryUp("CREATE TABLE IF NOT EXISTS scaletta(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, data TEXT);");
		}
		if ((tabella & TBL_SCALETTA_CANZONE) != 0) {
			queryUp("CREATE TABLE IF NOT EXISTS scaletta_canzone(id_scaletta INTEGER, id_canzone INTEGER, ordine INTEGER);");
		}
		
	}
	//stat.executeUpdate("CREATE TABLE IF NOT EXISTS canzone(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, lista_patch TEXT, lista_desc TEXT);");
	
	
}
