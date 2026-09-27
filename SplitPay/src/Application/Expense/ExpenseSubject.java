package Application.Expense;

import Core.Observer;
import Models.Expense;

public interface ExpenseSubject {
    void addObserver(Observer<Expense> observer);
}
