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
	 * @param filename
	 * @return
	 */
	public static File getResourceFile(String filename) {
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
	public static boolean isFileIngested(String fileName, String domain) {
		def isIngest = false
		def monitorSstatus = new HTTPBuilder("${domain}") .get( path : '/monitor/status') 
				{resp, json -> }
		
		while (!isIngest) {
			try {
				def monitor = new HTTPBuilder("${domain}") .get( path : '/monitor/cache') 
				{resp, json ->
					assert resp.status == 200
					
					assert json."1".fileStatusList[0].filename.equals(fileName)
				}
				//TODO Check on .../fileStatusList.status to verify completion of file ingestion
				isIngest = true;
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
	
	public static Sql getNewDbConnection() {
		Sql sql = Sql.newInstance("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS =(PROTOCOL = TCP)(HOST = 10.16.5.203)(PORT = 1521)))(CONNECT_DATA =(SID = devdb)(SERVER = DEDICATED)))"
			, "DRHADMIN", "summer123")
		
		return sql
	}
	
}
