package com.redhat.qe.reportengineforwarder;

import java.io.PrintWriter;
import java.io.StringWriter;

import spark.Request;
import spark.Response;
import spark.Route;

import com.redhat.reportengine.client.RemoteAPI;

public class Routes {
	static final class FailTestCase extends ActiveReportRoute {
		public  FailTestCase(String path, RemoteAPI api) {
			super(path, api);
		}

		@Override
		public Object handleApi(RemoteAPI api, Request request, Response response) throws Exception {
			api.updateTestCase("Failed", request.queryParams("message"));
			return new ResponseWrapper(response, 200, "OK");
		}
	}

	public static final class PassTestCase extends ActiveReportRoute {
		public PassTestCase(String path, RemoteAPI api) {
			super(path, api);
		}

		@Override
		public Object handleApi(RemoteAPI api, Request request, Response response) throws Exception {
			api.updateTestCase("Passed");
			return new ResponseWrapper(response, 200, "OK");
		}
	}

	public static abstract class ActiveReportRoute extends RemoteApiRoute {
	
		protected ActiveReportRoute(String path, RemoteAPI api) {
			super(api, path);
		}
	
		public abstract Object handleApi(RemoteAPI api, Request request, Response response) throws Exception;
	
		@Override
		public Object handle(Request request, Response response) {
			if (api().isClientConfigurationSuccess()) {
				return runApiHandle(api(), request, response);
			} else {
				return new ResponseWrapper(response, 500, "api is not connected");
			}
		}
	
		private Object runApiHandle(RemoteAPI api, Request request, Response response) {
			try {
				return handleApi(api(), request, response);
			} catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				return new ResponseWrapper(response, 500, errors.toString());
			}
		}
	
	}

	public static class CreateReport extends RemoteApiRoute {
	
		protected CreateReport(RemoteAPI api, String path) {
			super(api, path);
		}
	
		@Override
		public Object handle(Request request, Response response) {
	
			startReport(api(), request, response);
			if (api().isClientConfigurationSuccess()) {
				return new ResponseWrapper(response, 200, "report started");
			} else {
				return new ResponseWrapper(response, 500, "report could not be created");
			}
	
		}

		private static void startReport(final RemoteAPI reportEngine, Request request, Response response) {
			try {
				reportEngine.initClient(request.queryParams("name"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class CreateTestGroup extends ActiveReportRoute {
	
		protected CreateTestGroup(String path, RemoteAPI api) {
			super(path, api);
		}
	
		@Override
		public Object handleApi(RemoteAPI api, Request request, Response response) throws Exception {
			return createTestGroup(api(), response);
		}
	
		private ResponseWrapper createTestGroup(final RemoteAPI reportEngine, Response response) {
			try {
				reportEngine.insertTestGroup("tests");
			} catch (Exception e) {
				return new ResponseWrapper(response, 500, "test group not be created");
			}
			return new ResponseWrapper(response, 500, "test group created");
		}
	}

	public static class FinishReport extends ActiveReportRoute {
	
		protected FinishReport(String path, RemoteAPI api) {
			super(path, api);
		}
	
		@Override
		public Object handleApi(RemoteAPI api, Request request, Response response) throws Exception {
			api().updateTestSuite("Completed", "TODO buildverison empty");
			return new ResponseWrapper(response, 200, "report completed");
		}
	
	}

	public static abstract class RemoteApiRoute extends Route {
	
		private RemoteAPI api;
	
		protected RemoteApiRoute(RemoteAPI api, String path) {
			super(path);
			this.api = api;
		}
	
		public RemoteAPI api() {
			return api;
		}
	
	}

	public static class StartTestCase extends ActiveReportRoute {
	
		protected StartTestCase(String path, RemoteAPI api) {
			super(path, api);
		}
	
		@Override
		public Object handleApi(RemoteAPI api, Request request, Response response) throws Exception {
			String name = Helpers.getNameParam(request, response);
			api.insertTestCase(name, "Running");
			return new ResponseWrapper(response, 200, "reporting that test case as running");
		}
	
		private ResponseWrapper createTestGroup(final RemoteAPI reportEngine, Response response) {
			try {
				reportEngine.insertTestGroup("tests");
			} catch (Exception e) {
				return new ResponseWrapper(response, 500, "test group not be created");
			}
			return new ResponseWrapper(response, 500, "test group created");
		}
	}

}
