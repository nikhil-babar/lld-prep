package Application.Expense;

import Application.Expense.Split.Split;
import Core.Observer;
import Models.Expense;
import Models.Group;
import Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExpenseManagerImpl implements ExpenseManager {

    private final Map<String, List<String>> expensesByGroup = new HashMap<>();
    private final Map<String, List<String>> expensesByUser = new HashMap<>();
    private final Map<String, Expense> expenseMap = new HashMap<>();
    private final List<Observer<Expense>> observers = new ArrayList<>();

    @Override
    public Expense addExpense(Expense expense) {
        expenseMap.put(expense.getId(), expense);
        expensesByGroup.compute(expense.getGroupId(), (k,v) -> {
            if(v == null) return new ArrayList<>(List.of(expense.getId()));
            v.add(expense.getId());
            return v;
        });
        expensesByUser.compute(expense.getUserId(), (k,v) -> {
            if(v == null) return new ArrayList<>(List.of(expense.getId()));
            v.add(expense.getId());
            return v;
        });
        observers.forEach(observer -> observer.onAdd(expense));
        return expense;
    }

    @Override
    public List<Expense> getExpenses(User user) {
        return expensesByUser.getOrDefault(user.getId(), new ArrayList<>()).stream().map(expenseMap::get).collect(Collectors.toList());
    }

    @Override
    public List<Expense> getExpenses(Group group) {
        return expensesByGroup.getOrDefault(group.getId(), new ArrayList<>()).stream().map(expenseMap::get).collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<Expense> observer) {
        observers.add(observer);
    }

}
