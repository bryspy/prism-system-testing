package regression.sprint2_1;

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

class DuplicateProductsTest {

	static String domain = "http://localhost:8080";
	static String inFilename = "SingleProduct.xml"
	File inFile;
	
	Sql sql = CommonPrism.getNewDbConnection()
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		println "\n\n====Start Test '${DuplicateProductsTest.name}'====\n\n"
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
	}


	@Test
	public void testDeDuplication() {
/*
 * Step 1: Ingest New Product
 */
		
		//Generate Random ID for New Prism Product
		//File prodFile = Prism_Common_Test.getResourceFile(inFilename)
		File prodFile = CommonPrism.getResourceFile(inFilename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.localInpath)
		assert destDir.exists()
		
		
		//Generate Random Id
		String exRefId = CommonXml.randomIdAsString()
		
		//get File returned with newly generated externalReferenceID
		inFile = CommonXml.randomExRefIdToFile(prodFile, exRefId)
		
		
		//Start Ingestion!
		// :Move Test XML test File with New Product to Inbound for Ingestion
		FileUtils.copyFileToDirectory(inFile, destDir)
		
		assert {new File("${destDir}/${inFile}").exists()}
		
		
		//Verify File ingested and published
		println "File ${inFile.name} Ingested!"
		 
		
/*
 * Step 2: Verify Publish
 */
		//
		assert CommonPrism.isOutboundPublished(CommonUtil.localOutpath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.localOutpath)
		
		println "${outFile.name} was published!"
		
		CommonUtil.deleteInbound()
		
/*
 * Step 3: Verify DB Persistence of new Product
 */
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
 * Step 4: Copy New Product File to Inbound for Ingestion
 */
		
		//Start Ingestion!
		CommonPrism.startIngestionGetBatchId(inFile, destDir)
		
		assert {new File("${destDir}/${inFile}").exists()}
		 
		
/*
 * Step 2: Verify Publish
 */
		//
		assert CommonPrism.isOutboundPublished(CommonUtil.localOutpath).equals(true)
		outFile = CommonPrism.getOutboundFile(CommonUtil.localOutpath)
		
		println "${outFile.name} was published!"
		
/*
 * Step 5: Verify No product is Published for GC
 */
		def xml = new XmlSlurper().parse(outFile)
		
		assert xml.product.toString().equals(""), "Duplicate Product should not be returned in Published GC File!"
		

		
/*
 * Step 6: Verify Product is not persisted twice in DB
 */
		sql.eachRow(""" select External_Reference_ID, Count(*) as Count
						from PRISM_KEY_CROSSWALK 
						Group By External_Reference_ID
						Having Count(*) > 1""") 
					{ count ->
						assert count.Count == null
					}
		
	}
	
	@After
	public void after() {
		
		
		//Delete Ingested File on Exit
		inFile.deleteOnExit()
		
		
		println "\n\n====End Test====\n\n"
	}

}
