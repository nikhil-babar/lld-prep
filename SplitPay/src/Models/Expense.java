package Models;

import Application.Expense.Split.Split;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Expense {
    @EqualsAndHashCode.Include
    private final String id;

    private final String name;
    private final String groupId;
    private final String userId;
    private final List<Transaction> expenseTransactions;

    @Builder
    public Expense(@NonNull  String name, @NonNull  Group group, @NonNull  User user, @NonNull  Split split){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.groupId = group.getId();
        this.userId = user.getId();
        this.expenseTransactions = split.getSplitTransactions().stream().map(txn -> txn.toBuilder().expenseId(this.id).build()).toList();
    }
}
