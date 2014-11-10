package in_development;

import static org.junit.Assert.*;

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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		println "\n\n====Start Test '${MultiNewProductsTest.name}'====\n\n"
		
		//Remove Inbound and Outbound Files
		CommonUtil.deleteInbound()
		CommonUtil.deleteOutbound()
		
	}


	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
