package com.usman.csudh.bank;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.usman.csudh.bank.core.Account;
import com.usman.csudh.bank.core.AccountClosedException;
import com.usman.csudh.bank.core.Bank;
import com.usman.csudh.bank.core.InsufficientBalanceException;
import com.usman.csudh.bank.core.NoSuchAccountException;
import com.usman.csudh.util.UIManager;

public class MainBank {

	//All messages are declared as constants to make it easier to change. Also, to ensure future proofing in case the application need to be made available
	//in more than one languages
	public static final String MSG_ACCOUNT_OPENED = "%nAccount opened, account number is: %s%n%n";
	public static final String MSG_ACCOUNT_CLOSED = "%nAccount number %s has been closed, balance is %s%n%n";
	public static final String MSG_ACCOUNT_NOT_FOUND = "%nAccount number %s not found! %n%n";
	public static final String MSG_FIRST_NAME = "Enter first name:  ";
	public static final String MSG_LAST_NAME = "Enter last name:  ";
	public static final String MSG_SSN = "Enter Social Security Number:  ";
	public static final String MSG_ACCOUNT_NAME = "Enter account name:  ";
	public static final String MSG_ACCOUNT_OD_LIMIT = "Enter overdraft limit:  ";
	public static final String MSG_ACCOUNT_CREDIT_LIMIT = "Enter credit limit:  ";
	public static final String MSG_AMOUNT = "Enter amount: ";
	public static final String MSG_ACCOUNT_NUMBER = "Enter account number: ";
	public static final String MSG_ACCOUNT_ACTION = "%n%s was %s, account balance is: %s%n%n";
	public static final String MSG_SELLING_CURRENCY = "The currency you are selling: ";
	public static final String MSG_AMOUNT_SELLING = "The amount you are selling: ";
	public static final String MSG_CURRENCY_BUYING = "The currency you are buying: ";
	public static final String MSG_ACCOUNT_CURRENCY = "Account Currency: ";
	public static final String MSG_CURRENCY_FILE_ERROR = "**Currency file could not be loaded, Currency Conversion Service and Foreign Currency accounts are not available **";
	public static final String MSG_CURRENCY_CONVERT_ERROR = "Currency cannot be convert. One of the currency should be USD %n%n";
	public static final String MSG_CURRENCY_FILE = "exchange-rate.csv";
	public static final String MSG_EXCHANGE_SUCCESS = "The exchange rate is %s and you will get %s %.2f%n";
	public static final String MSG_CURRENCY_ERROR = "Exchange rates cannot be found %n%n";
	public static final String MSG_GOODBYE_EXIT = "Thank you for using this bank application. Goodbye and have a good day!";
	public static final String MSG_WEBSITE_ADDRESS = "http://www.usman.cloud/banking/exchange-rate.csv";
	public static final String MSG_WEBSITE_ERROR = "Website have problems. Cannot load currency";
	public static final String MSG_CONFIG_FILE = "config.txt";
	//Declare main menu and prompt to accept user input
	public static final String[] menuOptions = { "Open Checking Account%n","Open Saving Account%n", "List Accounts%n","View Statement%n","Account Information%n", "Deposit Funds%n", "Withdraw Funds%n"
		, "Foreign Exchange%n", "Close an Account%n", "Exit%n" };
	public static final String MSG_PROMPT = "%nEnter choice: ";

	
	//Declare streams to accept user input / provide output
	InputStream in;
	OutputStream out;
	
	
	//Constructor
	public MainBank(InputStream in, OutputStream out) {
		this.in=in;
		this.out=out;
	}
	
	
	//Main method. 
	public static void main(String[] args) throws InterruptedException, IOException {

		new MainBank(System.in,System.out).run();

	}
	
	
	//The core of the program responsible for providing user experience.
	public void run() throws InterruptedException, IOException {
		
		Account acc;
		int option = 0;

		
		
		if (Bank.canReadFromWeb(MSG_WEBSITE_ADDRESS) == false) {
			System.out.println( MSG_WEBSITE_ERROR);
		}
		
		UIManager ui = new UIManager(this.in,this.out,menuOptions,MSG_PROMPT);
		try {

			do {
				option = ui.getMainOption(); //Render main menu
				
				switch (option) {
				case 1:
					
					while (Bank.canReadFromWeb(MSG_WEBSITE_ADDRESS) == true) {
					//Compact statement to accept user input, open account, and print the result including the account number
						String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
						
						while (Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, currency) == false) {
							currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
						}		
						
						while (Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, currency) == true) {
							ui.print(MSG_ACCOUNT_OPENED,
							new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
									ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
									ui.readDouble(MSG_ACCOUNT_OD_LIMIT), currency.toUpperCase()).getAccountNumber() });
							break;
						}
						
					break;
					}
					
