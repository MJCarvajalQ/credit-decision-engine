package com.decisionengine.controller;

import com.decisionengine.model.LoanDecision;
import com.decisionengine.model.LoanRequest;
import com.decisionengine.service.DecisionEngineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private final DecisionEngineService decisionEngineService;

    public LoanController(DecisionEngineService decisionEngineService) {
        this.decisionEngineService = decisionEngineService;
    }

    @PostMapping("/decision")
    public ResponseEntity<LoanDecision> decide(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.ok(
                decisionEngineService.calculateDecision(request.personalCode(), request.loanPeriod())
        );
    }
}
