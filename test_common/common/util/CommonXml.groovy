package common.util


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

class CommonXml {
	
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
		
		
		//set externalReferenceID to newly generated prod_id
		product.externalReferenceID = prod_id
		
		//create a new file to write updated xml to
		File newOutFile = new File("${file.parentFile}/BPU_${file.name}")
		newOutFile.createNewFile()
		
		println newOutFile.absolutePath
		println "prod_id: ${prod_id}"
		
		//Write it
		CommonUtil.writeXmlToFile(newOutFile, xml)
		
		return newOutFile;
	}

}
