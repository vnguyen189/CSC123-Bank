package com.usman.csudh.bank;
import java.io.File;
import java.io.FileNotFoundException;
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
	public static final String MSG_NO_CURRENCY = "\nCurrency not configured\n\n";
	public static final String MSG_FILE = "file";
	public static final String MSG_HTTP = "http";
	public static final String MSG_WEBSERVICE = "webservice";
	public static final String MSG_USD = "USD";
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
	public static void main(String[] args) throws Exception {

		new MainBank(System.in,System.out).run();

	}
	
	
	//The core of the program responsible for providing user experience.
	public void run() throws Exception {
		
		Account acc;
		int option = 0;

		if (Bank.loadConfig(MSG_CONFIG_FILE)==false) {
			System.exit(0);
		}
		else {
		Bank.doCurrency(MSG_CONFIG_FILE);
		if (Bank.supportCurrencies()==true) {
		Bank.currencyFileOrWeb(MSG_CONFIG_FILE);
		if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_FILE)) {
		Bank.currencyFileName(MSG_CONFIG_FILE);
		Bank.loadCurrencyFile(Bank.getFileName());
			if (Bank.isCanLoadFile() == false) {
				System.out.println(MSG_CURRENCY_FILE_ERROR);
			}
			else 
				Bank.currencyFileName(MSG_CONFIG_FILE);
			}
		else if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_WEBSERVICE)) {
			Bank.webServiceURL(MSG_CONFIG_FILE);
			Bank.canReadFromWeb(Bank.getWebAddress());
			if (Bank.isCanReadWeb() == false) 
				System.out.println(MSG_WEBSITE_ERROR);
		}
		
		}
		
		}
		
	
	
		UIManager ui = new UIManager(this.in,this.out,menuOptions,MSG_PROMPT);
		try {
			//I also tried to load the currency file and then store into ArrayList so that it only loads once, but I wasn't able to do so.
			//Choices with currency will take 10 seconds to load to next part.
			//I actually tried to load currency file with a test class with one load, and it still slow as well.
			//The slowness is only when using webservice as source of currency file. 
			do {
				option = ui.getMainOption(); //Render main menu
				
				switch (option) {
				case 1:
					
					while (Bank.supportCurrencies()==true) {
						while (Bank.getFileOrWeb().equalsIgnoreCase(MSG_FILE)) {
							
							while (Bank.isCanLoadFile() == true) {
								//Compact statement to accept user input, open account, and print the result including the account number
									String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
									
									while (Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), currency) == false) {
										currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
									}		
									
									while (Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), currency) == true) {
										ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
												ui.readDouble(MSG_ACCOUNT_OD_LIMIT), currency.toUpperCase()).getAccountNumber() });
										break;
									}
									
								break;
								}
							while (Bank.isCanLoadFile() == false) {
								ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
												ui.readDouble(MSG_ACCOUNT_OD_LIMIT), MSG_USD).getAccountNumber() });
								break;
								}
							break;
							}
						while (Bank.getFileOrWeb().equalsIgnoreCase(MSG_WEBSERVICE)) {
							while (Bank.isCanReadWeb() == true) {
								//Compact statement to accept user input, open account, and print the result including the account number
									String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
									
									while (Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), currency) == false) {
										currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
									}		
									
									while (Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), currency) == true) {
										ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
												ui.readDouble(MSG_ACCOUNT_OD_LIMIT), currency.toUpperCase()).getAccountNumber() });
										break;
									}
									
								break;
								}
							while (Bank.isCanReadWeb() == false) {
								ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
												ui.readDouble(MSG_ACCOUNT_OD_LIMIT), MSG_USD).getAccountNumber() });
								break;
								}
							break;
						}
						break;
						} 
					
					
					while (Bank.supportCurrencies()==false) {
						ui.print(MSG_ACCOUNT_OPENED,
								new Object[] { Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
										ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN),
										ui.readDouble(MSG_ACCOUNT_OD_LIMIT), MSG_USD).getAccountNumber() });
						break;
						}
					
					break;
					
				case 2:
					while (Bank.supportCurrencies()==true) {
						if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_FILE)) {
							while (Bank.isCanLoadFile() == true) {
								//Compact statement to accept user input, open account, and print the result including the account number
									String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
									while (Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), currency) == false) {
										currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
									}
									
									while (Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), currency) == true) {
										ui.print(MSG_ACCOUNT_OPENED,
											new Object[] { Bank
													.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
															ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), currency.toUpperCase())
													.getAccountNumber() }); 
										break;
										}
									
									break;
								}
								while (Bank.isCanLoadFile() == false) {
									ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank
												.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
														ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), MSG_USD)
												.getAccountNumber() }); break;}
							
						}
					else if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_WEBSERVICE)) {
							
						while (Bank.isCanReadWeb() == true) {
							//Compact statement to accept user input, open account, and print the result including the account number
								String currency = ui.readToken(MSG_ACCOUNT_CURRENCY);
								while (Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), currency) == false) {
									currency = ui.readToken(MSG_ACCOUNT_CURRENCY).toUpperCase();
								}
								
								while (Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), currency) == true) {
									ui.print(MSG_ACCOUNT_OPENED,
										new Object[] { Bank
												.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
														ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), currency.toUpperCase())
												.getAccountNumber() }); 
									break;
									}
								
								break;
							}
							while (Bank.isCanReadWeb() == false) {
								ui.print(MSG_ACCOUNT_OPENED,
									new Object[] { Bank
											.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
													ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), MSG_USD)
											.getAccountNumber() }); break;}
							
							break;
						
						}
					break;
					}
					
					
					while (Bank.supportCurrencies()==false) {
						ui.print(MSG_ACCOUNT_OPENED,
								new Object[] { Bank
										.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
												ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), MSG_USD)
										.getAccountNumber() });
						break;
					}
					
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
							
					
					if (Bank.supportCurrencies()==true) {
						String sellingCurrency = ui.readToken(MSG_SELLING_CURRENCY);
						double amountSelling = ui.readDouble(MSG_AMOUNT_SELLING);
						String buyingCurrency = ui.readToken(MSG_CURRENCY_BUYING);
			
						if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_FILE)) {
							if (Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), buyingCurrency) == true && Bank.lookUpCurrency(MSG_FILE, Bank.getFileName(), sellingCurrency) == true) {
								
								if (sellingCurrency.toUpperCase().equals(MSG_USD) || buyingCurrency.toUpperCase().equals(MSG_USD)) {
									if(sellingCurrency.toUpperCase().equals(MSG_USD) == true && buyingCurrency.toUpperCase().equals(MSG_USD) == false) {
									ui.print(MSG_EXCHANGE_SUCCESS,
										new Object[] { Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), buyingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), buyingCurrency), amountSelling)});
							}
									else if (sellingCurrency.toUpperCase().equals(MSG_USD) == false && buyingCurrency.toUpperCase().equals(MSG_USD) == true) {
										ui.print(MSG_EXCHANGE_SUCCESS,
												new Object[] { Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), sellingCurrency), amountSelling)});
									}
									else if (sellingCurrency.toUpperCase().equals(MSG_USD) == true && buyingCurrency.toUpperCase().equals(MSG_USD) == true) {
										ui.print(MSG_EXCHANGE_SUCCESS,
												new Object[] { Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_FILE, Bank.getFileName(), sellingCurrency), amountSelling)});
									}
								
								
								}
								else if (sellingCurrency.toUpperCase().equals(MSG_USD) == false && (buyingCurrency.toUpperCase().equals(MSG_USD) == false))  {
									ui.print(MSG_CURRENCY_CONVERT_ERROR,
										new Object[] {});
							}	
						}
							else
								ui.print(MSG_CURRENCY_ERROR,
										new Object[] {});
						}
						else if (Bank.getFileOrWeb().equalsIgnoreCase(MSG_WEBSERVICE)) {
							if (Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), buyingCurrency) == true && Bank.lookUpCurrency(MSG_HTTP, Bank.getWebAddress(), sellingCurrency) == true) {
								
								if (sellingCurrency.toUpperCase().equals(MSG_USD) || buyingCurrency.toUpperCase().equals(MSG_USD)) {
									if(sellingCurrency.toUpperCase().equals(MSG_USD) == true && buyingCurrency.toUpperCase().equals(MSG_USD) == false) {
									ui.print(MSG_EXCHANGE_SUCCESS,
										new Object[] { Bank.findCurrencyRate(MSG_HTTP,Bank.getWebAddress(), buyingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_HTTP, Bank.getWebAddress(), buyingCurrency), amountSelling)});
							}
									else if (sellingCurrency.toUpperCase().equals(MSG_USD) == false && buyingCurrency.toUpperCase().equals(MSG_USD) == true) {
										ui.print(MSG_EXCHANGE_SUCCESS,
												new Object[] { Bank.findCurrencyRate(MSG_HTTP, Bank.getWebAddress(), sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_HTTP, Bank.getWebAddress(), sellingCurrency), amountSelling)});
									}
									else if (sellingCurrency.toUpperCase().equals(MSG_USD) == true && buyingCurrency.toUpperCase().equals(MSG_USD) == true) {
										ui.print(MSG_EXCHANGE_SUCCESS,
												new Object[] { Bank.findCurrencyRate(MSG_HTTP, Bank.getWebAddress(), sellingCurrency), buyingCurrency.toUpperCase(), Bank.convertingCurrency(buyingCurrency, sellingCurrency, Bank.findCurrencyRate(MSG_HTTP, Bank.getWebAddress(), sellingCurrency), amountSelling)});
									}
								
								
								}
								else if (sellingCurrency.toUpperCase().equals(MSG_USD) == false && (buyingCurrency.toUpperCase().equals(MSG_USD) == false))  {
									ui.print(MSG_CURRENCY_CONVERT_ERROR,
										new Object[] {});
							}	
						}
							else
								ui.print(MSG_CURRENCY_ERROR,
										new Object[] {});
						}
						
					}
					else if (Bank.supportCurrencies()==false) {
					
						ui.print(MSG_NO_CURRENCY,
								new Object[] {});
							
							
					}	
					
						
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
		catch (InterruptedException a) {
			a.printStackTrace();
		}
		
	}

	
	private  void handleException(UIManager ui, Exception e) throws IOException{
		ui.print(e.getMessage(), new Object[] { });
	}


}
