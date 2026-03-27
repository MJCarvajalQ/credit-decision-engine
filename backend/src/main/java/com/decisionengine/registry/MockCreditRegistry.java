package com.decisionengine.registry;

import com.decisionengine.exception.InvalidPersonalCodeException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MockCreditRegistry implements CreditRegistry {

    private static final Map<String, CreditProfile> REGISTRY = Map.of(
            "49002010965", CreditProfile.withDebt(),
            "49002010976", CreditProfile.withModifier(100),
            "49002010987", CreditProfile.withModifier(300),
            "49002010998", CreditProfile.withModifier(1000)
    );

    @Override
    public CreditProfile getProfile(String personalCode) {
        CreditProfile profile = REGISTRY.get(personalCode);
        if (profile == null) {
            throw new InvalidPersonalCodeException(personalCode);
        }
        return profile;
    }
}
