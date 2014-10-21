package in_progress;
import static org.junit.Assert.*;

import java.net.URL;

import org.junit.BeforeClass;
import org.junit.After
import org.junit.Test;
import java.util.Random;
import org.apache.commons.io.FileUtils

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.sql.Sql
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import common.prism.CommonPrism;
import common.util.CommonUtil;

class NewProductsTest {

	static String domain = "http://localhost:8080";
	Sql sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
		, "DRHADMIN", "summer123")
	
	static String inFilename = "SingleProduct.xml"
//	static String inFilename = "BulkImport_harperCollins-107153132280.xml"
	File inFile;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
		//Initiate Services
		CommonPrism.initiateServices(domain)
	}
	

	@Test
	public void testNewProductPersistence() {
		
		//Generate Random ID for New Prism Product
		//File prodFile = Prism_Common_Test.getResourceFile(inFilename)
		File prodFile = CommonPrism.getResourceFile(inFilename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.tomInboundPath)
		assert destDir.exists()
		
		//get File returned with newly generated externalReferenceID
		inFile = CommonUtil.randomExRefIdToFile(prodFile)
		
		//Start Ingestion!  
		// :Move Test XML test File with New Product to Inbound for Ingestion
		FileUtils.copyFileToDirectory(inFile, destDir)
		
		assert {new File("${destDir}/${inFile}").exists()}
		
		
		//Verify File ingested and published
		assert CommonPrism.isFileIngested(inFile.name, domain).equals(true)
		println "File ${inFile.name} Ingested!"
		 
		
		
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
		
			
		
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
		
		CommonUtil.deleteInbound()
		inFile.deleteOnExit()
		
	}
}
