

> Users
> Group
> Expense 
   > Group
   > PaidBy
   > Amount
   > Participants
   > Split
> Transaction
   > PaidBy
   > PaidTo
   > Amount
> AmountTracker
   > Group
   > Source
   > Destination
   > Amount



- Add a Expense
    1. Create a Expense
    2. Get a (Source, Destination, Amount) List
    2. Update the Amount Tracker with split amount

- View Domain Expense
    1. Aggregate Amount Tracker for lend/paid

- Settle Up
    1. Add to transactions
    2. Calculate remaining debt and update tracker.
    3. Cut from the selected expenses

    
Stores

1. Domain
2. Group
3. Expense
4. Transaction

Behaviour

1. ExpenseTracker
2. SplitStrategy



