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


class DeltaProductsTest {

	static File newProductFile;
	static File deltaFile;
	static def single_Product_Template_Filename = "SingleProduct.xml"
	static String domain = "http://localhost:8080";
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
		//Initiate Services
		CommonPrism.initiateServices(domain)
	}


	@Test
	public void testDeltaDetection() {
/*
 * Step 1: TODO  Ingest New Product
 */
		//Generate Random ID for New Prism Product
		//File prodFile = Prism_Common_Test.getResourceFile(inFilename)
		File prodFile = CommonPrism.getResourceFile(single_Product_Template_Filename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.tomInboundPath)
		assert destDir.exists()
		
		//get File returned with newly generated externalReferenceID
		newProductFile = CommonXml.randomExRefIdToFile(prodFile)
		
		//Start Ingestion!
		// :Move Test XML test File with New Product to Inbound for Ingestion
		FileUtils.copyFileToDirectory(newProductFile, destDir)
		
		assert {new File("${destDir}/${newProductFile}").exists()}
		
		
		//Verify File ingested and published
		assert CommonPrism.isFileIngested(newProductFile.name, domain).equals(true)
		println "File ${newProductFile.name} Ingested!"
		 
		
		
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
		//Delete New Product Outbound File
		outFile.delete()
		
			
/*
 * Step 2: TODO Ingest Same Product with a Delta
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
		FileUtils.copyFileToDirectory(deltaFile, destDir)
		
		assert {new File("${destDir}/${deltaFile}").exists()}
		
		
		//Verify File ingested and published
		assert CommonPrism.isFileIngested(deltaFile.name, domain).equals(true)
		println "File ${deltaFile.name} Ingested!"
		 
		
		
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
/*
 * Step 3: TODO Verify Publish File has Delta
 */
		
		
/*
 * Step 4: TODO Verify Delta Exists in the Database
 */
		fail("Not yet implemented");
	}
	
	@After
	public void after() {
		//Delete Test Files on Exit
//		newProductFile.deleteOnExit()
//		deltaFile.deleteOnExit()
	}

}
