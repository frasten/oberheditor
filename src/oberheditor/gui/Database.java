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
	
	public static boolean query(String query, String ... args) {
		try {
			//Statement stat = conn.createStatement();
			PreparedStatement prep = conn.prepareStatement(query);
			//stat.executeUpdate(query);
			for (int i = 0; i < args.length; i++) {
				prep.setString(i+1, new String(args[i]));
			}
			
			
			conn.setAutoCommit(false);
	    prep.execute();
	    conn.setAutoCommit(true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean querySel(String query) {
		try {
			Statement stat = conn.createStatement();
			stat.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void creaTable(int tabella) {
		
		
		if ((TBL_CANZONE & tabella) != 0) {
			query("CREATE TABLE IF NOT EXISTS canzone(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, lista_patch TEXT, lista_desc TEXT);");
		}
		if ((TBL_SCALETTA & tabella) != 0) {
			
		}
		if ((TBL_SCALETTA_CANZONE & tabella) != 0) {
			
		}
		
	}
	//stat.executeUpdate("CREATE TABLE IF NOT EXISTS canzone(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, lista_patch TEXT, lista_desc TEXT);");
	
	
}
