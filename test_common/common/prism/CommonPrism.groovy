package common.prism;
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
import groovy.sql.Sql

/**
 * 
 * @author bikonomovski
 *
 */
class CommonPrism {

	def static final monitorPort = "http://localhost:9001"
	def static final ingestionPort = "http://localhost:9003"
	def static final dataproviderPort = "http://localhost:9000"
	def static final publishPort = "http://localhost:9005"
	def static final transformationPort = "http://localhost:9004"
	def static final rulesproviderPort = "http://localhost:9002"
	
	
	
	/**
	 * Deprecated.
	 * Domain of "http://localhost:8080" is no longer the default, as each separate service runs on a different port.
	 * @param domain
	 */
	@Deprecated
	public static void initiateServices(def domain) {
		//Initiate Services before Ingestion
		def services = ["monitor":9001, "ingestion": 9003, "transformation": 9004, "publish":8080, "dataprovider": 9000];
		
		try {
			services.each() {i -> new HTTPBuilder( "http://localhost:${i.value}" ).get(path : "/${i.key}/status" )
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
		catch (AssertionFailedError e) {
			fail("Intiate Services  Failed!")
		}
	}
	
	
	/**
	 * Starts each service to initiate them prior to running tests.
	 * Possibly @Deprecated since Spring 3.1 with autostart and Spring-ification
	 */
	@Deprecated
	public static void initiateServices() {
		//Initiate Services before Ingestion
		def services = ["monitor":9001, "ingestion": 9003, "transformation": 9004, "publish":8080, "dataprovider": 9000];
		
		try {
			services.each() {i -> new HTTPBuilder( "http://localhost:${i.value}" ).get(path : "/${i.key}/status" )
				{resp ->
					assert "${resp.status}" == "200"
					println "${i}: ${resp.statusLine}"
				}
			}
			println "---------------------------------\n\n"
		}
		catch ( HttpResponseException ex ) {
			// default failure handler throws an exception:
			println "Unexpected response error: ${ex.statusCode}"
		}
		catch (AssertionFailedError e) {
			fail("Intiate Services  Failed!")
		}
	}
	
	
	
	/**
	 * Get Resource File by Name
	 * @param filename
	 * @return File object with Name filename
	 */
	public static File getResourceFile(String filename) {
		//Modify ProductID, to be unique for "New" product designation
		File file = new File("test_src/resources/${filename}")
		//println file.getAbsolutePath()
		assert file.exists()
		
		return file;
	}
	

	/**
	 * Get Current Bath Id from List at [0].
	 * Makes assumption that there is only 1 batch at a time
	 * @return String batch Id
	 */
	public static String getCurrentBatchId() {
		String id=null;
		def activeBatchList = new HTTPBuilder(monitorPort).get( path : '/monitor/activebatchlist')
		{resp, json ->
			id = json[0].toString()
		}
		return id
	}
	
		

	/**
	 * Start Ingestion by moving a File inFile to inboundDir
	 * @param inFile
	 * @param inboundDir
	 * @return String batch Id
	 */
	public static String startIngestionGetBatchId(File inFile, File inboundDir) {
		String id;
		boolean batchId = false;
		def i=0;
		
		FileUtils.copyFileToDirectory(inFile, inboundDir)
		assert {new File("${inboundDir}/${inFile}").exists()}
		
		println "Successfully copied ${inFile.name} to ${inboundDir.absolutePath}"
		
		println "Attempting to get Batch ID from monitor/activebatchlist"
		while (!batchId) {
			if (i > 500) {
				throw new Exception("Did not get Batch ID!!!")
			}
			try {
				
				//id = getCurrentBatchId("domain")
				id = getCurrentBatchId()
				
				
				if (id.equals(null) || id.equals("") || id.equals("null")) {
					i++;
					if (i%5 == 0)
					{
						print '.'
					}
					Thread.sleep(10);
					continue;
				}
				
				assert id.getBytes().size() > 0
				batchId = true;
				
			}catch (NullPointerException e) {
				
				Thread.sleep(100);
			}
			i++;
		}
		println "${id}"
		return id;
	}

	
	
	/**
	 * 
	 * @param outboundLocation
	 * @param filename
	 * @return
	 */
	public static boolean isOutboundPublished(String outboundLocation) {
		def outDir = new File("${outboundLocation}")
		def exists = false
		//File outXmlFile = new File("${outboundLocation}/${filename}")
		def i=0
		
		print "Waiting on Outbound File"
		//Check and wait for outbound File to drop

		while (outDir.list().length < 1) {
			if (i > 60) {
				throw new FileNotFoundException("Outbound File Not Published after 60+ Seconds")
			}
			print "."
			Thread.sleep(1000);
				
			i++
		}
		assert outDir.list().length > 0
		println "\nOutbound File Dropped."
		
		def list = []
		outDir.eachFileRecurse (FileType.FILES) { file ->
				list << file
		}
		File outXmlFile = new File(list[0].absolutePath)
		return outXmlFile;
	}
	
	
	/**
	 * 
	 * @param batchId
	 * @param domain
	 * @return isFinished
	 */
	@Deprecated
	public static boolean isBatchFinished(String batchId, String domain) {
		boolean isFinished = false;
		//def monitor = new HTTPBuilder("${domain}").get( path : '/monitor/isbatchfinished/${batchId}')
		HTTPBuilder monitor = new HTTPBuilder(domain)
		def i=0;
		
		println "/monitor/isbatchfinished/${batchId} "
		
		
		while (!isFinished){
			if (i > 100) {
				throw new Exception("BatchID: ${batchId} is not finished after 100+ seconds")
			}
			monitor = new HTTPBuilder(domain)
			monitor.get(path : "/monitor/isbatchfinished/${batchId}") {resp, json ->
				print '.'
				if (json == false) {
					i++	
					Thread.sleep(1000)
				}else {
					isFinished = true
				}
			}
		}
		
		return isFinished;
	}
	
	
	public static boolean isBatchFinished(String batchId) {
		boolean isFinished = false;
		//def monitor = new HTTPBuilder("${domain}").get( path : '/monitor/isbatchfinished/${batchId}')
		HTTPBuilder monitor = new HTTPBuilder("http://localhost:9001")
		def i=0;
		
		println "/monitor/isbatchfinished/${batchId} "
		
		
		while (!isFinished){
			if (i > 100) {
				throw new Exception("BatchID: ${batchId} is not finished after 100+ seconds")
			}
			monitor = new HTTPBuilder("http://localhost:9001")
			monitor.get(path : "/monitor/isbatchfinished/${batchId}") {resp, json ->
				print '.'
				if (json == false) {
					i++
					Thread.sleep(1000)
				}else {
					isFinished = true
				}
			}
		}
		
		return isFinished;
	}
	
	/**
	 * 
	 * @param outboundLocation
	 * @return
	 */
	public static File getOutboundFile(String outboundLocation) {
		def outDir = new File("${outboundLocation}")
		def list = []
		outDir.eachFileRecurse (FileType.FILES) { file ->
				list << file
		}
		File outXmlFile = new File(list[0].absolutePath)
		
		return outXmlFile
	}
	
	/**
	 * Returns a Sql Connection. Connects to Local PrismDB if parameter = true
	 * Otherwise Connects to AWS PRISM01
	 * @param local
	 * @return Sql Connection
	 */
	public static Sql getNewDbConnection(boolean local) {
		Sql sql;
		
		if (local) {
			sql = Sql.newInstance("""jdbc:oracle:thin:@(DESCRIPTION =
				(ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))
				(CONNECT_DATA =
				(SERVER = DEDICATED)
				(SERVICE_NAME = PrismDB)))""", "PRISMUSER","!@PrismUser@!")
		}else {
			sql = Sql.newInstance("""jdbc:oracle:thin:@(DESCRIPTION=
				(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))
				(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"""
				, "DRHADMIN", "summer123")
		}
		
		return sql
	}
	
	/**
	 * Get Local DB Connection PrismDB
	 * @return SQL connection
	 */
	public static Sql getNewDbConnection() {
		return getNewDbConnection(true)
	}
	
	
	/**
	 * @deprecated
	 * @param file
	 * @return
	 */
	@Deprecated
	public static boolean isFileIngested(String fileName, String batchId, String domain) {
		def isIngest = false
		
		while (!isIngest) {
			try {
				def monitor = new HTTPBuilder("${domain}").get( path : '/monitor/cache')
				{resp, json ->
					assert resp.status == 200
					
					assert json."1".fileStatusList[0].filename.equals(fileName)
				}
				isIngest = true;
			} catch (NullPointerException e) {
				//Pause for 4 seconds
				Thread.sleep(4000);
				//println isIngest
				
			}
		}
		return isIngest;
	}
	
}
