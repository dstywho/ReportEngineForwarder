package com.redhat.qe.reportengineforwarder;


import spark.Spark;

import com.redhat.reportengine.client.RemoteAPI;

public class Server {

	private static final int DEFAULT_PORT = 27514;
	private RemoteAPI api;

	public Server(){
		this(DEFAULT_PORT);
	}
	public Server(int port){
		Spark.setPort(port);
		this.api = new RemoteAPI();
		setRoutes();
	}

	private void setRoutes() {
		Spark.get(new Routes.CreateReport(api, "/report/create"));
		Spark.get(new Routes.CreateTestGroup("/testgroup/create", api));
		Spark.get(new Routes.FinishReport("/report/finish", api));
		Spark.get(new Routes.StartTestCase("/testcase/start", api) );
		Spark.get(new Routes.PassTestCase("/testcase/pass", api));
		Spark.get(new Routes.FailTestCase("/testcase/fail", api));
	}


	public static void main(String[] args) {
		if (args.length > 0) {
			new Server(Integer.parseInt(args[0]));
		} else {
			new Server();
		}
	}
}
