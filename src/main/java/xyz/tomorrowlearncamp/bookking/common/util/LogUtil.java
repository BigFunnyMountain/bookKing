package xyz.tomorrowlearncamp.bookking.common.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import xyz.tomorrowlearncamp.bookking.common.enums.LogType;


public class LogUtil {
	private static final Logger searchLogger = LoggerFactory.getLogger("SEARCH_LOG");
	private static final Logger purchaseLogger = LoggerFactory.getLogger("BUY_LOG");
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void log(LogType logType, Map<String, Object> data) {
		try {
			String jsonLog = objectMapper.writeValueAsString(data);
			switch (logType) {
				case SEARCH -> searchLogger.info(jsonLog);
				case PURCHASE -> purchaseLogger.info(jsonLog);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public static String getAgeGroup(int age) {
		if (age < 10) return "10대 미만";
		if (age < 20) return "10대";
		if (age < 30) return "20대";
		if (age < 40) return "30대";
		if (age < 50) return "40대";
		if (age < 60) return "50대";
		return "60대 이상";
	}
}
