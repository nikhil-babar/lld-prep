package Application.Balance;

import Models.Balance;
import Models.Group;
import Models.Transaction;
import Models.User;

import java.util.List;
import java.util.Map;

public interface BalanceManager {
    List<Transaction> getSettlementTransactions(User user, Group group);
    void settleExpenses(User user, Group group, Map<Transaction, Double> settlements) throws Exception;

    Balance getBalance(User user);

    Balance getBalance(User user, Group group);
}
