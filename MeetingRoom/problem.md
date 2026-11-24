# LLD Problem: Meeting Room Booking System

**Format:** Machine coding / LLD round, 90–120 minutes
**Language:** Java
**Scope:** In-memory only. No DB, no UI, no framework.

---

## Problem

Build an in-memory booking system for an office with multiple meeting rooms.

## Functional Requirements

### 1. Rooms
- Add a room with an id, a name, a capacity, and a floor.
- Rooms can be listed.

### 2. Users
- Register a user with an id and a name.

### 3. Book a meeting
- A user books a room for a time slot `[start, end)` with a title and a list of attendee user ids.
- Booking succeeds only if:
    - The room's capacity is at least the number of attendees, including the organizer.
    - The room has no overlapping booking in that slot. A booking ending at 10:00 and one starting at 10:00 do not overlap.
    - `start < end`, and the slot falls within office hours (09:00–19:00) on a single day.
- On success, return a booking id. On failure, return a clear reason (e.g. `ROOM_UNAVAILABLE`, `CAPACITY_EXCEEDED`, `INVALID_TIME`).

### 4. Auto-assign
- A user can request a room by `(start, end, attendeeCount)` without naming a room.
- The system picks the smallest-capacity room that fits and is free for the slot.
- Ties are broken by lowest room id.

### 5. Cancel
- Only the organizer can cancel a booking.
- Cancelling frees the slot for new bookings.

### 6. Queries
- List all free rooms for a given slot.
- List a room's bookings for a given day, sorted by start time.
- List all upcoming bookings for a user, as organizer or attendee.

### 7. Attendee conflicts
- A user cannot be booked into two overlapping meetings.
- If any attendee, including the organizer, is already busy in that slot, the booking fails with `ATTENDEE_CONFLICT`.

## Non-Functional Requirements

- Thread-safe: two users booking the same room and slot concurrently must result in exactly one success.
- Code should be extensible for new rules and room-selection strategies without rewriting existing code.

## Out of Scope

Recurring meetings, notifications, authentication, time zones, multi-day meetings.

## Deliverables

Working code with a `main` or driver class running at least these scenarios:

1. Successful booking.
2. Overlap rejection.
3. Back-to-back booking (allowed).
4. Capacity failure.
5. Attendee conflict.
6. Cancel, then rebook the same slot.
7. Auto-assign picking the smallest fitting room.
8. Concurrent booking test (e.g. 10 threads, same slot, exactly one winner).

---

## Time Plan

| Phase | Time |
|---|---|
| Read requirements, clarify, sketch entities and flows | 10–15 min |
| Core: rooms, users, booking, overlap check, cancel | 40–50 min |
| Auto-assign, queries, attendee conflicts | 20–25 min |
| Concurrency and driver tests | 15–20 min |
| Buffer and cleanup | 5–10 min |

## Solvability Check

- A clean solution is roughly 6–8 classes and 300–450 lines of Java.
- All 7 requirements fit in 120 minutes.
- In a strict 90-minute round, finish requirements 1–4 and the concurrency requirement fully and working, then do 5–7 as time allows.
- A correct, running core beats a half-built, over-abstracted whole. Check the clock at the 45-minute mark.