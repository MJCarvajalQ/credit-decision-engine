# Credit Decision Engine

## Overview

A full-stack loan decision engine that determines the maximum approvable loan amount for a given applicant. The backend exposes a single REST endpoint built with Spring Boot, which applies a credit scoring algorithm to calculate the highest amount the applicant qualifies for, rather than simply approving or rejecting the requested amount. If no valid amount exists within the requested period, the engine automatically finds the shortest period that makes a loan viable. The frontend is a React + Vite application that lets users submit their details and instantly see the decision.

## How to Run Locally

**Backend** (requires Java 17+):
```bash
cd backend && ./mvnw spring-boot:run
```

**Frontend** (requires Node 18+):
```bash
cd frontend && npm install && npm run dev
```

The backend runs on `http://localhost:8080` and the frontend on `http://localhost:5173`.

## Test Personal Codes

| Personal Code | Status    | Credit Modifier | Behaviour                               |
|---------------|-----------|-----------------|----------------------------------------|
| 49002010965   | Debt      | —               | Always rejected                        |
| 49002010976   | Segment 1 | 100             | Max amount = min(€10,000, 100 × period)|
| 49002010987   | Segment 2 | 300             | Max amount = min(€10,000, 300 × period)|
| 49002010998   | Segment 3 | 1,000           | Max amount = min(€10,000, 1000 × period)|

Any other personal code returns a 400 error.

## Decision Algorithm

The engine always returns the **maximum** approvable amount, regardless of what the applicant requested.

```
max_approvable = min(€10,000, credit_modifier × loan_period)
```

1. Look up the personal code to get the applicant's credit modifier.
2. If the applicant has debt, return a negative decision immediately.
3. Compute `max_approvable` for the requested period.
4. If `max_approvable ≥ €2,000`, return a positive decision with that amount.
5. If `max_approvable < €2,000`, extend the period by one month at a time (up to 60) and repeat.
6. If no period yields a valid amount, return a negative decision.

**Constraints:** loan amount €2,000–€10,000 · period 12–60 months.
