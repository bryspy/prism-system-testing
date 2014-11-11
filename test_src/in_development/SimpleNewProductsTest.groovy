package in_development;
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

class SimpleNewProductsTest {

		
	static String inFilename = "SingleProduct.xml"
	File inFile;
	
	Sql sql = CommonPrism.getNewDbConnection()
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		println "\n\n====Start Test 'NewProductsTest'====\n\n"
		
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
	}
	

	@Test
	public void testNewProductPersistence() {
		
/*
 * Step 1: Ingest xml with new Single Product
 */
		//Generate Random ID for New Prism Product
		File prodFile = CommonPrism.getResourceFile(inFilename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.winInpath)
		assert destDir.exists()
		
		
		//Generate Random Id
		String exRefId = CommonXml.randomIdAsString()
		
		//get File returned with newly generated externalReferenceID
		inFile = CommonXml.randomExRefIdToFile(prodFile, exRefId)
		
		//Start Ingestion!  
		// :Move Test XML test File with New Product to Inbound for Ingestion
		
		String batchId = CommonPrism.startIngestionGetBatchId(inFile, destDir)

		println "File ${inFile.name} Ingested!"
		 
		assert CommonPrism.isBatchFinished(batchId).equals(true), "Batch Did Not Finish processing File!"
/*
 * Step 2: Check that File was Published
 */
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true), "Outbound File Not Published!"
		File outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
		
			
		
		//Verify in database that New Product data was added for persistence
		sql.eachRow(""" select d.PRISM_PRODUCT_ID, d.COMPANY_ID, c.EXTERNAL_REFERENCE_ID, d.SOURCE_DOCUMENT
							from PRISM_Key_Crosswalk c,  PRISM_Source_Data d
							where c.External_Reference_ID = ${exRefId} AND 
								c.PRISM_Product_ID=d.PRISM_Product_ID""") 
				{ id ->
					assert id.External_Reference_ID.equals(exRefId), "Product with External Reference ID '${exRefId}' was not added to the Database!"
					println "Product with External Reference ID '${exRefId}' was added to the Database!"
				}	
		
		
/*
 * Step 3: Verify that Outbound File includes new products for Publish to GC		
 */
		//Verify that Outbound File includes New Products for Publish to GC
		def outXml = new XmlSlurper(false, false).parse(outFile)
		
		assert outXml.'**'.find {
				it.name().startsWith('externalReference') }.each
					{ node -> node.text() == '${exRefId}' }  == exRefId
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
		
		println "\n\n====Ending Test===\n\n"
		
	}
}
