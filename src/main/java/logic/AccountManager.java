package logic;

import model.Account;
import org.bitcoinj.kits.WalletAppKit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class AccountManager {

    private final Scanner scanner;
    private final Map<String, Account> accounts;

    AccountManager() {
        scanner = new Scanner(System.in);
        accounts = new HashMap<>();
    }


    void loadExistingAccounts() {
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

    Account createAccount() {
        System.out.println("Enter account name");
        String accountName = scanner.nextLine();
        if (accounts.containsKey(accountName)) {
            System.out.println("There is already an account with that name");
            return null;
        }
        Account account = new Account(accountName);
        accounts.put(accountName, account);
        return account;
    }

    Account login() {
        System.out.println("Enter account name to login");
        String accountName = scanner.nextLine();
        Account account = accounts.get(accountName);
        if (account == null) {
            System.out.println("There is no such an account");
        }
        return account;
    }

    void printWalletInfo(Account currentAccount) {
        if (currentAccount == null) {
            System.out.println("You must log in before checking info");
            return;
        }
        WalletAppKit wallet = currentAccount.getWallet();
        System.out.println("Account name: " + currentAccount.getAccountName());
        System.out.println("Current balance: " + wallet.wallet().getBalance().toFriendlyString());
        System.out.println("Wallet address: " + wallet.wallet().currentReceiveAddress().toString());
    }

    void printAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("There are no accounts");
        } else {
            for (String name : accounts.keySet()) {
                System.out.println(name);
            }
        }
    }

    Account getAccountForName(String accountName) {
        return accounts.get(accountName);
    }

}
