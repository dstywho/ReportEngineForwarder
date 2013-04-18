import org.calgb.test.performance.HttpSession;
import org.calgb.test.performance.HttpSession.HttpProtocol;
import org.calgb.test.performance.SimplifiedResponse;
import org.eclipse.jetty.util.URIUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;

import static org.mockito.Mockito.*;

import com.redhat.qe.reportengineforwarder.Server;
import com.redhat.reportengine.client.RemoteAPI;



public class ServerTest {
	private static RemoteAPI api;

	public void assertPositiveResponse(SimplifiedResponse response){
		Assert.assertEquals(200, response.getCode());
		
	}
	
	@BeforeClass
	public static void setup(){
		api = mock(RemoteAPI.class);
		Server.__createServer(27514, api);
	}
	
	@Test
	public void createReport() throws Exception{
		String name="my test suite";
		doNothing().when(api).initClient(isA(String.class));
		when(api.isClientConfigurationSuccess()).thenReturn(true);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/report/create?name=" + URIUtil.encodePath(name));
		assertPositiveResponse(response);
	}

	@Test
	public void createReportNegative() throws Exception{
		String name="my test suite";
		doNothing().when(api).initClient(isA(String.class));
		when(api.isClientConfigurationSuccess()).thenReturn(false);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/report/create?name=" + URIUtil.encodePath(name));
		Assert.assertEquals(500, response.getCode());
	}
	
	@Test
	public void createTestGroup() throws Exception{
		String name="my test suite";
		doNothing().when(api).insertTestGroup(isA(String.class));
		when(api.isClientConfigurationSuccess()).thenReturn(true);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/testgroup/create?name=" + URIUtil.encodePath(name));
		verify(api, atLeastOnce()).insertTestGroup(isA(String.class));
		assertPositiveResponse(response);
	}
	
	@Test
	public void createTestGroupNegative() throws Exception{
		String name="my test suite";
		doThrow(new Exception()).when(api).insertTestGroup(isA(String.class));
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/testgroup/create?name=" + URIUtil.encodePath(name));
		Assert.assertEquals(500, response.getCode());
	}
	
	@Test
	public void finishReport() throws Exception{
		String build="mybuild";
		doNothing().when(api).updateTestSuite("Completed", build);
		when(api.isClientConfigurationSuccess()).thenReturn(true);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/report/finish?build=" + build);
		Assert.assertEquals(200, response.getCode());
		
	}
	@Test
	public void finishReportNoParams() throws Exception{
		String build="mybuild";
		doNothing().when(api).updateTestSuite("Completed", null);
		when(api.isClientConfigurationSuccess()).thenReturn(true);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/report/finish");
		Assert.assertEquals(200, response.getCode());
		
	}
	
	@Test
	public void finishReportNegative() throws Exception{
		String build="mybuild";
		doThrow(new Exception("blah")).when(api).updateTestSuite("Completed", build);
		when(api.isClientConfigurationSuccess()).thenReturn(true);
		
		HttpSession session = new HttpSession("localhost", Server.DEFAULT_PORT, HttpProtocol.HTTP);
		SimplifiedResponse response = session.executeGet("/report/finish?build=" + build);
		Assert.assertEquals(500, response.getCode());
		Assert.assertTrue(response.getBody().contains("blah"));
		
		System.out.println(response.getBody());
	}
}
