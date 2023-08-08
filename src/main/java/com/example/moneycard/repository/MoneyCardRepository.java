package com.example.moneycard.repository;

import com.example.moneycard.model.MoneyCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoneyCardRepository extends CrudRepository<MoneyCard, Long>, PagingAndSortingRepository<MoneyCard, Long> {
    MoneyCard findByIdAndOwner(Long id, String owner);
    Page<MoneyCard> findByOwner(String owner, PageRequest amount);

    boolean existsByIdAndOwner(Long id, String owner);
}
