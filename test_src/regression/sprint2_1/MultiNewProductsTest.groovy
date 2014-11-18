package regression.sprint2_1;

import static org.junit.Assert.*;

import java.io.File;
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



class MultiNewProductsTest {
	
	static String inFilename = "SingleProduct.xml"
	File resourceFile = CommonPrism.getResourceFile(inFilename)
	File inBoundFile
	
	Sql sql = CommonPrism.getNewDbConnection()
	int prodCount = 200;

	
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		println "\n\n====Start Test '${MultiNewProductsTest.name}'====\n\n"
		
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
	}


	@Test
	public void testMultiNewProducts() {
/*
 * Step 1: Construct Multi Product File 
 */
		assert resourceFile.exists()
		File destDir = new File(CommonUtil.localInpath)
		assert destDir.exists()
		
		def arrayIds = [];
		def arrayIdsCopy = [];
		def value
		for (def i =0; i < prodCount; i++) {
			value = CommonXml.randomIdAsString()
			arrayIdsCopy << value 
			arrayIds << value
		}
		
		println arrayIds.toString()

		inBoundFile = CommonXml.randomArrayIdsToFile(resourceFile, arrayIds)
		
		println arrayIdsCopy.toString()
		
		def xml = new XmlSlurper(false, false).parse(inBoundFile)
		
		def products = xml.depthFirst().findAll { it.name() == 'product' };
		
		
		//assert that number of product nodes equals the size of prodCount
		assert prodCount == products.size()
		
		//assert that products include the expected ids
		
		arrayIdsCopy.each { i ->
			assert ( 1 == products.findAll{ it.externalReferenceID == i }.size() ), "Cannot Find Product with ExternalReferenceID: ${i}"
		}
		
		
/*
 * Step 2: Ingest Multi-Product File
 */
		def batchId = CommonPrism.startIngestionGetBatchId(inBoundFile, new File(CommonUtil.localInpath))
		println "batchid: ${batchId}"
		
/*
 * Step 2: Check that File was Published
 */
		assert CommonPrism.isOutboundPublished(CommonUtil.localOutpath).equals(true), "Outbound File Not Published!"
		File outFile = CommonPrism.getOutboundFile(CommonUtil.localOutpath)
				
		println "${outFile.name} was published!"
		
		//Verify in database that New Product data was added for persistence
		
		arrayIdsCopy.each { i ->
			sql.eachRow(""" select d.PRISM_PRODUCT_ID, d.COMPANY_ID, c.EXTERNAL_REFERENCE_ID, d.SOURCE_DOCUMENT
								from PRISM_Key_Crosswalk c,  PRISM_Source_Data d
								where c.External_Reference_ID = ${i} AND 
									c.PRISM_Product_ID=d.PRISM_Product_ID""") 
					{ id ->
						assert id.External_Reference_ID.equals(i), "Product with External Reference ID '${i}' was not added to the Database!"
						println "Product with External Reference ID '${i}' was added to the Database!"
					}
		}
	}
	
	@After
	public void after() {
		
		inBoundFile.deleteOnExit()
		println "\n\n====Ending Test===\n\n"
	}

}
