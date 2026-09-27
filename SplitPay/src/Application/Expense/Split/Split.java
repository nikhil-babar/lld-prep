package Application.Expense.Split;

import Models.Transaction;

import java.util.List;

public interface Split {
    List<Transaction> getSplitTransactions();
}
