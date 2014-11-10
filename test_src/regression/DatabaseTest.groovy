package regression;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import groovy.sql.Sql

import common.prism.CommonPrism

class DatabaseTest {

	Sql sql = CommonPrism.getNewDbConnection()

	@Test
	public void testDatabaseConnection() {
		
		println "\n\n====Start Test ${DatabaseTest.name}====\n\n"
		
		
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
		
		println "\n\n====End Test====\n\n"
	}

}
