package logic;

import com.google.common.collect.ImmutableList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import model.Account;
import org.bitcoinj.core.*;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class TransactionManager {

    private final Scanner scanner;

    private final DatabaseManager database;
    private final DBCollection offersCollection;
    private final AccountManager accountManager;

    TransactionManager(AccountManager accountManager) {
        scanner = new Scanner(System.in);
        database = new DatabaseManager();
        //database.clear();
        this.accountManager = accountManager;
        offersCollection = database.createCollection("offers");
    }

    void addNewOfferToDatabase(Account currentAccount) {
        if (currentAccount == null) {
            System.out.println("You must log in before adding offers");
            return;
        }
        System.out.println("Enter available from [dd/MM/yyyy]");
        Date availableFrom = DateUtils.enterDate();
        System.out.println("Enter available to [dd/MM/yyyy]");
        Date availableTo = DateUtils.enterDate();
        System.out.println("Enter price");
        String price = scanner.nextLine();
        String id = String.valueOf(10000 + ThreadLocalRandom.current().nextInt(10000));
        Map<String, Object> queryArgs = new HashMap<>();
        queryArgs.put("id", id);
        queryArgs.put("accountName", currentAccount.getAccountName());
        queryArgs.put("availableFrom", availableFrom);
        queryArgs.put("availableTo", availableTo);
        queryArgs.put("price", price);
        database.insert(offersCollection, queryArgs);
    }

    void listMatchingOffers() {
        System.out.println("Enter available from [dd/MM/yyyy]");
        Date availableFrom = DateUtils.enterDate();
        Map<String, Object> queryArgs = new HashMap<>();
        for (int i = 0; i < 14; i++) {
            queryArgs.put("availableFrom", availableFrom);
            database.listMatchingDocuments(offersCollection, queryArgs);
            availableFrom = DateUtils.plusOneDay(availableFrom);
        }
    }

    void listAllOffers() {
        database.listAllDocuments(offersCollection);
    }

    void deleteOffer(Account currentAccount) {
        if (currentAccount == null) {
            System.out.println("You must log in before removing offers");
            return;
        }
        System.out.println("Enter offer id");
        String id = scanner.nextLine();
        Map<String, Object> queryArgs = new HashMap<>();
        queryArgs.put("id", id);
        queryArgs.put("accountName", currentAccount.getAccountName());
        database.delete(offersCollection, queryArgs);
    }

    void acquireOffer(Account currentAccount) {
        if (currentAccount == null) {
            System.out.println("You must log in before acquiring offers");
            return;
        }
        System.out.println("Enter offer id");
        String id = scanner.nextLine();

        Map<String, Object> queryArgs = new HashMap<>();
        queryArgs.put("id", id);
        DBObject offer = database.getDocument(offersCollection, queryArgs);
        if (offer == null) {
            System.out.println("There is no such an offer");
            return;
        }
        String targetAccountName = offer.get("accountName").toString();
        if (targetAccountName.equals(currentAccount.getAccountName())) {
            System.out.println("You can't acquire your own offer");
            return;
        }
        String price = offer.get("price").toString();
        Account targetAccount = accountManager.getAccountForName(targetAccountName);
        if (transferBTC(currentAccount, targetAccount, price)) {
            database.delete(offersCollection, queryArgs);
        }
    }

    private boolean transferBTC(Account from, Account to, String amount) {
        boolean success = false;
        Coin value = Coin.parseCoin(amount);
        Address targetAddress = to.getWallet().wallet().currentReceiveAddress();
        System.out.println("Sending money to: " + targetAddress.toString());

        try {
            Wallet.SendResult result = from.getWallet().wallet().sendCoins(from.getWallet().peerGroup(), targetAddress, value);
            System.out.println("Coins sent. Transaction hash: " + result.tx.getHashAsString());
            success = true;
        } catch (InsufficientMoneyException e) {
            assert e.missing != null;
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
        }
        return success;
    }

    private boolean makeContract(Account from, Account to, String amount) {

        boolean success = false;
        ECKey client = new ECKey();
        ECKey seller = new ECKey();

        from.getWallet().wallet().importKey(client);
        to.getWallet().wallet().importKey(seller);

        Transaction contract = new Transaction(TestNet3Params.get());
        List<ECKey> keys = ImmutableList.of(client, seller);

        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);

        Coin value = Coin.parseCoin(amount);
        contract.addOutput(value, script);

        SendRequest req = SendRequest.forTx(contract);
        try {
            from.getWallet().wallet().completeTx(req);
            System.out.println("Contract has been made");
            success = true;
        } catch (InsufficientMoneyException e) {
            assert e.missing != null;
            System.out.println("Not enough coins in your wallet. Missing " + e.missing.getValue() + " satoshis are missing (including fees)");
        }
        from.getWallet().peerGroup().broadcastTransaction(req.tx);
        return success;
    }

}
