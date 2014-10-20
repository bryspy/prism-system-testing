package regression.sprint1_1_CanonicalIngestion
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javassist.bytecode.stackmap.BasicBlock.Catch;

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils

import com.sun.org.apache.xalan.internal.xsltc.compiler.Import;

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.util.XmlSlurper
import common.Prism_Common_Test

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Sprint1_1EndToEndTest {

	def static String domain = "http://localhost:8080";
	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
	def static final tomOutboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/outbound"
	def static outFilename = "BPUFile_1.xml"
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Remove Inbound and Outbound Files
		Prism_Common_Test.deleteInbound(tomInboundPath)
		Prism_Common_Test.deleteOutbound(tomOutboundPath)
		
		//Copy to Closet 
		Prism_Common_Test.copyToCloset(tomClosetPath)
		
		//Initiate Services
		Prism_Common_Test.initiateServices(domain)
		
	}

	@Before
	public void setUp() throws Exception {
		//TODO
		
	}
		
	
	
	@Test
	public void verifyTransformation() {
		
		def bpuFileName = "BPU_Digital_River_48513_20140908_210257.xml"
		
		def bpuXML_ClosetPath = "${tomClosetPath}/${bpuFileName}"
		def bpuXML_InboundPath = "${tomInboundPath}/${bpuFileName}"
		
		//Step1: Copy Closet XML to /inbound
		File xmlFile = new File(bpuXML_ClosetPath)
		File inbound = new File(bpuXML_InboundPath)
		FileUtils.copyFile(xmlFile, inbound)
		
		//Verify file was picked up on inbound
		assert Prism_Common_Test.isFileIngested(bpuFileName, domain) == true
		
		File outXmlFile = Prism_Common_Test.isOutboundPublished(tomOutboundPath, outFilename)
		def items = new XmlSlurper().parse(outXmlFile)
		//Verify that shortDescription is not empty
		assert !(items.product.shortDescription.isEmpty())
	}
}
