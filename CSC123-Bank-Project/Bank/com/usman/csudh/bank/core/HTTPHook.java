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
		
		String[] s=parseURL(typePath);
		
		Socket socket = new Socket(s[0], 80);
	
		
		// Sending request
		OutputStream out = socket.getOutputStream();
		out.write(("GET " + s[1] + " HTTP/1.1\r\n").getBytes());
		out.write(("Host: "+ s[0] +"\r\n").getBytes());
		out.write("\r\n".getBytes());

		// Reading response

		return socket.getInputStream();
	
	}
	
	private static String[] parseURL(String url) {
		String[] s=new String[2];
		
		String s1=url.substring(url.indexOf("//")+2,url.length());
		
		int p2=s1.indexOf("/");
		
		String hostName=s1.substring(0,p2);
		String path=s1.substring(p2,s1.length());
		
		s[0]=hostName;
		s[1]=path;
		return s;
		
	}

}
