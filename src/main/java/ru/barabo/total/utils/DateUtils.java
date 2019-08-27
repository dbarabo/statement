package ru.barabo.total.utils;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

final public class DateUtils {

	final static transient private Logger logger = Logger.getLogger(DateUtils.class.getName());

	public static LocalDate toLocalDate(Date date) {

		if (date == null) {
			return null;
		}

		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime toLocalDateTime(Date date) {

		if (date == null) {
			return null;
		}

		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalTime toLocalTime(Date date) {

		if (date == null) {
			return null;
		}

		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
	}

	public static <E extends Date> E toDate(LocalDate localDate, Class<E> clazz) {
		if (localDate == null) {
			return null;
		}

		try {
			return clazz.getDeclaredConstructor(Long.class).newInstance(
					localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		} catch (InstantiationException e) {
			logger.error("toDate InstantiationException ", e);
		} catch (IllegalAccessException e) {
			logger.error("toDate IllegalAccessException ", e);
		} catch (IllegalArgumentException e) {
			logger.error("toDate IllegalArgumentException ", e);
		} catch (InvocationTargetException e) {
			logger.error("toDate InvocationTargetException ", e);
		} catch (NoSuchMethodException e) {
			logger.error("toDate NoSuchMethodException ", e);
		} catch (SecurityException e) {
			logger.error("toDate SecurityException ", e);
		}

		return null;
	}

	public static Timestamp toTimestamp(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		return new Timestamp(
				localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	public static Timestamp toTimestamp(LocalDate localDate, LocalTime time) {
		if (time == null || localDate == null) {
			return toTimestamp(localDate);
		}

		return new Timestamp(time.atDate(localDate).atZone(
				ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	public static Timestamp toTimestamp(Date date, LocalTime time) {
		return new Timestamp(time.atDate(toLocalDate(date)).atZone(
				ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	public static Timestamp addMinute(Date date, int addMinute) {
		if (date == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(Calendar.MINUTE, addMinute);

		return new Timestamp(calendar.getTime().getTime());
	}

}
