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
import common.util.CommonXml;

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
		
/*
 * Step 1: Ingest xml with new Single Product
 */
		//Generate Random ID for New Prism Product
		//File prodFile = Prism_Common_Test.getResourceFile(inFilename)
		File prodFile = CommonPrism.getResourceFile(inFilename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.tomInboundPath)
		assert destDir.exists()
		
		//get File returned with newly generated externalReferenceID
		inFile = CommonXml.randomExRefIdToFile(prodFile)
		
		//Start Ingestion!  
		// :Move Test XML test File with New Product to Inbound for Ingestion
		FileUtils.copyFileToDirectory(inFile, destDir)
		
		assert {new File("${destDir}/${inFile}").exists()}
		
		
		//Verify File ingested and published
		assert CommonPrism.isFileIngested(inFile.name, domain).equals(true)
		println "File ${inFile.name} Ingested!"
		 
		
/*
 * Step 2: Check that File was Published
 */
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
		
			
		
		//TODO Verify in database that New Product data was added for persistence
//		sql.eachRow("""select * from prism_Source_Data s, prism_product prod, prism_publish pub
//			where s.prism_product_id = ${prod_id}""") { prod ->
//				assert prod.prism_product_id.equals(prod_id)
//			}
		
		
/*
 * Step 3: Verify that Outbound File includes new products for Publish to GC		
 */
		//TODO Verify that Outbound File includes New Products for Publish to GC
		def outXml = new XmlSlurper(false, false).parse(outFile)
		println "outXml = ${outXml.product.find{it.externalReferenceID == '31344775'} }"
		fail("Not Yet Implemented")
	}
	
	
	@After
	void after() {
		//TODO Clean up New Products added to Database
		
		/*
		 * def sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
		 * 	, "DRHADMIN", "summer123")
		 */
		
		//sql.execute("delete from prism_source where client_id = ${cl_id}")
		
		CommonUtil.deleteInbound()
		//Delete testIngestFile(s)
		inFile.deleteOnExit()
		
	}
}
