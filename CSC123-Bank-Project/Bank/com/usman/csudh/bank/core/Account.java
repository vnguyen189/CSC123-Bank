package com.usman.csudh.bank.core;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.usman.csudh.util.UniqueCounter;

public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String accountName;
	private Customer accountHolder;
	private ArrayList<Transaction> transactions;
	private String accountCurrency;
	private double USDcurrency;
	
	public double getUSDcurrency(String accountCurrency, double balance) throws IOException, InterruptedException {
		if (accountCurrency.equals("USD"))
			return balance;
		else
			if(Bank.currencyFileOrWeb("config.txt")==true)
		return USDcurrency = balance * Bank.findCurrencyRate(Bank.currencyFileName("config.txt"), accountCurrency);
			else
		return USDcurrency = balance * Bank.fromWebRate(Bank.webServiceURL("config.txt"), accountCurrency);
	}

	private boolean open=true;
	private int accountNumber;

	protected Account(String name, Customer customer, String currency) {
		accountName=name;
		accountHolder=customer;
		accountCurrency = currency;
		transactions=new ArrayList<Transaction>();
		accountNumber=UniqueCounter.nextValue();
	}
	
	public String getAccountName() {
		return accountName;
	}

	public Customer getAccountHolder() {
		return accountHolder;
	}

	public String getAccountCurrency() {
		return accountCurrency;
	}

	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	public double getBalance() {
		double workingBalance=0;
		
		for(Transaction t: transactions) {
			if(t.getType()==Transaction.CREDIT)workingBalance+=t.getAmount();
			else workingBalance-=t.getAmount();
		}
		
		return workingBalance;
	}
	
	
	
	
	public void deposit(double amount)  throws AccountClosedException{
		double balance=getBalance();
		if(!isOpen()&&balance>=0) {
			throw new AccountClosedException("\nAccount is closed with positive balance, deposit not allowed!\n\n");
		}
		transactions.add(new Transaction(Transaction.CREDIT,amount));
	}
	
	
	
	
	public void withdraw(double amount) throws InsufficientBalanceException {
			
		double balance=getBalance();
			
		if(!isOpen()&&balance<=0) {
			throw new InsufficientBalanceException("\nThe account is closed and balance is: "+balance+"\n\n");
		}
		
		transactions.add(new Transaction(Transaction.DEBIT,amount));
	}
	
	public void close() {
		open=false;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}

	public String toString()  {
		String aName = null;
		try {
			aName = accountNumber+"("+accountName+")"+" : "+accountHolder.toString()+ " : " + getAccountCurrency().toUpperCase() + " : " +getBalance()+ " : "+ getUSDcurrency(accountCurrency, getBalance()) + " : " + (open?"Account Open":"Account Closed");
			return aName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	}
		catch (InterruptedException a) {
			a.printStackTrace();
		}
		catch (IOException b) {
			b.printStackTrace();
		}
		return aName;
	}
	 
	public void printTransactions(OutputStream out) throws IOException {
		
		out.write("\n\n".getBytes());
		out.write("------------------\n".getBytes());
	
		for(Transaction t: transactions) {
			out.write(t.toString().getBytes());
			out.write((byte)10);
		}
		out.write("------------------\n".getBytes());
		out.write(("Balance: "+ getAccountCurrency() + " " + getBalance()+"\n\n\n").getBytes());
		out.flush();
		
	}
public void printInformation(OutputStream out) throws IOException, InterruptedException {
	
		out.write("\n".getBytes());
		out.write(("Account Number: " + getAccountNumber() + "\n").getBytes());
		out.write(("Name: " + accountHolder.getFirstName() + " " + accountHolder.getLastName() + "\n").getBytes());
		out.write(("SNN: " + accountHolder.getSSN() + "\n").getBytes());
		out.write(("Currency: " + getAccountCurrency() + "\n").getBytes());
		out.write(("Currency Balance: " + getAccountCurrency() + " "  + getBalance() + "\n").getBytes());
		out.write(("USD Balance: USD " + getUSDcurrency(accountCurrency, getBalance()) + "\n").getBytes());
		out.write("\n".getBytes());
		out.flush();
		
	}
	
}
