package in_progress;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.BeforeClass;
import org.junit.After
import org.junit.Test;

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.sql.Sql
import common.Prism_Common_Test;

class NewProductsTest {

	static String domain = "http://localhost:8080";
	Sql sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
		, "DRHADMIN", "summer123")
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Remove Inbound and Outbound Files
		Prism_Common_Test.deleteInbound()
		Prism_Common_Test.deleteOutbound()
		
		//Copy to Closet
		Prism_Common_Test.copyToCloset()
		
		//Initiate Services
		Prism_Common_Test.initiateServices(domain)
	}

	@Test
	public void testNewProductPersistence() {
		//TODO Move Test XML File1 with New Product to Inbound for Ingestion
		URL single = this.getClass().getResource("/resources/Single_Product.xml");
		
		File singProdXML = new File(single.getFile())
		assert singProdXML.exists()
		//def prod_id = "123456"
		//def xmlSlurp(file(client_id))
		
		//TODO Verify in database that New Product data was added for persistence
//		sql.eachRow("""select * from prism_Source_Data s, prism_product prod, prism_publish pub
//			where s.prism_product_id = ${prod_id}""") { prod ->
//				assert prod.prism_product_id.equals(prod_id)
//			}
		
		
		
		//TODO Verify that Outbound File includes New Products for Publish to GC
		
		fail("Not Yet Implemented")
	}
	
	
	@After
	void after() {
		//TODO Clean up New Products added to Database
		
		/*def sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
				, "DRHADMIN", "summer123")
		*/
		
		//sql.execute("delete from prism_source where client_id = ${cl_id}")
	}
}