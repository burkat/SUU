package logic;

import model.Account;

import java.util.*;

public class ServiceManager {

    private Account currentAccount;
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    private final Scanner scanner;

    public ServiceManager() {
        scanner = new Scanner(System.in);
        accountManager = new AccountManager();
        accountManager.loadExistingAccounts();
        transactionManager = new TransactionManager(accountManager);
    }

    public void mainLoop() {
        boolean exit = false;
        while (!exit) {
            //printHelp();
            System.out.println("OK");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 0:
                    exit = true;
                    break;
                case 1:
                    currentAccount = accountManager.createAccount();
                    break;
                case 2:
                    currentAccount = accountManager.login();
                    break;
                case 3:
                    accountManager.printWalletInfo(currentAccount);
                    break;
                case 4:
                    accountManager.printAccounts();
                    break;
                case 5:
                    transactionManager.addNewOfferToDatabase(currentAccount);
                    break;
                case 6:
                    transactionManager.listMatchingOffers();
                    break;
                case 7:
                    transactionManager.listAllOffers();
                    break;
                case 8:
                    transactionManager.deleteOffer(currentAccount);
                    break;
                case 9:
                    transactionManager.acquireOffer(currentAccount);
                    break;
                default:
                    System.out.println("There is no such an option");
            }
        }
    }

    private void printHelp() {
        System.out.println("Choose option and enter appropriate parameters: \n" +
                "0) Exit \n" +
                "1) Create account \n" +
                "2) Login \n" +
                "3) Get wallet info \n" +
                "4) Print available account names \n" +
                "5) Add new offer \n" +
                "6) List offers from given date two weeks ahead \n" +
                "7) List all offers \n" +
                "8) Delete offer \n" +
                "9) Acquire offer (buy) \n");
    }

}
