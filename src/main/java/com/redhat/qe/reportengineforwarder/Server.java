package com.redhat.qe.reportengineforwarder;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import com.redhat.reportengine.client.RemoteAPI;

public class Server {
	public static class ResponseWrapper {

		private Response response;

		/**
		 * @param response
		 */
		public ResponseWrapper(Response response) {
			super();
			this.response = response;
		}

		/**
		 * @return the response
		 */
		public Response getResponse() {
			return response;
		}

		/**
		 * @param response
		 *            the response to set
		 */
		public void setResponse(Response response) {
			this.response = response;
		}

		public String toString() {
			return String.format("%s", response.body());
		}
	}

	public static abstract class ActiveReportRoute extends Route {

		private RemoteAPI api;

		protected ActiveReportRoute(String path, RemoteAPI api) {
			super(path);
			this.api = api;
		}

		public abstract Object handleApi(Request request, Response response) throws Exception;

		@Override
		public Object handle(Request request, Response response) {
			if (api.isClientConfigurationSuccess()) {
				return runApiHandle(request, response);
			} else {
				response.status(400);
				response.body("api is not connected");
				return new ResponseWrapper(response);
			}
		}

		/**
		 * @param request
		 * @param response
		 */
		private Object runApiHandle(Request request, Response response) {
			try {
				return handleApi(request, response);
			} catch (Exception e) {
				response.status(400);
				response.body(e.getMessage());
				return new ResponseWrapper(response);
			}
		}

	}

	private static String getNameParam(Request request, Response response) {
		String name = request.queryParams("name");
		if (name == null && name.isEmpty()) {
			throw new RuntimeException("name param not given");
		}
		return name;
	}

	private static Response sucessResponse(Response response) {
		response.status(200);
		response.body("OK");
		return response;
	}

	public static void main(String[] args) {
		final RemoteAPI reportEngine = new RemoteAPI();

		Spark.get(new Route("/report/create") {
			@Override
			public Object handle(Request request, Response response) {

				startReport(reportEngine, request, response);
				if (reportEngine.isClientConfigurationSuccess())
					response.status(200);
				else {
					response.status(400);
					response.body("report could not be created");
				}
				return new ResponseWrapper(response);
			}

			private void startReport(final RemoteAPI reportEngine, Request request, Response response) {
				try {
					reportEngine.initClient(request.queryParams("name"));
				} catch (Exception e) {
				}
			}
		});

		Spark.get(new ActiveReportRoute("/report/finish", reportEngine) {

			@Override
			public Object handleApi(Request request, Response response) throws Exception {
				reportEngine.updateTestSuite("Completed", "TODO buildverison empty");
				response.body("report finished");
				return new ResponseWrapper(response);
			}
		});

		Spark.get(new ActiveReportRoute("/testcase/start", reportEngine) {

			@Override
			public Object handleApi(Request request, Response response) throws Exception {
				String name = getNameParam(request, response);
				reportEngine.insertTestCase(name, "Running");
				return new ResponseWrapper(sucessResponse(response));
			}

		});
		
		Spark.get(new ActiveReportRoute("/testcase/pass", reportEngine) {

			@Override
			public Object handleApi(Request request, Response response) throws Exception {
				reportEngine.updateTestCase("Passed");
				return new ResponseWrapper(sucessResponse(response));
			}

		});
		Spark.get(new ActiveReportRoute("/testcase/fail", reportEngine) {

			@Override
			public Object handleApi(Request request, Response response) throws Exception {
				reportEngine.updateTestCase("Failed",request.queryParams("message"));
				return new ResponseWrapper(sucessResponse(response));
			}

		});

	}
}
