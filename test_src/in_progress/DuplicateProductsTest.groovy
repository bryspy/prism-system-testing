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

class DuplicateProductsTest {

	static String domain = "http://localhost:8080";
	static String inFilename = "SingleProduct.xml"
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
	public void testDeDuplication() {
/*
 * Step 1: Ingest New Product
 */
		
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
		 
		
/*
 * Step 2: Verify Publish
 */
		//
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		File outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
		CommonUtil.deleteInbound()
		
/*
 * Step 3: TODO Verify DB Persistence of new Product
 */
		
		
/*
 * Step 4: TODO Copy New Product File to Inbound for Ingestion
 */
		
		//Start Ingestion!
		// :Move Test XML test File with New Product to Inbound for Ingestion
		FileUtils.copyFileToDirectory(inFile, destDir)
		
		assert {new File("${destDir}/${inFile}").exists()}
		
		
		//Verify File ingested and published
		assert CommonPrism.isFileIngested(inFile.name, domain).equals(true)
		println "File ${inFile.name} Ingested!"
		 
		
/*
 * Step 2: Verify Publish
 */
		//
		assert CommonPrism.isOutboundPublished(CommonUtil.tomOutboundPath).equals(true)
		outFile = CommonPrism.getOutboundFile(CommonUtil.tomOutboundPath)
		
		println "${outFile.name} was published!"
		
/*
 * Step 5: TODO Verify No product is Published for GC
 */
		def xml = new XmlSlurper().parse(outFile)
		
		println "product= ${xml.product}"
		assert xml.product.toString().equals(""), "Duplicate Product should not be returned in Published GC File!"
		

		
/*
 * Step 6: TODO Verify Product is not persisted twice in DB
 */
		
		
		fail("Not yet implemented");
	}
	
	@After
	public void after() {
		//Delete Ingested File on Exit
		inFile.deleteOnExit()
	}

}
