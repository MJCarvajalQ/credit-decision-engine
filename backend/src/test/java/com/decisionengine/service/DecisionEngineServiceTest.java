package com.decisionengine.service;

import com.decisionengine.exception.InvalidPersonalCodeException;
import com.decisionengine.model.DecisionType;
import com.decisionengine.model.LoanDecision;
import com.decisionengine.registry.CreditProfile;
import com.decisionengine.registry.CreditRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionEngineServiceTest {

    @Mock
    private CreditRegistry creditRegistry;

    @InjectMocks
    private DecisionEngineService decisionEngineService;

    @ParameterizedTest(name = "modifier={0}, startPeriod={1} → amount={2}, period={3}")
    @MethodSource("approvedLoanScenarios")
    void givenEligibleApplicant_whenCalculateDecision_thenReturnMaxApprovedAmount(
            int modifier, int startPeriod, int expectedAmount, int expectedPeriod) {

        when(creditRegistry.getProfile("code")).thenReturn(CreditProfile.withModifier(modifier));

        LoanDecision decision = decisionEngineService.calculateDecision("code", startPeriod);

        assertThat(decision.decision()).isEqualTo(DecisionType.POSITIVE);
        assertThat(decision.approvedAmount()).isEqualTo(expectedAmount);
        assertThat(decision.approvedPeriod()).isEqualTo(expectedPeriod);
    }

    static Stream<Arguments> approvedLoanScenarios() {
        return Stream.of(
                Arguments.of(100, 24, 2400, 24),   // segment 1 — no cap
                Arguments.of(300, 24, 7200, 24),   // segment 2 — no cap
                Arguments.of(1000, 24, 10000, 24), // segment 3 — capped at 10000
                Arguments.of(100, 12, 2000, 20)    // period extended: 100*12=1200 < 2000, extends to 20
        );
    }

    @Test
    void givenApplicantWithDebt_whenCalculateDecision_thenReturnNegative() {
        when(creditRegistry.getProfile("code")).thenReturn(CreditProfile.withDebt());

        LoanDecision decision = decisionEngineService.calculateDecision("code", 24);

        assertThat(decision.decision()).isEqualTo(DecisionType.NEGATIVE);
        assertThat(decision.approvedAmount()).isNull();
        assertThat(decision.approvedPeriod()).isNull();
    }

    @Test
    void givenNoViableLoanInAnyPeriod_whenCalculateDecision_thenReturnNegative() {
        // modifier 1 → even at max period 60: 1 * 60 = 60 < 2000
        when(creditRegistry.getProfile("code")).thenReturn(CreditProfile.withModifier(1));

        LoanDecision decision = decisionEngineService.calculateDecision("code", 12);

        assertThat(decision.decision()).isEqualTo(DecisionType.NEGATIVE);
        assertThat(decision.approvedAmount()).isNull();
        assertThat(decision.approvedPeriod()).isNull();
    }

    @Test
    void givenUnknownPersonalCode_whenCalculateDecision_thenThrowException() {
        when(creditRegistry.getProfile("00000000000")).thenThrow(new InvalidPersonalCodeException("00000000000"));

        assertThatThrownBy(() -> decisionEngineService.calculateDecision("00000000000", 24))
                .isInstanceOf(InvalidPersonalCodeException.class)
                .hasMessageContaining("00000000000");
    }
}
