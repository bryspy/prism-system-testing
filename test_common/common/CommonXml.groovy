package common


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

class CommonXml {
	
	/**
	 * 
	 * @param file
	 * @param exRefId
	 * @return File with random id in External Reference Id
	 */
	public static File randomExRefIdToFile(File file, String exRefId) {
		
		def xml = new XmlSlurper(false, false).parse(file)
		
		def product = xml.items.product
		
		//Is Product Node within Items?
		try {
			assert !product.toString().equals(""), "Error: xml.items.product Not Found in ${file.name}!!";
		}
		catch (AssertionError e) {
			
			try {
				product = xml.product/*.find{
				(it.companyID == 'testcoid' && it.catalogID == '12345678' && it.productName == 'Test Product') } */
				
				assert !product.toString().equals(""), "Error: xml.prodcut Not Found in ${file.name}!!";
				
			} catch (AssertionError a) {
				throw new Exception("Cannot Find Product Node! Check XML Structure.")
			}
		}
		
		
		//set externalReferenceID to newly generated prod_id
		product.externalReferenceID = exRefId
		
		//create a new file to write updated xml to
		File newOutFile = new File("${file.parentFile}/BPU_${file.name}")
		newOutFile.createNewFile()
		
		println newOutFile.absolutePath
		println "id: ${exRefId}"
		
		//Write it
		CommonUtil.writeXmlToFile(newOutFile, xml)
		
		return newOutFile;
	}
	
	/**
	 * Takes an array of IDs and creates a <product> node for each value and writes that to a file called BPU_MultiProduct.xml 
	 * @param inFile
	 * @param arrayIds
	 * @return File with provided IDs in serialized XML
	 */
	public static File randomArrayIdsToFile(File inFile, def arrayIds ) {
		def inXml = new XmlSlurper(false, false).parse(inFile)
		def outXml
		
		GPathResult inProd = inXml.items.product
		GPathResult inItems = inXml.items
		
		
		//Is Product Node within Items?
		try {
			assert !inProd.toString().equals(""), "Error: xml.items.product Not Found in ${inFile.name}!!";
			
			def id = arrayIds.pop()
			inProd.externalReferenceID = id
			
			def prodString = "${serializeXml(inProd).drop(38)}"
			def addProduct
			
			arrayIds.each { i ->
				inProd.externalReferenceID = i
				prodString = prodString + "${serializeXml(inProd).drop(38)}"
			}
			//TODO Add <items> to beginning to String
			prodString = "<items>" + prodString + "</items>"
			

			//replace fully built Items node
			inXml.items.replaceNode{
				mkp.yield(new XmlSlurper(false,false).parseText(prodString))
			}
			
			def outString = serializeXml(inXml)
			
			outXml = new XmlSlurper(false, false).parseText(outString)
			
		}
		catch (AssertionError e) {
			
			try {
				inProd = inXml.product
				
				assert !inProd.toString().equals(""), "Error: xml.prodcut Not Found in ${inFile.name}!!";
				
				
			} catch (AssertionError a) {
				throw new Exception("Cannot Find Product Node! Check XML Structure.")
			}
		}
		
		//create a new file to write updated xml to
		File newOutFile = new File("${inFile.parentFile}/BPU_MultiProduct.xml")
		newOutFile.createNewFile()
		
		println newOutFile.absolutePath
		
		//Write it
		CommonUtil.writeXmlToFile(newOutFile, outXml)
		
		return newOutFile
	}
	
	
	
	public static String serializeXml(GPathResult xml)
	{
		def mb = new groovy.xml.StreamingMarkupBuilder()
		mb.encoding = "UTF-8"
		
		XmlUtil.serialize(mb.bind {
				mkp.yield xml
		} )
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static String randomIdAsString() {
		Random rand = new Random()
		
		def prod_id = rand.nextInt(100000000+1).toString()
		
		return prod_id.toString()
	}

}
