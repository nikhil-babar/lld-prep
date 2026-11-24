# LLD Problem Statements (90-min round format)

Each problem: core requirements (first ~60 min) + follow-ups an interviewer typically adds (last ~30 min). In-memory only, no UI, no DB. Expose clean APIs and a driver/demo that exercises them.

---

## 1. Parking Lot

**Statement:** Design a multi-level parking lot system that parks vehicles, issues tickets, and charges on exit.

**Requirements**
1. Lot has multiple floors; each floor has multiple spots of different sizes (e.g., motorcycle, compact, large). EV-charging spots are flagged separately.
2. Vehicle types: motorcycle, car, bus/truck. A vehicle may only use a spot that fits it (larger spots may host smaller vehicles; buses may need multiple adjacent or one large spot, as you define).
3. Multiple entry and exit gates operate simultaneously.
4. `park(vehicle)` assigns a spot and returns a ticket (ticket id, vehicle, spot, floor, entry time). Return a clear failure if the lot is full.
5. `unpark(ticket)` frees the spot, computes the fee from duration and vehicle type, and records payment (cash/card/UPI).
6. Pricing is hourly with a different rate per vehicle type; rate card must be changeable without touching parking logic.
7. Query availability per floor per spot type at any time (display board).
8. Lost ticket / invalid ticket / double exit must be handled.

**Constraints:** two gates must never assign the same spot; spot-assignment rule (nearest to entry, lowest floor first, etc.) should be changeable.

**Follow-ups:** reserved/handicap spots, monthly pass holders, peak-hour pricing, spot out-of-service, max stay alert.

---

## 2. Movie Ticket Booking (BookMyShow)

**Statement:** Design a movie ticket booking backend where users browse shows and book seats.

**Requirements**
1. Multiple cities; each city has cinemas; each cinema has multiple screens; each screen has a seat layout with seat categories (Regular, Premium, Recliner) and different prices.
2. Admin can add movies and schedule shows (movie, screen, start time, language).
3. Users can: list movies by city, list shows of a movie in a city, filter by cinema/language/genre/date, and view the seat map of a show with availability.
4. Users select one or more seats and hold them for a limited time (e.g., 5 min). If payment is not completed in time, seats are released automatically.
5. On successful payment, booking is confirmed and a booking id/ticket is returned. On payment failure, seats are released.
6. Users can cancel a booking before show start; seats go back to inventory; refund amount depends on how close to show time the cancellation is.
7. Users can view their booking history.

**Constraints:** a seat in a show can belong to at most one user; many users may try for the same seats simultaneously; a multi-seat request is all-or-nothing.

**Follow-ups:** coupons/offers, food add-ons, per-seat-category dynamic pricing, show time overlap validation on a screen, max seats per booking.

---

## 3. Pub-Sub / Notification System

**Statement:** Design an in-memory publish-subscribe messaging system and use it to build a notification service.

**Requirements**
1. Create and delete topics.
2. Publishers publish messages (id, payload, timestamp, optional attributes) to a topic.
3. Subscribers subscribe to / unsubscribe from one or more topics at runtime.
4. Every active subscriber of a topic receives every message published after it subscribed; messages from one publisher to one topic are delivered in publish order.
5. A slow or failing subscriber must not block other subscribers or publishers.
6. Failed deliveries are retried a configurable number of times, after which the message is parked in a failed-messages store for inspection.
7. Subscribers can optionally register a filter on message attributes so they receive only matching messages.
8. Notification layer on top: users choose channels (email, SMS, push) per topic/event type; one published event fans out to each user's chosen channels with a message template per channel.
9. Provide basic stats: delivered / failed count per subscriber and per topic.

**Constraints:** many concurrent publishers and subscribers; subscribe/unsubscribe during publishing must be safe; adding a new channel type must not require changing existing channel code.

**Follow-ups:** message replay from an offset, delivery priority, rate limit per channel, user do-not-disturb window, deduplication.

---

## 4. Meeting Room Scheduler / Calendar

**Statement:** Design a system to book meeting rooms and manage participants' calendars.

**Requirements**
1. System has N meeting rooms, each with a capacity and optional features (projector, VC). Rooms can be added or removed.
2. User books a meeting by giving: start time, end time, participant list, and optionally required capacity/features. System finds and assigns a free room that fits, or fails with a clear reason.
3. A room cannot have overlapping meetings. Back-to-back meetings (one ends when the next starts) are allowed.
4. Each room has a calendar; query a room's schedule for a day and its free slots.
5. Each user has a personal calendar showing meetings they organise or are invited to.
6. Participants receive notifications on invite, update, and cancellation, and can accept or decline. Track response per participant.
7. Organizer can reschedule, cancel, add or remove participants. Reschedule must keep the existing booking if the new slot is unavailable.
8. Reject booking if a required participant is already busy in that slot (configurable: warn vs. block).
9. Room selection rule when several rooms fit (smallest sufficient capacity, lowest floor, etc.) must be changeable.

**Constraints:** concurrent booking requests for the same room and time must not both succeed.

**Follow-ups:** recurring meetings (daily/weekly), meeting auto-release if nobody checks in, multi-floor/building preference, working-hours restriction, priority bumping.

---

## 5. Vehicle Rental System (Zoomcar-style)

**Statement:** Design a vehicle rental platform where customers rent vehicles from multiple locations for a time period.

**Requirements**
1. Multiple branches/locations; each has its own fleet. Vehicle types: hatchback, sedan, SUV, bike, with attributes (reg number, model, fuel, seats, price per hour/day, odometer).
2. Admin can add/remove vehicles and mark them under maintenance.
3. Customers search for available vehicles by location, type, and a pickup–drop time range, with optional filters (fuel, seats, price).
4. Customer reserves a vehicle for a time range. A vehicle cannot be double-booked for overlapping periods.
5. Reservation lifecycle: create, modify dates, cancel, pickup, return. Cancellation fee depends on time before pickup.
6. Pricing = base rate × duration + optional add-ons (child seat, GPS, extra insurance) + security deposit. Different rate for hourly vs daily vs weekly rental.
7. On return: record odometer/fuel, apply late-return fee and extra-km charges, generate final bill, take payment, refund remaining deposit.
8. Returned vehicle goes through a cleaning/inspection period before it is available again.
9. Customer can view rental history; each vehicle keeps a usage/maintenance log.
10. Customers must have a valid driving licence on file to reserve.

**Constraints:** if two customers request the last available vehicle for overlapping slots, exactly one succeeds.

**Follow-ups:** one-way drop at a different branch, dynamic/surge pricing, loyalty discounts, damage reporting with charges, waitlist when nothing is available.

---

### Suggested 90-min split
- 0–10 min: clarify scope, entities, assumptions
- 10–25 min: class diagram and APIs
- 25–65 min: working code for core flows
- 65–80 min: demo driver / edge cases
- 80–90 min: follow-ups and discussion