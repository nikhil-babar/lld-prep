package Application.Balance.SettlementPlanner;

import Models.Balance;
import Models.Transaction;
import Models.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BacktrackingSettlementPlanner implements SettlementPlanner {
    @Override
    public List<Transaction> getSettlementTransaction(String groupId, Map<String, Balance> userBalances) {
        var balances = getUserBalances(userBalances);
        return getOptimalTransactions(groupId, balances);
    }

    private List<UserBalance> getUserBalances(Map<String, Balance> userBalances){
        return userBalances.entrySet().stream().map((entry) -> new UserBalance(entry.getKey(), entry.getValue().getLent() - entry.getValue().getOwned())).toList();
    }

    private List<Transaction> getOptimalTransactions(String groupId, List<UserBalance> balances){
        return getOptimalTransactions(
                groupId,
                new ArrayList<>(balances.stream().filter(balance -> balance.amount > 0).toList()),
                new ArrayList<>(balances.stream().filter(balance -> balance.amount < 0).toList()),
                0);
    }

    private List<Transaction> getOptimalTransactions(String groupId, List<UserBalance> positives, List<UserBalance> negatives, int depth){
        var noOfUsers = positives.size() + negatives.size();
        if(noOfUsers == 0) return new ArrayList<>();
        if(depth >= noOfUsers) return null;

        List<Transaction> optimalTransactions = null;
        boolean hasProcessed = false;

        for(int i = 0 ; i < positives.size() ; i++){
            for(int j = 0 ; j < negatives.size() ; j++){
                UserBalance creditor = positives.get(i);
                UserBalance debitor = negatives.get(j);
                double transferAmount = Math.min(creditor.amount, -debitor.amount);

                if(transferAmount == 0) continue;
                hasProcessed = true;

                var transaction = Transaction.builder()
                        .from(debitor.userId)
                        .to(creditor.userId)
                        .transactionType(TransactionType.SETTLEMENT)
                        .amount(transferAmount)
                        .groupId(groupId)
                        .build();

                var updatedCreditor = new UserBalance(creditor.userId, creditor.amount - transferAmount);
                var updatedDebitor = new UserBalance(debitor.userId, debitor.amount + transferAmount);

                positives.set(i, updatedCreditor);
                negatives.set(j, updatedDebitor);

                var optimalNextTransactions = getOptimalTransactions(groupId, positives, negatives, depth+1);
                if(optimalNextTransactions != null){
                    optimalNextTransactions.add(transaction);
                    if(optimalTransactions == null || optimalNextTransactions.size() < optimalTransactions.size()){
                        optimalTransactions = optimalNextTransactions;
                    }
                }

                positives.set(i, creditor);
                negatives.set(j, debitor);
            }
        }

        if(!hasProcessed){
            return new ArrayList<>();
        }

        return optimalTransactions;
    }

    private record UserBalance(String userId, double amount) {}
}
