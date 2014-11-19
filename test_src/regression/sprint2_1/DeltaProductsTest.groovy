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
import common.CommonPrism;
import common.CommonUtil;
import common.CommonXml;


class DeltaProductsTest {

	static File newProductFile;
	static File deltaFile;
	static def single_Product_Template_Filename = "SingleProduct.xml"
	Sql sql = CommonPrism.getNewDbConnection()
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		println "\n\n===Start Test ${DeltaProductsTest.name}===\n\n"
		
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
	}


	@Test
	public void testDeltaDetection() {
/*
 * Step 1: Ingest New Product
 */
		//Generate Random ID for New Prism Product
		File prodFile = CommonPrism.getResourceFile(single_Product_Template_Filename)
		assert prodFile.exists()
		File ingestDir = new File(CommonUtil.localInpath)
		assert ingestDir.exists()
		
		String exRefId = CommonXml.randomIdAsString()
		
		//get File returned with newly generated externalReferenceID
		newProductFile = CommonXml.randomExRefIdToFile(prodFile, exRefId)
		
		//Start Ingestion!
		// :Move Test XML test File with New Product to Inbound for Ingestion
		CommonPrism.startIngestionGetBatchId(newProductFile, ingestDir)
		
		assert {new File("${ingestDir}/${newProductFile}").exists()}
		
		assert CommonPrism.isOutboundPublished(CommonUtil.localOutpath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.localOutpath)
		
		println "${outFile.name} was published!"
		
		//Delete New Product Outbound File
		outFile.delete()
		
			
/*
 * Step 2: Ingest Same Product with a Delta
 */
		//Delete existing File on Inbound
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
		
		//Make Change to Product with Same Unique Product Identifier
		//ie. Same ExternalReferenceID
		def  xml = new XmlSlurper(false, false).parse(newProductFile)
		
		
		try {
			xml.items.product.platform = "Test Paperback"
			assert xml.items.product.platform.equals("Test Paperback")
			
		} catch (Exception e) {fail("Did not set platform!")}
		
		
		deltaFile = new File("${newProductFile.parentFile}/Delta${newProductFile.name}")
		deltaFile.createNewFile()
		
		println deltaFile.absolutePath
		
		CommonUtil.writeXmlToFile(deltaFile, xml)
		
		//Start Ingestion!
		// :Move Test XML test File with New Product to Inbound for Ingestion
		println "\n\n------------------------------------------"
		println "Delta File"
		CommonPrism.startIngestionGetBatchId(deltaFile, ingestDir)
		
		assert {new File("${ingestDir}/${deltaFile}").exists()}
		
		println "File ${deltaFile.name} Ingested!"
		 
		
		
		assert CommonPrism.isOutboundPublished(CommonUtil.localOutpath).equals(true)
		outFile = CommonPrism.getOutboundFile(CommonUtil.localOutpath)
		
		println "${outFile.name} was published!"
		
/*
 * Step 3: Verify Publish File has Delta
 */
		def outXml = new XmlSlurper(false,false).parse(outFile)
		
		assert outXml.items.product.platform.equals("Test Paperback")
/*
 * Step 4: Verify Delta Exists in the Database
 */
		sql.eachRow(""" select p.PRISM_PRODUCT_ID, p.PRISM_PRODUCT, c.EXTERNAL_REFERENCE_ID
							from PRISM_Key_Crosswalk c,  PRISM_Product p
							where c.External_Reference_ID = ${exRefId} AND 
								c.PRISM_Product_ID=p.PRISM_Product_ID""") 
				{ row ->
					def jsonProductClob = row.PRISM_PRODUCT.getAsciiStream().getText()
					def prodJson = new JsonSlurper().parseText(jsonProductClob)
					assert  prodJson.product.platform.equals("Test Paperback"), "PRISM_PRODUCT does not contain Delta!!"
					
				}
		
	}
	
	@After
	public void after() {
		//Delete Test Files on Exit
		newProductFile.deleteOnExit()
		deltaFile.deleteOnExit()
		
		println "\n\n====End Test====\n\n"
	}

}
