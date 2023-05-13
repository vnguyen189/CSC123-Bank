package com.usman.csudh.bank.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileHook extends CurrencyReader{

	public FileHook(String path) {
		super(path);
	}

	@Override
	protected InputStream getInputStream(String typePath) throws Exception {
		return new FileInputStream(new File(typePath));
	}


}
