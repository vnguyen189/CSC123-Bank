package com.usman.csudh.bank.core;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Bank {
	
	private static Map<Integer,Account> accounts=new TreeMap<Integer,Account>();
	
	public static Account openCheckingAccount(String firstName, String lastName, String ssn, double overdraftLimit, String currency) {
		Customer c=new Customer(firstName,lastName, ssn);
		Account a=new CheckingAccount(c,overdraftLimit, currency);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}
	
	public static Account openSavingAccount(String firstName, String lastName, String ssn, String currency) {
		Customer c=new Customer(firstName,lastName, ssn);
		Account a=new SavingAccount(c, currency);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}

	
	
	public static Account lookup(int accountNumber) throws NoSuchAccountException{
		if(!accounts.containsKey(accountNumber)) {
			throw new NoSuchAccountException("\nAccount number: "+accountNumber+" not found!\n\n");
		}
		
		return accounts.get(accountNumber);
	}
	
	public static void makeDeposit(int accountNumber, double amount) throws AccountClosedException, NoSuchAccountException{
		
		lookup(accountNumber).deposit(amount);
		
	}
	
	public static void makeWithdrawal(int accountNumber, double amount) throws InsufficientBalanceException, NoSuchAccountException {
		lookup(accountNumber).withdraw(amount);
	}
	
	public static void closeAccount(int accountNumber) throws NoSuchAccountException {
		lookup(accountNumber).close();
	}

	
	public static double getBalance(int accountNumber) throws NoSuchAccountException {
		return lookup(accountNumber).getBalance();
	}

	public static void listAccounts(OutputStream out) throws IOException{
		
		out.write((byte)10);
		Collection<Account> col=accounts.values();
		
		for (Account a:col) {
			out.write(a.toString().getBytes());
			out.write((byte)10);
		}
		
		out.write((byte)10);
		out.flush();
	}
	
	public static void printAccountTransactions(int accountNumber, OutputStream out) throws IOException,NoSuchAccountException{
		
		lookup(accountNumber).printTransactions(out);
	}
	
	public static void printAccountInformation(int accountNumber, OutputStream out) throws IOException,NoSuchAccountException{
		
		lookup(accountNumber).printInformation(out);
	}
	
				
	public static boolean loadCurrencyFile (String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if(!file.exists() || !file.canRead() ) 
			return false;
	
		return true;
	}
	
	public static double findCurrencyRate (String keyCurrency) throws FileNotFoundException {
		File file = new File("exchange-rate.csv");
		Scanner inputFile = new Scanner(file);
		while (inputFile.hasNext()) {
			String str = inputFile.nextLine();
				if (str.toUpperCase().contains(keyCurrency.toUpperCase())) {
					String [] splitt = str.split(",");	
					double rate = Double.parseDouble(splitt[2]);
					return rate;
				}
		}
		return 0;
	}
	
	public static boolean lookUpCurrency (String keyCurrency) throws FileNotFoundException {
		File file = new File("exchange-rate.csv");
		Scanner inputFile = new Scanner(file);

		while (inputFile.hasNext()) {
			String str = inputFile.nextLine();
				if (str.toUpperCase().contains(keyCurrency.toUpperCase())) 
					return true;
				}
		return false;
	}
	
	public static double convertingCurrency (String buyCurrency, String sellCurrency, double currencyRate, double convertAmount) {
		double convertedCurrency = 0;
		if (sellCurrency.toUpperCase().equals("USD") && buyCurrency.toUpperCase().equals("USD") == false)
			return convertedCurrency = convertAmount / currencyRate;
		else if (sellCurrency.toUpperCase().equals("USD")  == false && buyCurrency.toUpperCase().equals("USD")) 
			return convertedCurrency = convertAmount * currencyRate;
		else if (sellCurrency.toUpperCase().equals("USD")  == true && buyCurrency.toUpperCase().equals("USD") == true) 
			return convertedCurrency = convertAmount * currencyRate;
		
	return convertedCurrency;		
	}
	
	}
