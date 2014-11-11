package in_development;

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
	File inFile;
	
	Sql sql = CommonPrism.getNewDbConnection()
	int prodCount = 2;

	
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		println "\n\n====Start Test '${MultiNewProductsTest.name}'====\n\n"
		
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
	}


	@Test
	public void test() {
/*
 * Step 1: Construct Multi Product File 
 */
		File prodFile = CommonPrism.getResourceFile(inFilename)
		assert prodFile.exists()
		File destDir = new File(CommonUtil.winInpath)
		assert destDir.exists()
		
		def arrayIds = [];
		
		for (def i =0; i < prodCount; i++) {
			arrayIds << CommonXml.randomIdAsString()
		}
		
		println arrayIds.toString()
		
		CommonXml.randomArrayIdsToFile(inFile, arrayIds)
		
		
		
		fail("Not yet implemented");
	}
	
	@After
	public void after() {
		
		//inFile.deleteOnExit()
		println "\n\n====Ending Test===\n\n"
	}

}
