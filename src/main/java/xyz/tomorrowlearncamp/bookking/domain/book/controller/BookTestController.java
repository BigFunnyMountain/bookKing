package xyz.tomorrowlearncamp.bookking.domain.book.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/")
public class BookTestController {

	private static final Logger searchLogger = LoggerFactory.getLogger("SEARCH_LOG");
	private static final Logger buyLogger = LoggerFactory.getLogger("BUY_LOG");
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<String> keywords = List.of("자바", "스프링", "JPA", "엘라스틱", "도커", "쿠버네티스", "파이썬", "AI", "데이터", "웹개발");
	private static final List<String> books = List.of("자바의 정석", "스프링 인 액션", "JPA 프로그래밍", "엘라스틱서치 완벽 가이드", "도커 입문", "쿠버네티스 핸즈온", "파이썬 코딩 도장", "AI 개론", "빅데이터 시대", "실전 웹개발");

	private static final List<String> ageGroups = List.of("10대", "20대", "30대", "40대", "50대");
	private static final List<String> genders = List.of("M", "F");

	private final Random random = new Random();

	@GetMapping("/search/{size}")
	public ResponseEntity<Void> search(@PathVariable int size) {
		for (int i = 0; i < size; i++) {
			Map<String, Object> log = new HashMap<>();
			log.put("log_type", "search");
			log.put("age_group", randomFrom(ageGroups));
			log.put("keyword", randomFrom(keywords));
			log.put("gender", randomFrom(genders));
			log.put("timestamp", Instant.now().toString());
			try {
				searchLogger.info(objectMapper.writeValueAsString(log));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping("/buy/{size}")
	public ResponseEntity<Void> buy(@PathVariable int size) {
		for (int i = 0; i < size; i++) {
			Map<String, Object> log = new HashMap<>();
			log.put("log_type", "buy");
			log.put("price", random.nextInt(70)*1000);
			log.put("age_group", randomFrom(ageGroups));
			log.put("book_name", randomFrom(books));
			log.put("gender", randomFrom(genders));
			int day = random.nextInt(31) + 1;
			String date = String.format("2025-03-%02d", day);
			log.put("timestamp", date);
			try {
				buyLogger.info(objectMapper.writeValueAsString(log));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.ok().build();
	}

	private String randomFrom(List<String> list) {
		return list.get(random.nextInt(list.size()));
	}
}
