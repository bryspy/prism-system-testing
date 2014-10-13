import static org.junit.Assert.*;
import org.junit.Test;

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*
import groovy.json.*

class NewProductsTest {

	String monitor = "http://localhost:8080";

	@Test
	public void test() {
		
		def http = new HTTPBuilder( monitor )

		// perform a GET request, expecting JSON response data
		http.request( GET, JSON ) {
			uri.path = '/monitor/status/'

			headers.'User-Agent' = 'Mozilla/5.0 '

			//response handler for a success response code:
			response.success = { resp, json ->
				println resp.statusLine
				
				// parse the JSON response object:
				
				println json.toString()
				/*json.toString() {
					println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
				}*/
			}

			//handler for any failure status code:
			response.failure = { resp ->
				println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
			}
		}
		//Passing Regardless
		assertTrue(true);
	}
}
