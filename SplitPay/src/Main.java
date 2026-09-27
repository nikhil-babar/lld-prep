import Application.Balance.BalanceManager;
import Application.Balance.BalanceManagerImpl;
import Application.Balance.SettlementPlanner.BacktrackingSettlementPlanner;
import Application.Expense.ExpenseManager;
import Application.Expense.ExpenseManagerImpl;
import Application.Expense.Split.EqualSplit;
import Models.Expense;
import Models.Group;
import Models.Transaction;
import Models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        User nikhil = User.builder().name("Nikhil").email("abc@gmail.com").build();
        System.out.println(nikhil);

        User rohit = User.builder().name("Rohit").email("xyz@gmail.com").build();
        System.out.println(rohit);

        User karan = User.builder().name("Karan").email("karan@gmail.com").build();
        System.out.println(karan);

        Group group = Group.builder().name("Movies").participants(List.of(nikhil, rohit)).build();
        System.out.println(group);

        Expense tickets = Expense.builder()
                .name("Tickets")
                .group(group)
                .user(rohit)
                .split(
                        EqualSplit.builder()
                                .user(rohit)
                                .group(group)
                                .participants(List.of(nikhil, karan))
                                .amount(1000.0)
                                .build()
                ).build();

        Expense meal = Expense.builder()
                .name("Meal")
                .group(group)
                .user(nikhil)
                .split(
                        EqualSplit.builder()
                                .user(nikhil)
                                .group(group)
                                .participants(List.of(rohit, karan))
                                .amount(1000.0)
                                .build()
                ).build();

        ExpenseManager expenseManager = new ExpenseManagerImpl();
        BalanceManager balanceManager = new BalanceManagerImpl(expenseManager, new BacktrackingSettlementPlanner());

        System.out.println("Nikhil: " + balanceManager.getBalance(nikhil, group));
        System.out.println("Rohit: " + balanceManager.getBalance(rohit, group));
        System.out.println("Karan: " + balanceManager.getBalance(karan, group));

        expenseManager.addExpense(tickets);

        System.out.println("Added Expense: " + tickets);

        System.out.println("Nikhil: " + balanceManager.getBalance(nikhil, group));
        System.out.println("Rohit: " + balanceManager.getBalance(rohit, group));
        System.out.println("Karan: " + balanceManager.getBalance(karan, group));

        expenseManager.addExpense(meal);

        System.out.println("Added Expense: " + meal);

        System.out.println("Nikhil: " + balanceManager.getBalance(nikhil, group));
        System.out.println("Rohit: " + balanceManager.getBalance(rohit, group));
        System.out.println("Karan: " + balanceManager.getBalance(karan, group));


        List<Transaction> transactions = balanceManager.getSettlementTransactions(karan, group);
        System.out.println("Karan Settlements: " + transactions);

        Map<Transaction, Double> settlements = new HashMap<>();
        settlements.put(transactions.get(0), 200.0);
        balanceManager.settleExpenses(karan, group, settlements);

        transactions = balanceManager.getSettlementTransactions(karan, group);
        System.out.println("Karan Settlements: " + transactions);

        System.out.println("Nikhil: " + balanceManager.getBalance(nikhil, group));
        System.out.println("Rohit: " + balanceManager.getBalance(rohit, group));
        System.out.println("Karan: " + balanceManager.getBalance(karan, group));

    }
}
