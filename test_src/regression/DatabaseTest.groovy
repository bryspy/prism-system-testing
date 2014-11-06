package regression;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import groovy.sql.Sql

class DatabaseTest {

	Sql sqlLocal = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = PrismDB)))"
			, "PrismUser","!@PrismUser@!")

	@Test
	public void testDatabaseConnection() {
		
		def source = sqlLocal.dataSet("PRISM_SOURCE")
		def cl_id = "1234567566"
		def cl_na = "Client"
		def ra_or = "123"
		def co_id = "987"
		def co_na = "Harper"
		
		source.add(client_id: cl_id, client_name: cl_na, rank_ordering: ra_or, company_id: co_id, company_name: co_na)
		
		
		sqlLocal.eachRow("select * from PRISM_SOURCE") { row ->
			println "Hello ${row.CLIENT_NAME} with id: ${row.client_id}"
		}
		
		sqlLocal.execute("delete from prism_source where client_id = ${cl_id}")
	}

}
