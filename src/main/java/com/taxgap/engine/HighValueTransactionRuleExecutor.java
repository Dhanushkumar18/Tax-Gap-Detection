package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxRule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class HighValueTransactionRuleExecutor implements TaxRuleExecutor {
    
    private final ObjectMapper objectMapper;
    
    public HighValueTransactionRuleExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public RuleExecutionResult execute(Transaction transaction, TaxRule rule) {
        try {
            JsonNode config = objectMapper.readTree(rule.getRuleConfig());
            BigDecimal threshold = new BigDecimal(config.get("threshold").asText());
            
            if (transaction.getAmount().compareTo(threshold) > 0) {
                return new RuleExecutionResult(true, 
                    "Transaction amount " + transaction.getAmount() + 
                    " exceeds threshold " + threshold);
            }
            return new RuleExecutionResult(false, "Transaction amount is within threshold");
        } catch (Exception e) {
            throw new RuntimeException("Error executing high value transaction rule: " + e.getMessage());
        }
    }
}
