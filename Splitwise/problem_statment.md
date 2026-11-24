# LLD Problem Statement: Splitwise (Expense Sharing App)

**Time box:** 90 minutes
**Asked at:** Microsoft, Uber, Amazon, Lenskart, Dunzo, PharmEasy (common LLD round)

## Problem Statement

Design the low-level design (LLD) of a system like Splitwise, which allows a group of users to track shared expenses and settle debts. The system should support adding expenses, splitting costs among users using different strategies, tracking pairwise balances, and settling up with the minimum number of transactions.

## Functional Requirements

1. **Domain management** — Users can be added to the system.
2. **Group management** — A group can be created with a set of users.
3. **Add expense** — Any user can add an expense (paid by one user) and split it among a subset of group members using:
    - **Equal split** — divide equally among selected users.
    - **Exact split** — specify exact amounts per user (must sum to total expense amount).
    - **Percentage split** — specify percentage per user (must sum to 100%).
4. **Balance tracking** — The system tracks pairwise balances between users (who owes whom, how much).
5. **View balances** — Show balances for a single user or for an entire group.
6. **Settle up** — Users can record a payment between two users, which updates balances.
7. **Debt simplification** — Given the net balances, minimize the number of settle-up transactions required to clear all debts (i.e., don't just show raw pairwise transaction history — simplify the debt graph).

## Non-Functional / Scoping Constraints (assume unless told otherwise)

- In-memory storage only — no database or persistence layer.
- Single currency — no currency conversion.
- Single payer per expense — no multi-payer/multi-payer-splitting expenses.
- No authentication/authorization concerns.
- Focus on class design, extensibility, and correctness over UI or API layer.

## Points Likely to Be Left Ambiguous (clarify with interviewer before designing)

- Should debts be simplified globally across the group, or just netted pairwise? (Confirm: global simplification is expected as a core requirement, not a stretch goal.)
- Is multi-payer support needed for a single expense? (Default: no — mention as extension if time permits.)
- Does settle-up need to validate against existing balances, or can it be a free-form transaction log entry?
- Should the system support removing/editing an expense after creation?

## What Interviewers Are Evaluating

- Clean OOP class separation (Domain, Group, Expense, Split, Balance/Ledger).
- Use of the **Strategy pattern** for the three split types.
- A sensible data structure for storing and updating pairwise balances without expensive recomputation.
- Validation logic (percentages sum to 100, exact splits sum to total).
- A working algorithm for **debt simplification** (minimizing transactions to settle all balances) — this is the step that differentiates strong candidates.
- Extensibility — ability to add a new split type or feature without modifying existing code (Open/Closed Principle).
- Delivering a working, demonstrable design within the time box, not just diagrams.