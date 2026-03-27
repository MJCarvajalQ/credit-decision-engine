package com.decisionengine.model;

public record LoanDecision(
        DecisionType decision,
        Integer approvedAmount,
        Integer approvedPeriod,
        String message
) {

    public static LoanDecision positive(int amount, int period) {
        return new LoanDecision(DecisionType.POSITIVE, amount, period, "Loan approved");
    }

    public static LoanDecision negative(String message) {
        return new LoanDecision(DecisionType.NEGATIVE, null, null, message);
    }
}
