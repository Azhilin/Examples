package gms.helper;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.genesyslab.functional.tests.gms.helper.PropertiesInitialization.getPropertyConfiguration;


public final class DBUtils {
	 private static final String configHost = getPropertyConfiguration("config.server.host");
	 private static final int  dbPort =1433;
	 private static final String dbUsername ="sa";
	 private static final String dbPassword ="sqlgenesys";
	 private static final String databaseName ="genesys_ucs";
	
	 private static Logger logger = Logger.getLogger(DBUtils.class);

	    public static void truncateTable(String host, int port, String login, String password, String databaseName,
	            String tableName) {
	        try {
	            JtdsDataSource ds = new JtdsDataSource();
	            ds.setServerName(host);
	            ds.setPortNumber(port);
	            ds.setDatabaseName(databaseName);

	            Connection cnx = ds.getConnection(login, password);
	            Statement stmt = cnx.createStatement();

	            stmt.execute("Truncate Table " + tableName);

	        } catch (Exception e) {
	            logger.warn("While removing Multimedia interaction from InxSvrDB, " + e);
	        }
	    }

	    public static ResultSet executeQuery(String host, int port, String login, String password, String databaseName,
	            String query) {
	        JtdsDataSource ds = new JtdsDataSource();
	        ds.setServerName(host);
	        ds.setPortNumber(port);
	        ds.setDatabaseName(databaseName);

	        Connection cnx;
	        try {
	            cnx = ds.getConnection(login, password);
	            Statement stmt = cnx.createStatement();
	            return stmt.executeQuery(query);
	        } catch (SQLException e) {
	            logger.warn("Exception while executing query " + e);
	        }
	        return null;
	    }

//	    public static void main(String str[]) throws SQLException {
//	        ResultSet resultset = DBUtils.executeQuery("135.225.58.54", 1433, "sa", "sqlgenesys", "genesys_ucs",
//	                "SELECT * FROM ServiceStarted");
//
//	        while (resultset.next()) {
//	            System.out.println(resultset.getString("ServiceId"));
//	        }
//	    }		
		
	
		public static void deleteDataInUCS() {
			
			List<String> queries = new ArrayList<>();
			queries.add("DELETE from Contact");
			queries.add("DELETE from ServiceStarted");
			queries.add("DELETE from ServiceStartedAnonymous");
			queries.add("DELETE from ServiceCompleted");
			queries.add("DELETE from StateStarted");
			queries.add("DELETE from StateCompleted");
			queries.add("DELETE from TaskStarted");
			queries.add("DELETE from TaskCompleted");
			
			for(String query: queries){
				ResultSet resultset = DBUtils.executeQuery(configHost, dbPort, dbUsername, dbPassword, databaseName,
		                query);
			}
			// -dbhost 192.168.3.118 -dbport 1521 -dbname rh54x321 -dbuser grs01 -dbpassword grs01 
//			for(String query: queries){
//				ResultSet resultset = DBUtils.executeQuery("192.168.3.118", 1521, "grs01", "grs01", "rh54x321",
//		                query);
//			}
			

		}
}