					while (Bank.canReadFromWeb(MSG_WEBSITE_ADDRESS) == false) {
						ui.print(MSG_ACCOUNT_OPENED,
								new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
										ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
										ui.readDouble(MSG_ACCOUNT_OD_LIMIT), "USD").getAccountNumber() });
						break;
						}
					
					break;
					
				case 2:
					while (Bank.canReadFromWeb(MSG_WEBSITE_ADDRESS) == true) {
					//Compact statement to accept user input, open account, and print the result including the account number
						String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
						while (Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, currency) == false) {
							currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
						}
						
						while (Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, currency) == true) {
							ui.print(MSG_ACCOUNT_OPENED,
								new Object[] { Bank
										.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), currency.toUpperCase())
										.getAccountNumber() }); 
							break;
							}
						
						break;
					}
					while (Bank.canReadFromWeb(MSG_WEBSITE_ADDRESS) == false) {
						ui.print(MSG_ACCOUNT_OPENED,
							new Object[] { Bank
									.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
											ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), "USD")
									.getAccountNumber() }); break;}
					
					break;

				case 3:
					
					//Get bank to print list of accounts to the output stream provided as method argument
					Bank.listAccounts(this.out);
					break;
					
				case 4:
					
					//find account and get the account to print transactions to the  output stream provided in method arguments
					try {
						Bank.printAccountTransactions(ui.readInt(MSG_ACCOUNT_NUMBER),this.out);
					} catch (NoSuchAccountException e1) {
						this.handleException(ui, e1);

					}		
					
					break;
					
					
				case 5: 
					try {
					Bank.printAccountInformation(ui.readInt(MSG_ACCOUNT_NUMBER), this.out);
					}
					
					catch (NoSuchAccountException e1) {
						this.handleException(ui, e1);

					}		
					
					
					break;

				case 6:
					//find account, deposit money and print result
					
					try {
						int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.makeDeposit(accountNumber, ui.readDouble(MSG_AMOUNT));
						ui.print(MSG_ACCOUNT_ACTION, new Object[] {"Deposit","successful",Bank.getBalance(accountNumber)});
					}
					catch(NoSuchAccountException | AccountClosedException e) {
						this.handleException(ui, e);

					}
					break;
					
				case 7:
					//find account, withdraw money and print result
					try {
						int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.makeWithdrawal(accountNumber, ui.readDouble(MSG_AMOUNT));
						ui.print(MSG_ACCOUNT_ACTION, new Object[] {"Withdrawal","successful",Bank.getBalance(accountNumber)});
						
					}
					catch(NoSuchAccountException | InsufficientBalanceException e) {
						this.handleException(ui, e);

					}
					break;

				case 8:
							
					String sellingCurrency = ui.readToken(MSG_SELLING_CURRENCY);
					double amountSelling = ui.readDouble(MSG_AMOUNT_SELLING);
					String buyingCurrency = ui.readToken(MSG_CURRENCY_BUYING);
		
					if (Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, buyingCurrency) == true && Bank.lookUpCurrencyWeb(MSG_WEBSITE_ADDRESS, sellingCurrency) == true) {
					
						if (sellingCurrency.toUpperCase().equals("USD") || buyingCurrency.toUpperCase().equals("USD")) {
							if(sellingCurrency.toUpperCase().equals("USD") == true && buyingCurrency.toUpperCase().equals("USD") == false) {
							ui.print(MSG_EXCHANGE_SUCCESS,
								new Object[] { Bank.fromWebRate(MSG_WEBSITE_ADDRESS, buyingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.fromWebRate(MSG_WEBSITE_ADDRESS, buyingCurrency), amountSelling)});
					}
							else if (sellingCurrency.toUpperCase().equals("USD") == false && buyingCurrency.toUpperCase().equals("USD") == true) {
								ui.print(MSG_EXCHANGE_SUCCESS,
										new Object[] { Bank.fromWebRate(MSG_WEBSITE_ADDRESS, sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.fromWebRate(MSG_WEBSITE_ADDRESS, sellingCurrency), amountSelling)});
							}
							else if (sellingCurrency.toUpperCase().equals("USD") == true && buyingCurrency.toUpperCase().equals("USD") == true) {
								ui.print(MSG_EXCHANGE_SUCCESS,
										new Object[] { Bank.fromWebRate(MSG_WEBSITE_ADDRESS, sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.fromWebRate(MSG_WEBSITE_ADDRESS, sellingCurrency), amountSelling)});
							}
						
						
						}
						else if (sellingCurrency.toUpperCase().equals("USD") == false && (buyingCurrency.toUpperCase().equals("USD") == false))  {
							ui.print(MSG_CURRENCY_CONVERT_ERROR,
								new Object[] {});
					}	
				}
					else
						ui.print(MSG_CURRENCY_ERROR,
								new Object[] {});
						
					break;
					
					
				case 9:
					//find account and close it
					
					
					try {
						int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
						Bank.closeAccount(accountNumber);
						ui.print(MSG_ACCOUNT_CLOSED,
								new Object[] { accountNumber, Bank.getBalance(accountNumber) });
						
					} catch (NoSuchAccountException e) {
						this.handleException(ui, e);

					}
					break;
					
					
				case 10:
					ui.print(MSG_GOODBYE_EXIT,
							new Object[] {});
					System.exit(0);
					break;
				
				}
			} while (option != menuOptions.length);

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	
	private  void handleException(UIManager ui, Exception e) throws IOException{
		ui.print(e.getMessage(), new Object[] { });
	}


}
