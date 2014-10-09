import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

/**
 * 
 * @author bikonomovski
 *
 */
public class NewProductsTest {

	String monitorStatus = "http://localhost:8080/monitor/status";
	

	@Test
	public void testNewProducts() throws MalformedURLException, IOException {
		URLConnection con = new URL(monitorStatus).openConnection();
		//con.setRequestProperty("Accept-Charset", charset);
		InputStream response = con.getInputStream();
		String resJson = response.toString();
		System.out.println(resJson);
		assertNotNull(resJson);
		//fail("Not yet implemented");
		
	}

}
