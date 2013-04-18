package com.redhat.qe.reportengineforwarder;

import spark.Response;

public  class ResponseWrapper {

		private Response response;
		private int code;
		private String body;

		/**
		 * @param response
		 */
		public ResponseWrapper(Response response, int code, String body) {
			super();
			this.code = code;
			this.body = body;
			this.response = response;
			this.response.status(code);
			this.response.body("body");
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
			return String.format("%s: %s", code, body);
		}
	}
