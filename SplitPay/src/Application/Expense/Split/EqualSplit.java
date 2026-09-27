package Application.Expense.Split;

import Models.Group;
import Models.Transaction;
import Models.TransactionType;
import Models.User;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

public class EqualSplit implements Split {

    private final List<Transaction> splitTransactions;

    @Builder
    public EqualSplit(@NonNull User user, @NonNull Group group, @NonNull List<User> participants, @NonNull Double amount){
        List<User> finalParticipants = participants.stream().filter(cuser -> !user.getId().equals(cuser.getId())).toList();
        Double finalAmount = amount / (participants.size() + 1);
        splitTransactions = finalParticipants.stream().map(cuser -> {
            return Transaction.builder()
                    .from(user.getId())
                    .to(cuser.getId())
                    .amount(finalAmount)
                    .groupId(group.getId())
                    .transactionType(TransactionType.EXPENSE)
                    .build();
        }).toList();
    }

    @Override
    public List<Transaction> getSplitTransactions() {
        return splitTransactions;
    }
}
