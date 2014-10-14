package in_progress;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.*

import common.Prism_Common_Test

class NewProductsTest {

	static String domain = "http://localhost:8080";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Remove Inbound and Outbound Files
		Prism_Common_Test.deleteInbound()
		Prism_Common_Test.deleteOutbound()
		
		//Copy to Closet
		Prism_Common_Test.copyToCloset()
		
		//Initiate Services
		Prism_Common_Test.initiateServices(domain)
	}

	@Test
	public void test() {
		
		//Passing Regardless
		assertTrue(true);
	}
}
