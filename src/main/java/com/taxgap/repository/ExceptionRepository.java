package com.taxgap.repository;

import com.taxgap.entity.TaxException;
import com.taxgap.entity.ExceptionSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExceptionRepository extends JpaRepository<TaxException, Long> {
    List<TaxException> findByCustomerId(Long customerId);
    
    List<TaxException> findByCustomerIdAndSeverity(Long customerId, ExceptionSeverity severity);
    
    List<TaxException> findBySeverity(ExceptionSeverity severity);
    
    List<TaxException> findByRuleName(String ruleName);
    
    @Query("SELECT COUNT(e) FROM TaxException e WHERE e.severity = :severity")
    Long countBySeverity(@Param("severity") ExceptionSeverity severity);
    
    @Query("SELECT COUNT(e) FROM TaxException e WHERE e.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
}
