package Application.Expense;

import Application.Expense.Split.Split;
import Core.Observer;
import Models.Expense;
import Models.Group;
import Models.User;

import java.util.List;

public interface ExpenseManager extends ExpenseSubject {
    Expense addExpense(Expense expense);
    List<Expense> getExpenses(User user);
    List<Expense> getExpenses(Group group);
}
