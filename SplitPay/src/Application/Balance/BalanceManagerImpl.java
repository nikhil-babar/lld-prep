package Application.Balance;

import Application.Balance.SettlementPlanner.SettlementPlanner;
import Application.Expense.ExpenseManager;
import Core.Observer;
import Models.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BalanceManagerImpl implements BalanceManager {

    private final SettlementPlanner settlementPlanner;

    private record GroupUser(String userId, String groupId) {}

    private final Map<String, Map<String, Balance>> groupToUserToBalanceMap = new HashMap<>();
    private final Map<String, Map<String, List<Transaction>>> groupToUserToSettlementTransactions = new HashMap<>();

    private final Map<String, Balance> userToBalances = new HashMap<>();
    private final Map<String, List<Transaction>> groupToTransactions = new HashMap<>();


    public BalanceManagerImpl(ExpenseManager expenseManager, SettlementPlanner settlementPlanner){
        this.settlementPlanner = settlementPlanner;
        expenseManager.addObserver(new ExpenseObserver());
    }

    @Override
    public List<Transaction> getSettlementTransactions(User user, Group group) {
        if(groupToUserToSettlementTransactions.containsKey(group.getId())) return groupToUserToSettlementTransactions.get(group.getId()).getOrDefault(user.getId(), new ArrayList<>());
        return calculateSettlementPlan(group.getId());
    }

    private List<Transaction> calculateSettlementPlan(String groupId) {
        var userBalances = groupToUserToBalanceMap.get(groupId);
        var settlementTransactions = settlementPlanner.getSettlementTransaction(groupId, userBalances);
        var userSettlements = settlementTransactions.stream().collect(Collectors.groupingBy(Transaction::getFrom));
        groupToUserToSettlementTransactions.put(groupId, userSettlements);
        return settlementTransactions;
    }

    @Override
    public void settleExpenses(User user, Group group, Map<Transaction, Double> settlements) {
        updateSettlementTransactions(user, group, settlements);
        var settledTransactions = getSettledTransaction(settlements);
        settledTransactions.forEach(txn -> updateBalance(txn, false));
        groupToTransactions.get(group.getId()).addAll(settledTransactions);
    }

    private List<Transaction> getSettledTransaction(Map<Transaction, Double> settlements){
        var settledTransactions = new ArrayList<Transaction>();
        settlements.forEach((txn, amount) -> {
            var ledgerTxn = txn.toBuilder().transactionType(TransactionType.SETTLED).amount(amount).build();
            settledTransactions.add(ledgerTxn);
        });
        return settledTransactions;
    }

    private void updateSettlementTransactions(User user, Group group, Map<Transaction, Double> settlements) {
        var existingSettlements = groupToUserToSettlementTransactions.get(group.getId());
        var updatedSettlements = new ArrayList<Transaction>();
        existingSettlements.values().stream().flatMap(Collection::stream).forEach(txn-> {
            if(settlements.containsKey(txn)){
                var amount = settlements.get(txn);
                if(txn.getAmount().equals(amount)){
                    return;
                }
                var updatedTxn = txn.toBuilder().amount(txn.getAmount()-amount).build();
                updatedSettlements.add(updatedTxn);
                return;
            }
            updatedSettlements.add(txn);
        });
        groupToUserToSettlementTransactions.put(group.getId(), updatedSettlements.stream().collect(Collectors.groupingBy(Transaction::getFrom)));
    }

    @Override
    public Balance getBalance(User user) {
        return Balance.normalize(userToBalances.getOrDefault(user.getId(), new Balance(0, 0)));
    }

    @Override
    public Balance getBalance(User user, Group group) {
        var groupUsersBalances = groupToUserToBalanceMap.get(group.getId());
        var defaultBalance = new Balance(0, 0);
        if(groupUsersBalances == null){
            return defaultBalance;
        }
        return Balance.normalize(groupUsersBalances.getOrDefault(user.getId(), defaultBalance));
    }

    private class ExpenseObserver implements Observer<Expense> {
        @Override
        public void onAdd(Expense item) {
            handleExpense(item);
        }

        @Override
        public void onUpdate(Expense item) {
            handleExpense(item);
        }
    }

    private void handleExpense(Expense item){
        updateBalance(item);
        var transactions = item.getExpenseTransactions();
        groupToTransactions.compute(item.getGroupId(), (groupId, txns) -> {
            if(txns == null) return transactions;
            return Stream.concat(transactions.stream(), txns.stream()).collect(Collectors.toList());
        });
        if(groupToUserToSettlementTransactions.containsKey(item.getGroupId())) calculateSettlementPlan(item.getGroupId());
    }

    private void updateBalance(Expense item){
        if(groupToTransactions.containsKey(item.getGroupId())){
            var oldTransactions = groupToTransactions.get(item.getGroupId()).stream().filter(txn -> txn.getExpenseId().equals(item.getId()));
            oldTransactions.forEach(txn -> {
                updateBalance(txn, true);
            });
        }
        var transactions = item.getExpenseTransactions();
        transactions.forEach(txn -> {
            updateBalance(txn, false);
        });
    }

    private void updateBalance(Transaction txn, boolean isRevert){
        var group = txn.getGroupId();
        var from = txn.getFrom();
        var to = txn.getTo();

        BiFunction<Balance, Balance, Balance> getUpdatedBalance = (Balance balance, Balance delta) -> {
            var sign = isRevert ? -1 : 1;
            if(balance == null){
                return delta;
            }
            return new Balance(balance.getLent() + sign * delta.getLent(), balance.getOwned() + sign * delta.getOwned());
        };

        if(!groupToUserToBalanceMap.containsKey(group)){
            groupToUserToBalanceMap.put(group, new HashMap<>());
        }

        userToBalances.compute(from, (key, balance) -> {
            var delta = txn.getFromBalanceDelta();
            return getUpdatedBalance.apply(balance, delta);
        });
        userToBalances.compute(to, (key, balance) -> {
            var delta = txn.getToBalanceDelta();
            return getUpdatedBalance.apply(balance, delta);
        });

        groupToUserToBalanceMap.get(group).compute(from, (key, balance) -> {
            var delta = txn.getFromBalanceDelta();
            return getUpdatedBalance.apply(balance, delta);
        });
        groupToUserToBalanceMap.get(group).compute(to, (key, balance) -> {
            var delta = txn.getToBalanceDelta();
            return getUpdatedBalance.apply(balance, delta);
        });
    }
}
