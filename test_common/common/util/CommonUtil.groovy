package common.util;

import java.io.File;
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
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import groovy.io.FileType

import common.prism.CommonPrism

class CommonUtil {

	def static final tomClosetPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/closet"
	def static final tomInboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/inbound"
	def static final tomOutboundPath = "C:/Program Files/Apache Software Foundation/Tomcat 7.0/prism/outbound"
	
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
	 * @param resourceFile
	 * @param closetDir
	 */
	public static void copyResourceToInbound(File resourceFile, File inboundDir) {
		File resFile = CommonPrism.getResourceFile(resourceFile);
		FileUtils.copyFileToDirectory(resourceFile, inboundDir)
		
	}
	
	/**
	 *
	 * @param filename
	 * @return
	 */
	public static File getClosetFile(String filename) {
		File closFile;
		
		if (SystemUtils.IS_OS_WINDOWS) {
			//Copy from /common/.../resources to Tomca/prism/closet
			closFile = new File("${tomClosetPath}/${filename}")
			assert closFile.exists()
		}else if ( SystemUtils.IS_OS_LINUX )
		{
			//Linux Paths?!?!
			throw new Exception("Missing Unix Paths")
		}
		
		return closFile;
	}
	
	/**
	 *
	 * @param file
	 * @return
	 */
	public static File randomExRefIdToFile(File file) {
		
		def xml = new XmlSlurper(false, false).parse(file)
//		def xml = new XmlSlurper().parse(file)
		
		Random rand = new Random()
		
		def prod_id_list = []
		def prod_id = rand.nextInt(100000000+1).toString()
		def product = xml.items.product.find{
			(it.companyID == 'testcoid' && it.catalogID == '12345678' && it.productName == 'Test Product') }
		
		//Is Product Node within Items?
		try {
			assert !product.toString().equals(""), "Error: xml.items.product Not Found in ${file.name}!!";
		}
		catch (AssertionError e) {
			
			try {
				product = xml.product.find{
				(it.companyID == 'testcoid' && it.catalogID == '12345678' && it.productName == 'Test Product') }
				
				assert !product.toString().equals(""), "Error: xml.prodcut Not Found in ${file.name}!!";
				
			} catch (AssertionError a) {
				throw new Exception("Cannot Find Product Node! Check XML Structure.")
			}
		}
		
		
		
		product.externalReferenceID = prod_id
		
		File newOutFile = new File("${file.parentFile}/Test${file.name}")
		newOutFile.createNewFile()
//		FileUtils.copyFile(file, newOutFile)
		println newOutFile.absolutePath
		writeXmlToFile(newOutFile, xml)
		
		return newOutFile;
	}
	
	
	/**
	 *
	 * @param file
	 * @param xml
	 */
	public static void writeXmlToFile(File file, def xml) {
		FileWriter writer = new FileWriter(file);
		BufferedWriter buff = new BufferedWriter(writer);
		buff.write(XmlUtil.serialize(xml))
		buff.close();
	}
	
	
}
