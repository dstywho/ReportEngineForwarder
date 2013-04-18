package com.redhat.qe.reportengineforwarder;

import spark.Request;
import spark.Response;

public class Helpers {

	public static String getNameParam(Request request, Response response) {
		String name = request.queryParams("name");
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("name param not given");
		}
		return name;
	}
}
