package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxRule;
import com.taxgap.repository.TaxRuleRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleEngineService {
    
    private final TaxRuleRepository ruleRepository;
    private final Map<String, TaxRuleExecutor> executorRegistry;
    
    public RuleEngineService(TaxRuleRepository ruleRepository,
                           HighValueTransactionRuleExecutor highValueExecutor,
                           GSTSlabViolationRuleExecutor gstExecutor,
                           RefundValidationRuleExecutor refundExecutor) {
        this.ruleRepository = ruleRepository;
        this.executorRegistry = new HashMap<>();
        
        // Register executors
        executorRegistry.put("HighValueTransactionRule", highValueExecutor);
        executorRegistry.put("GSTSlabViolationRule", gstExecutor);
        executorRegistry.put("RefundValidationRule", refundExecutor);
    }
    
    public List<RuleViolation> executeAllActiveRules(Transaction transaction) {
        List<RuleViolation> violations = new java.util.ArrayList<>();
        
        List<TaxRule> activeRules = ruleRepository.findByEnabled(true);
        
        for (TaxRule rule : activeRules) {
            try {
                TaxRuleExecutor executor = executorRegistry.get(rule.getRuleName());
                if (executor != null) {
                    RuleExecutionResult result = executor.execute(transaction, rule);
                    if (result.violated) {
                        violations.add(new RuleViolation(rule.getRuleName(), result.message, rule.getDefaultSeverity()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error executing rule " + rule.getRuleName() + ": " + e.getMessage());
            }
        }
        
        return violations;
    }
    
    public static class RuleViolation {
        public final String ruleName;
        public final String message;
        public final com.taxgap.entity.ExceptionSeverity severity;
        
        public RuleViolation(String ruleName, String message, com.taxgap.entity.ExceptionSeverity severity) {
            this.ruleName = ruleName;
            this.message = message;
            this.severity = severity;
        }
    }
}
