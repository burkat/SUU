package model;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;

public class Account {

    private String accountName;
    private WalletAppKit wallet;

    public Account(String accountName) {
        this.accountName = accountName;
        createOrLoadWallet();
    }

    public WalletAppKit getWallet() {
        return wallet;
    }

    public String getAccountName() {
        return accountName;
    }

    private void createOrLoadWallet() {
        WalletAppKit kit = new WalletAppKit(TestNet3Params.get(), new File("./wallets"), accountName);
        kit.startAsync();
        kit.awaitRunning();
        wallet = kit;
    }
}
