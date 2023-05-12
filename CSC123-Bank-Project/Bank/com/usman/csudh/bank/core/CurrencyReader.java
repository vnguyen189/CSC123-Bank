package com.usman.csudh.bank.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class CurrencyReader {
public String typePath;

	protected CurrencyReader(String path) {
		this.typePath=path;
	}
	public static CurrencyReader getInstance(String type, String PathName) throws Exception{
			if(type.equalsIgnoreCase("file")) {
			return new FileHook(PathName);
		}
		else if(type.equalsIgnoreCase("http")) {
			return new HTTPHook(PathName);
		}
		else {
			throw new Exception("Type " +type+ " not understood!");
		}
	}
		
	public ArrayList<String> readCurrencies() throws Exception{
		//get an input steam
		InputStream in=getInputStream(typePath);
		//Create stream readers / buffered reader
		BufferedReader br=new BufferedReader(new InputStreamReader(in));
		
		ArrayList<String> list=new ArrayList<String>();
		
		String line=null;
		//read lines 
		while((line=br.readLine())!=null) {
			if (line.startsWith("Server"))
				continue;
			if(line.startsWith("Last-Modified"))
				continue;
			if(line.startsWith("ETag"))
				continue;
			if(line.startsWith("Accept-Ranges"))
				continue;
			if(line.startsWith("Content-Length"))
				continue;
			if(line.startsWith("Content-Type"))
				continue;
			if(line.startsWith("HTTP"))
				continue;
			if(line.startsWith("HTTPHook"))
				continue;
			if(line.startsWith("Date:"))
				continue;
			if(line.startsWith("HTTP/1.1"))
				continue;
			//add lines to arraylist
			list.add(line);
		}
		
		//return array list 
		
		return list;
		
	}
	
	protected abstract InputStream getInputStream(String typePath) throws Exception; 
	

}