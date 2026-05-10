package com.taxgap.repository;

import com.taxgap.entity.Transaction;
import com.taxgap.entity.ComplianceStatus;
import com.taxgap.entity.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByCustomerId(Long customerId);
    
    List<Transaction> findByCustomerIdAndValidationStatus(Long customerId, ValidationStatus status);
    
    List<Transaction> findByCustomerIdAndComplianceStatus(Long customerId, ComplianceStatus status);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.customerId = :customerId AND t.complianceStatus != 'COMPLIANT'")
    Long countNonCompliantByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    List<Transaction> findByValidationStatus(ValidationStatus status);
}
