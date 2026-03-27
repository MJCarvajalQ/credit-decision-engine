package com.decisionengine.registry;

public record CreditProfile(boolean hasDebt, Integer creditModifier) {

    public static CreditProfile withDebt() {
        return new CreditProfile(true, null);
    }

    public static CreditProfile withModifier(int modifier) {
        return new CreditProfile(false, modifier);
    }
}
