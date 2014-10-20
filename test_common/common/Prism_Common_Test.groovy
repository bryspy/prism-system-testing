package common;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javassist.bytecode.stackmap.BasicBlock.Catch;
import junit.framework.AssertionFailedError

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.util.XmlSlurper
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import groovy.io.FileType

/**
 * 
 * @author bikonomovski
 *
 */
class Prism_Common_Test {

	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
	def static final tomOutboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/outbound"
	
	
	
	/**
	 * 
	 * @param domain
	 */
	public static void initiateServices(def domain) {
		//Initiate Services before Ingestion
		def services = ["monitor", "ingestion", "transformation", "publish", "dataprovider"];
		
		try {
			services.each() {i -> new HTTPBuilder( "${domain}" ).get(path : "/${i}/status" )
				{resp ->
					assert "${resp.status}" == "200"
					println "${i}: ${resp.statusLine}"}
			}
			println "---------------------------------\n\n"
		}
		catch ( HttpResponseException ex ) {
			// default failure handler throws an exception:
			println "Unexpected response error: ${ex.statusCode}"
		}
	}
	
	/**
	 * 
	 */
	public static void deleteInbound() {
		if ( SystemUtils.IS_OS_WINDOWS )
		{
			//delete inbound file
			new File("${tomInboundPath}").eachFile { f -> f.delete() }
		}
		else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
	}
	/**
	 * 
	 * @param path
	 */
	public static void deleteInbound(def path) {
		if ( SystemUtils.IS_OS_WINDOWS )
		{
			//delete inbound file
			new File("${path}").eachFile { f -> f.delete() }
		}
		else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
	}
	
	/**
	 * 
	 */
	public static void deleteOutbound() {
		if (SystemUtils.IS_OS_WINDOWS) {
			new File("${tomOutboundPath}").eachFile { f -> f.delete() }
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
	}

	/**
	 * 
	 * @param path
	 */
	public static void deleteOutbound(def path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			new File("${path}").eachFile { f -> f.delete() }
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
	}
	/**
	 * 
	 */
	public static void copyToCloset() {
		if (SystemUtils.IS_OS_WINDOWS) {
			//Copy from /common/.../resources to Tomca/prism/closet
			FileUtils.copyDirectory(new File("C:/dev/prism/common/build/resources/test/"),
				new File("${tomClosetPath}"))
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
	}
	/**
	 * 
	 * @param path
	 */
	public static void copyToCloset(def path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			//Copy from /common/.../resources to Tomca/prism/closet
			FileUtils.copyDirectory(new File("C:/dev/prism/common/build/resources/test/"),
				new File("${path}"))
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
	}
	
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static File getResourceFile(filename) {
		//Modify ProductID, to be unique for "New" product designation
		File file = new File("test_src/resources/${filename}")
		//println file.getAbsolutePath()
		assert file.exists()
		
		return file;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String randomExRefIdToFile(File file) {
		
		def xml = new XmlSlurper().parse(file)
		
		Random rand = new Random()
		
		def prod_id_list = []
		def prod_id = rand.nextInt(100000000+1).toString()
		def product = xml.items.product.find{
			(it.companyID == 'testcoid' && it.catalogID == '12345678' && it.productName == 'Test Product') }
		
		//Is Product Node within Items?
		try {
			assert !product.toString().equals(null);
		}
		catch (AssertionFailedError e) {
			
			try {
				product = xml.product.find{
				(it.companyID == 'testcoid' && it.catalogID == '12345678' && it.productName == 'Test Product') }
				
				assert !product.equals(null)
			} catch (AssertionFailedError a) {
				throw Exception("Cannot Find Product Node! Check XML Structure.")
			}
		}
		
		
		
		product.externalReferenceID = prod_id
		
		
		writeXmlToFile(file, xml)
		
		return "${product.externalReferenceID}";
	}
	
	
	/**
	 * 
	 * @param file
	 * @param xml
	 */
	public static void writeXmlToFile(File file, def xml) {
		FileWriter writer = new FileWriter(file);
		BufferedWriter buff = new BufferedWriter(writer);
		//buff.write(xml.text());
		buff.write(XmlUtil.serialize(xml))
		buff.close();
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isFileIngested(fileName, domain) {
		def isIngest = false
		while (!isIngest) {
			try {
				def monitor = new HTTPBuilder("${domain}") .get( path : '/monitor/cache') 
				{resp, json ->
					assert resp.status == 200
					assert json."1".fileStatusList[0].filename.equals(fileName).equals(fileName)
				}
				//TODO Check on .../fileStatusList.status to verify completion of file ingestion
				isIngest = true
			} catch (NullPointerException e) {
				//Pause for 4 seconds
				Thread.sleep(4000);
				//println isIngest
				
			}
		}
		return isIngest;
	}
	
	
	/**
	 * 
	 * @param outboundLocation
	 * @param filename
	 * @return
	 */
	public static File isOutboundPublished(outboundLocation, filename) {
		def exists = false
		//File outXmlFile = new File("${outboundLocation}/${filename}")
		def i=0
		
		print "Waiting on Outbound File"
		//Check and wait for outbound File to drop
		def outDir = new File("${outboundLocation}")
		while (outDir.list().length < 1) {
			if (i > 60) {
				throw new FileNotFoundException("Outbound File Not Published")
			}
			print "."
			Thread.sleep(1000);
				
			i++
		}
		assert outDir.list().length < 1
		println "\nOutbound File Dropped."
		
		def list = []
		outDir.eachFileRecurse (FileType.FILES) { file ->
				list << file
		}
		File outXmlFile = new File(list[0].absolutePath)
		return outXmlFile;
	}
	
}
