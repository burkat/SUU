import logic.ServiceManager;

public class Janusz {

    public static void main(String[] args) {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.loadExistingAccounts();
        serviceManager.mainLoop();
    }


}
