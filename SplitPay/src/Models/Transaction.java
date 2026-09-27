package Models;


import lombok.*;

import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@ToString
public class Transaction {
    @Builder.Default
    @EqualsAndHashCode.Include
    private final String id = UUID.randomUUID().toString();

    @NonNull
    private final String from;

    @NonNull
    private final String to;

    @NonNull
    private final String groupId;

    @Builder.Default
    private final String expenseId = "-1";

    @NonNull
    private final Double amount;

    @NonNull
    private final TransactionType transactionType;

    public Balance getFromBalanceDelta(){
        if(transactionType == TransactionType.SETTLED){
            return new Balance(0, -amount);
        }
        if(transactionType == TransactionType.EXPENSE){
            return new Balance(amount, 0);
        }
        return new Balance(0, 0);
    }

    public Balance getToBalanceDelta(){
        if(transactionType == TransactionType.SETTLED){
            return new Balance(-amount, 0);
        }
        if(transactionType == TransactionType.EXPENSE){
            return new Balance(0, amount);
        }
        return new Balance(0, 0);
    }


    public static Transaction changeAmount(Transaction transaction, Double delta){
        return transaction.toBuilder()
                .amount(transaction.getAmount() + delta)
                .build();
    }

    public static Transaction createSettledTransaction(Transaction transaction){
        return transaction.toBuilder()
                .transactionType(TransactionType.SETTLED)
                .build();
    }
}
