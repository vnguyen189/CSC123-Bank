package com.usman.csudh.bank.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HTTPHook extends CurrencyReader {

	public HTTPHook(String path) {
		super(path);
			}

	@Override
	protected InputStream getInputStream(String typePath) throws Exception {
		
		String[] partOfURL =parseURL(typePath);
		
		Socket socket = new Socket(partOfURL[0], 80);
	
		
		// Sending request
		OutputStream out = socket.getOutputStream();
		out.write(("GET " + partOfURL[1] + " HTTP/1.1\r\n").getBytes());
		out.write(("Host: "+ partOfURL[0] +"\r\n").getBytes());
		out.write("\r\n".getBytes());

		// Reading response

		return socket.getInputStream();
	
	}
	
	private static String[] parseURL(String url) {
		String[] partOfURL =new String[2];
		
		String partOfURL2 =url.substring(url.indexOf("//")+2,url.length());
		
		int split =partOfURL2.indexOf("/");
		
		String hostName=partOfURL2.substring(0,split);
		String path=partOfURL2.substring(split,partOfURL2.length());
		
		partOfURL[0]=hostName;
		partOfURL[1]=path;
		return partOfURL;
		
	}

}
