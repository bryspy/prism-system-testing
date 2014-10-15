package regression;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import groovy.sql.Sql

class DatabaseTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDatabaseConnection() {
		def sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
				, "DRHADMIN", "summer123")
		
		def source = sql.dataSet("PRISM_SOURCE")
		def cl_id = "1234567566"
		def cl_na = "Client"
		def ra_or = "123"
		def co_id = "987"
		def co_na = "Harper"
		
		source.add(client_id: cl_id, client_name: cl_na, rank_ordering: ra_or, company_id: co_id, company_name: co_na)
		
		
		sql.eachRow("select * from PRISM_SOURCE") { row ->
			println "Hello ${row.CLIENT_NAME} with id: ${row.client_id}"
		}
		
		sql.execute("delete from prism_source where client_id = ${cl_id}")
	}

}
