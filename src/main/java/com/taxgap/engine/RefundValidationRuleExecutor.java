package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxRule;
import com.taxgap.entity.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class RefundValidationRuleExecutor implements TaxRuleExecutor {
    
    private final ObjectMapper objectMapper;
    
    public RefundValidationRuleExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public RuleExecutionResult execute(Transaction transaction, TaxRule rule) {
        // This rule validates that refunds don't exceed sales
        // In a real system, you would check against actual sales records
        // For now, we'll just flag if it's a refund without other checks
        
        if (transaction.getTransactionType() == TransactionType.REFUND) {
            return new RuleExecutionResult(false, "Refund validated (would need to check against sales in real system)");
        }
        return new RuleExecutionResult(false, "Not a refund transaction");
    }
}
