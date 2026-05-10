package com.taxgap.engine;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.TaxRule;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface TaxRuleExecutor {
    RuleExecutionResult execute(Transaction transaction, TaxRule rule);
}

class RuleExecutionResult {
    public final boolean violated;
    public final String message;
    
    public RuleExecutionResult(boolean violated, String message) {
        this.violated = violated;
        this.message = message;
    }
}
