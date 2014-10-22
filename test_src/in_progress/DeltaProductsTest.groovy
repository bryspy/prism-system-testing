package in_progress;


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

class DeltaProductsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}


	@Test
	public void testDeltaDetection() {
/*
 * Step 1: TODO  Ingest New Product
 */
		
			
/*
 * Step 2: TODO Ingest Same Product with a Delta
 */
		
/*
 * Step 3: TODO Verify Publish File has Delta
 */
		
/*
 * Step 4: TODO Verify Delta Exists in the Database
 */
		fail("Not yet implemented");
	}

}
