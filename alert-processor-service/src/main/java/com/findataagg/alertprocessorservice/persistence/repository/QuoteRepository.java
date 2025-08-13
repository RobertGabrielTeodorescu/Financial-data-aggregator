package com.findataagg.alertprocessorservice.persistence.repository;

import com.findataagg.alertprocessorservice.persistence.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
}
