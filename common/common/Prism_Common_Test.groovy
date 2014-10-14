package common;
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

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*
import groovy.util.XmlSlurper

class Prism_Common_Test {

	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
	def static final tomOutboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/outbound"
	
	
	
	
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
	
	
	public static void deleteOutbound() {
		if (SystemUtils.IS_OS_WINDOWS) {
			new File("${tomOutboundPath}").eachFile { f -> f.delete() }
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
	}
	
	
	
	public static void deleteOutbound(def path) {
		if (SystemUtils.IS_OS_WINDOWS) {
			new File("${path}").eachFile { f -> f.delete() }
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
	}
	
	
	
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
}
