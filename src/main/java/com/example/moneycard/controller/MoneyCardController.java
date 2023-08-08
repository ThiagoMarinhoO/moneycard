package com.example.moneycard.controller;

import com.example.moneycard.model.MoneyCard;
import com.example.moneycard.repository.MoneyCardRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/moneycards")
public class MoneyCardController {
    @Autowired
    private MoneyCardRepository moneyCardRepository;

    @GetMapping()
    public ResponseEntity<Iterable<MoneyCard>> findAll(Pageable pageable) {
        Page<MoneyCard> page = moneyCardRepository.findAll(
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))
            )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<MoneyCard> findById(@PathVariable Long requestedId, Principal principal) {
//        Optional<MoneyCard> moneyCardOptional = Optional.ofNullable(moneyCardRepository.findByIdAndOwner(requestedId, principal.getName()));
//        if (moneyCardOptional.isPresent()) {
//            return ResponseEntity.ok(moneyCardOptional.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }

        MoneyCard moneyCard = moneyCardRepository.findByIdAndOwner(requestedId, principal.getName());

        if (moneyCard != null) {
            return ResponseEntity.ok(moneyCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createMoneyCard(@RequestBody MoneyCard newMoneyCardRequest, UriComponentsBuilder ucb) {
        MoneyCard savedMoneyCard = moneyCardRepository.save(newMoneyCardRequest);
        URI locationOfNewMoneyCard = ucb.path("moneycards/{id}").buildAndExpand(savedMoneyCard.getId()).toUri();
        return ResponseEntity.created(locationOfNewMoneyCard).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putMoneyCard(@PathVariable Long requestedId, @RequestBody MoneyCard moneyCardUpdate, Principal principal
    ) {
        MoneyCard moneyCard = moneyCardRepository.findByIdAndOwner(requestedId, principal.getName());

        if (moneyCard != null) {
            MoneyCard updatedMoneyCard = new MoneyCard();
            updatedMoneyCard.setId(moneyCard.getId());
            updatedMoneyCard.setAmount(moneyCardUpdate.getAmount());
            updatedMoneyCard.setOwner(moneyCard.getOwner());

            moneyCardRepository.save(updatedMoneyCard);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteMoneyCard(@PathVariable Long id, Principal principal) {

        if (moneyCardRepository.existsByIdAndOwner(id, principal.getName())) {
            moneyCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
