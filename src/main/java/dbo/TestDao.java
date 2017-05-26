package dbo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import utils.DBUtils;

public class TestDao {

	public void test(){
		Connection conn = DBUtils.getConnection();
		
				
		try{
	      String query = "SELECT count(*) c FROM weather ";
 
	      Statement st = conn.createStatement();
	      ResultSet rs = st.executeQuery(query);
	      
	      while (rs.next())
	      {
	        System.out.println("num rows: " + rs.getInt("c"));
	          
	      }
	      st.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	      System.err.println(e.getMessage());
	    }
 	} 

	
	/**
	 * stub for testing db connection, direct queries
	 */
	public static void main(String args[]){
		TestDao loginDao = new TestDao();
		loginDao.test();
		
	}
}
