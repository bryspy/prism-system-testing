import static org.junit.Assert.*;
import org.junit.Test;


import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class NewProductsTest_gr {

	String monitorStatus = "http://localhost:8080/monitor/status";

	@Test
	public void test() {
//		URLConnection con = new URL(monitorStatus).openConnection();
//		//con.setRequestProperty("Accept-Charset", charset);
//		InputStream response = con.getInputStream();
//		String resJson = response.toString();
//		System.out.println(resJson);
		//assertTrue(true);
		
//		@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
//		def http = new groovyx.net.http.HTTPBuilder('http://www.codehaus.org')
		
		
		@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0-RC2' )
		def http = new HTTPBuilder('http://localhost:8080/monitor/status');
		
	}

}
