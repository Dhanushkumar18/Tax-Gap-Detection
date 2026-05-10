package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxRule;
import com.taxgap.entity.TransactionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class GSTSlabViolationRuleExecutor implements TaxRuleExecutor {
    
    private final ObjectMapper objectMapper;
    
    public GSTSlabViolationRuleExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public RuleExecutionResult execute(Transaction transaction, TaxRule rule) {
        try {
            JsonNode config = objectMapper.readTree(rule.getRuleConfig());
            BigDecimal slabThreshold = new BigDecimal(config.get("slabThreshold").asText());
            BigDecimal requiredRate = new BigDecimal(config.get("requiredRate").asText());
            
            if (transaction.getAmount().compareTo(slabThreshold) > 0 &&
                transaction.getTaxRate().compareTo(requiredRate) < 0) {
                return new RuleExecutionResult(true,
                    "Amount " + transaction.getAmount() + " exceeds slab threshold " + 
                    slabThreshold + " but tax rate " + transaction.getTaxRate() + 
                    " is lower than required " + requiredRate);
            }
            return new RuleExecutionResult(false, "GST slab compliant");
        } catch (Exception e) {
            throw new RuntimeException("Error executing GST slab violation rule: " + e.getMessage());
        }
    }
}
