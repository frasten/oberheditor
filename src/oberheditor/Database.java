package oberheditor;

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
	    prep.executeUpdate();
	    ResultSet rs = prep.getGeneratedKeys();
	    int result = rs.getInt(1);
	    rs.close();
	    conn.setAutoCommit(true);
	    
	    prep.close();
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
	
	
	/**
	 * Creo il database e lo popolo
	 */
	public static void crea() {
		Database.creaTable(Database.TBL_CANZONE | Database.TBL_SCALETTA | Database.TBL_SCALETTA_CANZONE);
		queryUp("DELETE FROM scaletta");
		queryUp("DELETE FROM scaletta_canzone");
		queryUp("DELETE FROM canzone");
		
		Scaletta scaletta = new Scaletta("SL BlackRose");
		Canzone kinslayer = new Canzone("The Kinslayer");
		kinslayer.setPatches(new String[] {"A-021", "A-022", "A-023", "A-024"});
		Canzone shesMySin = new Canzone("She's My Sin");
		shesMySin.setPatches(new String[] {"A-025", "A-026", "A-027", "A-028", "A-029", "A-030", "A-031", "A-032", "A-033", "A-034", "A-025"});
		Canzone sacrament = new Canzone("Sacrament of Wilderness");
		sacrament.setPatches(new String[] {"A-035", "A-036", "A-037", "A-038", "A-039", "A-040", "A-041", "A-042", "A-043", "A-044", "A-045", "A-046"});
		Canzone elvenpath = new Canzone("Elvenpath");
		elvenpath.setPatches(new String[] {"A-058", "A-059", "A-060", "A-061", "A-062"});
		Canzone fantasmic = new Canzone("Fantasmic");
		fantasmic.setPatches(new String[] {"A-063", "A-064"});
		Canzone everdream = new Canzone("Ever Dream");
		everdream.setPatches(new String[] {"A-047", "A-048", "A-049", "A-050", "A-051", "A-052", "A-053", "A-054"});
		Canzone moondance = new Canzone("Moondance");
		moondance.setPatches(new String[] {"A-055", "A-056", "A-057"});
		Canzone deepSilent = new Canzone("Deep Silent Complete");
		deepSilent.setPatches(new String[] {"A-065", "A-066", "A-067", "A-068", "A-069", "A-070", "A-071", "A-072"});
		Canzone oceansoul = new Canzone("Oceansoul");
		oceansoul.setPatches(new String[] {"A-073", "A-074", "A-075", "A-076", "A-073", "A-074"});
		Canzone darkChest = new Canzone("Dark Chest of Wonders");
		darkChest.setPatches(new String[] {"A-077", "A-078", "A-079", "A-080", "A-081", "A-082", "A-083", "A-084", "A-085", "A-086", "A-087", "A-088", "A-089", "A-090", "A-091", "A-092"});
		Canzone sleepingSun = new Canzone("Sleeping Sun");
		sleepingSun.setPatches(new String[] {"A-093", "A-094", "A-095", "A-096", "A-097", "A-098"});
		Canzone overTheHills = new Canzone("Over the Hills and Far Away");
		overTheHills.setPatches(new String[] {"A-099", "A-100", "A-101", "A-102", "A-103", "A-104", "A-105", "A-106", "A-107", "A-108", "A-109", "A-110"});
		Canzone crimsonTide = new Canzone("Crimson Tide / Deep Blue Sea");
		crimsonTide.setPatches(new String[] {"A-111", "A-112", "A-113"});
		Canzone comeCoverMe = new Canzone("Come Cover Me");
		comeCoverMe.setPatches(new String[] {"A-114", "A-115", "A-116", "A-117", "A-118", "A-119", "A-120", "A-121", "A-122"});
		Canzone blessTheChild = new Canzone("Bless the Child");
		blessTheChild.setPatches(new String[] {"A-123", "A-124", "A-125", "A-126", "A-127"});
		Canzone nemo = new Canzone("Nemo");
		nemo.setPatches(new String[] {"B-001", "B-002", "B-003", "B-004", "B-005", "B-006", "B-007", "B-008", "B-009", "B-010", "B-011", "B-012", "B-013", "B-014", "B-001", "B-015"});
		Canzone amaranth = new Canzone("Amaranth");
		amaranth.setPatches(new String[] {"B-016", "B-017", "B-018", "B-019", "B-020", "B-021", "B-022", "B-023", "B-024"});
		Canzone wishmaster = new Canzone("Wishmaster");
		wishmaster.setPatches(new String[] {"B-025", "B-026", "B-027", "B-028", "B-029", "B-030", "B-031", "B-032"});
		Canzone wishIHadAnAngel = new Canzone("Wish I Had an Angel");
		wishIHadAnAngel.setPatches(new String[] {"B-033", "B-034", "B-035", "B-036", "B-037", "B-038", "B-039"});
		Canzone ghostLove = new Canzone("Ghost Love Score");
		ghostLove.setPatches(new String[] {"B-040", "B-041", "B-042", "B-043", "B-044", "B-045", "B-046", "B-047", "B-048", "B-049", "B-050"});
		Canzone endOfAllHope = new Canzone("End of All Hope");
		endOfAllHope.setPatches(new String[] {"B-051", "B-052", "B-053", "B-054", "B-055", "B-056"});
		
		scaletta.addCanzone(shesMySin);
		scaletta.addCanzone(kinslayer);
		scaletta.addCanzone(endOfAllHope);
		scaletta.addCanzone(sacrament);
		scaletta.addCanzone(everdream);
		scaletta.addCanzone(moondance);
		scaletta.addCanzone(elvenpath);
		scaletta.addCanzone(fantasmic);
		scaletta.addCanzone(deepSilent);
		scaletta.addCanzone(oceansoul);
		scaletta.addCanzone(darkChest);
		scaletta.addCanzone(sleepingSun);
		scaletta.addCanzone(overTheHills);
		scaletta.addCanzone(crimsonTide);
		scaletta.addCanzone(comeCoverMe);
		scaletta.addCanzone(blessTheChild);
		scaletta.addCanzone(nemo);
		scaletta.addCanzone(amaranth);
		scaletta.addCanzone(wishmaster);
		scaletta.addCanzone(wishIHadAnAngel);
		scaletta.addCanzone(ghostLove);
		
		scaletta.salvaDB();
	}
}
