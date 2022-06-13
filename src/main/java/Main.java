import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.FruitTransaction;
import service.FileService;
import service.OperationHandler;
import service.OperationStrategy;
import service.ParseService;
import service.ShopService;
import service.StorageService;
import service.impl.FileServiceImplementation;
import service.impl.ParseServiceImplementation;
import service.impl.ShopServiceImplementation;
import service.impl.StorageImplementation;
import strategy.AddOperationHandler;
import strategy.OperationStrategyImplementation;
import strategy.SetBalanceOperationHandler;
import strategy.SubtractOperationHandler;

public class Main {
    private static final String FROM_FILE = "src/main/java/resources/fruit_shop.csv";
    private static final String TO_FILE = "src/main/java/resources/fruit_shop_report.csv";

    public static void main(String[] args) {
        FileService fileService = new FileServiceImplementation();
        final List<String[]> listTransactions = fileService.readFile(FROM_FILE);
        ParseService parseService = new ParseServiceImplementation();
        final List<FruitTransaction> transactions = parseService.parse(listTransactions);
        StorageService storageService = new StorageImplementation();
        Map<FruitTransaction.Operation, OperationHandler> mapOperation = new HashMap<>();
        mapOperation.put(FruitTransaction.Operation.BALANCE,
                new SetBalanceOperationHandler(storageService));
        mapOperation.put(FruitTransaction.Operation.PURCHASE,
                new SubtractOperationHandler(storageService));
        mapOperation.put(FruitTransaction.Operation.RETURN,
                new AddOperationHandler(storageService));
        mapOperation.put(FruitTransaction.Operation.SUPPLY,
                new AddOperationHandler(storageService));
        OperationStrategy operationStrategy = new OperationStrategyImplementation(mapOperation);
        ShopService shopService = new ShopServiceImplementation(storageService);
        for (FruitTransaction transaction : transactions) {
            operationStrategy.getOperationHandler(transaction.getOperation())
                    .doTransaction(transaction);
        }
        fileService.writeFile(TO_FILE, shopService.doReport());
    }
}
