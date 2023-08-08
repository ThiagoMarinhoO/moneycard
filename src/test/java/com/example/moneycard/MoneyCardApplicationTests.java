package com.example.moneycard;

import com.example.moneycard.model.MoneyCard;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyCardApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void myFirstTest() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("wrongUser", "abc123").getForEntity("/moneycards", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("th", "abc123")
				.getForEntity("/moneycards/8", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void shouldReturnAPageOfMoneyCards() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("th", "abc123")
				.getForEntity("/moneycards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	public void shouldReturnASortedPageOfMoneyCards() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("th", "abc123")
				.getForEntity("/moneycards?page=0&size=1&sort=amount,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
		double amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(321.00);
	}

	@Test
	void shouldUpdateAnExistingMoneyCard() {
		MoneyCard moneyCardUpdate = new MoneyCard();
		moneyCardUpdate.setId(null);
		moneyCardUpdate.setAmount(123.65);
		moneyCardUpdate.setOwner(null);

		HttpEntity<MoneyCard> request = new HttpEntity<>(moneyCardUpdate);

		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("th", "abc123")
				.exchange("/moneycards/7", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("th", "abc123")
				.getForEntity("/moneycards/7", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");
		assertThat(id).isEqualTo(7);
		assertThat(amount).isEqualTo(123.65);
	}

	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		MoneyCard unknownCard = new MoneyCard();
		unknownCard.setId(null);
		unknownCard.setAmount(19.99);
		unknownCard.setOwner(null);

		HttpEntity<MoneyCard> request = new HttpEntity<>(unknownCard);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("th", "abc123")
				.exchange("/moneycards/99999", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingMoneyCard() {
		ResponseEntity<Void> response = restTemplate.withBasicAuth("lucas", "asd123").exchange("/moneycards/8", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("lucas", "asd123")
				.getForEntity("/moneycards/8", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteACashCardThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate.withBasicAuth("th", "abc123").exchange("/moneycards/9798", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
		ResponseEntity<Void> deleteResponse = restTemplate.withBasicAuth("th", "abc123").exchange("/moneycards/8", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		ResponseEntity<String> getResponse = restTemplate.withBasicAuth("lucas", "asd123").getForEntity("/moneycards/8", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
