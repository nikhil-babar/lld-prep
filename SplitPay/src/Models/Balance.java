package Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class Balance
{
    private final double lent;
    private final double owned;

    public static Balance normalize(Balance balance){
        if(balance.lent > balance.owned){
            return new Balance(balance.getLent() - balance.getOwned(), 0);
        }
        return new Balance(0, balance.getOwned() - balance.getLent());
    }
}