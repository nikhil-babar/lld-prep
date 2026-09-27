package Application.Balance.SettlementPlanner;

import Models.Balance;
import Models.Expense;
import Models.Transaction;

import java.util.List;
import java.util.Map;

public interface SettlementPlanner {
    public List<Transaction> getSettlementTransaction(String groupId, Map<String, Balance> userBalances);
}
