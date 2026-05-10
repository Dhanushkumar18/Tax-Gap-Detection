INSERT INTO users (username, password, email, role, enabled, created_at) VALUES 
('admin', '$2a$10$slYQmyNdGzin7olVN3p5be0DlH.PKZbv5H8KnzzVgXXbVxzy990O2', 'admin@taxgap.com', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP),
('auditor1', '$2a$10$slYQmyNdGzin7olVN3p5be0DlH.PKZbv5H8KnzzVgXXbVxzy990O2', 'auditor1@taxgap.com', 'ROLE_AUDITOR', true, CURRENT_TIMESTAMP),
('auditor2', '$2a$10$slYQmyNdGzin7olVN3p5be0DlH.PKZbv5H8KnzzVgXXbVxzy990O2', 'auditor2@taxgap.com', 'ROLE_AUDITOR', true, CURRENT_TIMESTAMP);

INSERT INTO tax_rules (rule_name, description, rule_config, enabled, default_severity, created_at, updated_at) VALUES 
('HighValueTransactionRule', 'Flags transactions exceeding a threshold amount', '{"threshold": "100000"}', true, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GSTSlabViolationRule', 'Flags when transaction exceeds slab but tax rate is lower than required', '{"slabThreshold": "50000", "requiredRate": "0.18"}', true, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RefundValidationRule', 'Validates refund amounts against original sales', '{}', true, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
