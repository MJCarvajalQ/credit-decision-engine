package com.decisionengine.service;

import com.decisionengine.model.LoanDecision;
import com.decisionengine.registry.CreditProfile;
import com.decisionengine.registry.CreditRegistry;
import org.springframework.stereotype.Service;

@Service
public class DecisionEngineService {

    private static final int MIN_AMOUNT = 2000;
    private static final int MAX_AMOUNT = 10000;
    private static final int MAX_PERIOD = 60;

    private final CreditRegistry creditRegistry;

    public DecisionEngineService(CreditRegistry creditRegistry) {
        this.creditRegistry = creditRegistry;
    }

    public LoanDecision calculateDecision(String personalCode, int loanPeriod) {
        CreditProfile profile = creditRegistry.getProfile(personalCode);

        if (profile.hasDebt()) {
            return LoanDecision.negative("Applicant has existing debt");
        }

        return findBestLoan(profile.creditModifier(), loanPeriod);
    }

    private LoanDecision findBestLoan(int creditModifier, int startPeriod) {
        for (int period = startPeriod; period <= MAX_PERIOD; period++) {
            int maxAmount = Math.min(MAX_AMOUNT, creditModifier * period);
            if (maxAmount >= MIN_AMOUNT) {
                return LoanDecision.positive(maxAmount, period);
            }
        }
        return LoanDecision.negative("No valid loan amount found for any period");
    }
}
