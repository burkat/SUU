package logic;

import com.mongodb.DBCollection;
import model.Account;
import org.bitcoinj.kits.WalletAppKit;

import java.io.File;
import java.util.*;

public class ServiceManager {

    private Scanner scanner = new Scanner(System.in);
    private Map<String, Account> accounts = new HashMap<>();
    private Account currentAccount;
    private final Database database = new Database();
    private final DBCollection offersCollection = database.createCollection("offers");

    public ServiceManager() {
        database.clear();
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
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    printWalletInfo();
                    break;
                case 4:
                    printAccounts();
                    break;
                case 5:
                    addNewOfferToDatabase();
                    break;
                case 6:
                    listMatchingOffers();
                    break;
                case 7:
                    database.listAllDocuments(offersCollection);
                    break;
                case 8:
                    deleteOffer();
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

    public void loadExistingAccounts() {
        File folder = new File("./wallets");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return;
        }
        for (File file : listOfFiles) {
            if (file.getName().contains("wallet")) {
                String accountName = file.getName().split("\\.")[0];
                Account account = new Account(accountName);
                accounts.put(accountName, account);
            }
        }
    }

    private void createAccount() {
        System.out.println("Enter account name");
        String accountName = scanner.nextLine();
        if (accounts.containsKey(accountName)) {
            System.out.println("There is already an account with that name");
            return;
        }
        Account account = new Account(accountName);
        accounts.put(accountName, account);
        currentAccount = account;
    }

    private void login() {
        System.out.println("Enter account name to login");
        String accountName = scanner.nextLine();
        Account account = accounts.get(accountName);
        if (account == null) {
            System.out.println("There is no such an account");
        } else {
            currentAccount = account;
        }
    }

    private void printWalletInfo() {
        if (currentAccount == null) {
            System.out.println("You must log in before checking info");
            return;
        }
        WalletAppKit wallet = currentAccount.getWallet();
        System.out.println("Current balance: " + wallet.wallet().getBalance());
        System.out.println("Wallet address: " + wallet.wallet().currentReceiveAddress().toString());
    }

    private void printAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("There are no accounts");
        } else {
            for (String name : accounts.keySet()) {
                System.out.println(name);
            }
        }
    }

    private void addNewOfferToDatabase() {
        if (currentAccount == null) {
            System.out.println("You must log in before adding offers");
            return;
        }
        System.out.println("Enter available from [dd/MM/yyyy]");
        Date availableFrom = Utils.enterDate();
        System.out.println("Enter available to [dd/MM/yyyy]");
        Date availableTo = Utils.enterDate();
        System.out.println("Exnter price per day");
        long pricePerDay = scanner.nextLong();
        Map<String, Object> fields = new HashMap<>();
        fields.put("accountName", currentAccount.getAccountName());
        fields.put("availableFrom", availableFrom);
        fields.put("availableTo", availableTo);
        fields.put("pricePerDay", pricePerDay);
        database.insert(offersCollection, fields);
    }

    private void listMatchingOffers() {
        System.out.println("Enter available from [dd/MM/yyyy]");
        Date availableFrom = Utils.enterDate();
        Map<String, Object> fields = new HashMap<>();
        for (int i = 0; i < 14; i++) {
            fields.put("availableFrom", availableFrom);
            database.listMatchingDocuments(offersCollection, fields);
            availableFrom = Utils.plusOneDay(availableFrom);
        }
    }

    private void deleteOffer() {
        if (currentAccount == null) {
            System.out.println("You must log in before removing offers");
            return;
        }
        System.out.println("Enter available from date [dd/MM/yyyy]");
        Date availableFrom = Utils.enterDate();
        Map<String, Object> fields = new HashMap<>();
        fields.put("availableFrom", availableFrom);
        fields.put("accountName", currentAccount.getAccountName());
        database.delete(offersCollection, fields);
    }

}
