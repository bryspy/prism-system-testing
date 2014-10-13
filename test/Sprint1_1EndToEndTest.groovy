import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*


class Sprint1_1EndToEndTest {

	String locHost = "http://localhost:8080";
	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if ( SystemUtils.IS_OS_WINDOWS )
		{
			new File("${tomInboundPath}").eachFile { f -> f.delete() }
			//Copy from /common/.../resources to Tomca/prism/closet
			FileUtils.copyDirectory(new File("C:/dev/prism/common/build/resources/test/"),
				new File("${tomClosetPath}"))
		}	
		else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
	}

	@Before
	public void setUp() throws Exception {
		
		//Initiate Services before Ingestion
		def services = ["monitor", "ingestion", "transformation", "publish", "dataprovider"];
		
		try {
			services.each() {i -> new HTTPBuilder( locHost ).get(path : "/${i}/status" ) 
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
		
	@Test
	public void testIngestion() {
		//fail("Not yet implemented");
		
		def bpuFileName = "BPU_Digital_River_48513_20140908_210257.xml"
		
		def bpuXML_ClosetPath = "${tomClosetPath}/${bpuFileName}"
		def bpuXML_InboundPath = "${tomInboundPath}/${bpuFileName}"
		
		//Step1: Copy Closet XML to /inbound
		File xmlFile = new File(bpuXML_ClosetPath)
		File inbound = new File(bpuXML_InboundPath)
		FileUtils.copyFile(xmlFile, inbound)
		
		//Verify file was picked up on inbound
		def con = false
		while (con) {
			def monitor = new HTTPBuilder(locHost).get(path : "/monitor/cache") {resp, json ->
				try {
					assert resp.status == 200
					assert json."1".fileStatusList[0].filename.equals(bpuFileName)
					con = true
				} catch (NullPointerException e) {
					//Pause for 4 seconds
					Thread.sleep(4000);
					println con
					
				}
			}
		}
	}
	
	
	@Test
	public void testTransformation() {
		//fail("Not yet implemented");
		
		
	}
}
