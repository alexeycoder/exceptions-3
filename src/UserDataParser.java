
// import java.beans.IntrospectionException;
// import java.beans.Introspector;
// import java.beans.PropertyDescriptor;
// import java.util.Arrays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.Gender;
import entities.UserData;
import exceptions.IssueKind;
import exceptions.ParseException;

public class UserDataParser implements Parser<UserData> {

	private static final String LAST_NAME = "Фамилия";
	private static final String FIRST_NAME = "Имя";
	private static final String MIDDLE_NAME = "Отчество";
	private static final String BIRTH_DATE = "Дата_рождения";
	private static final String PHONE_NUMBER = "Номер_телефона";
	private static final String GENDER = "Пол";
	private static final int REQUIRED_DATA_ITEMS = 6;

	// static {
	// int gettersCount;
	// try {
	// var pds = Introspector.getBeanInfo(UserData.class,
	// Object.class).getPropertyDescriptors();
	// gettersCount = Arrays.stream(pds).map(PropertyDescriptor::getReadMethod)
	// .filter(m -> m != null).mapToInt(m -> 1).sum();
	// } catch (IntrospectionException e) {
	// gettersCount = 0;
	// }
	// REQUIRED_DATA_ITEMS = gettersCount;
	// }

	private String inputStr;

	public int setInput(String input) {
		if (input == null) {
			// не должно произойти при должном использовании метода клиентской частью
			throw new NullPointerException();
		}

		int itemsCountDiff = input.split("\\s+").length - REQUIRED_DATA_ITEMS;
		if (itemsCountDiff == 0) {
			this.inputStr = input;
		}
		this.inputStr = input;
		return itemsCountDiff;
	}

	public UserData parse() throws ParseException {
		StringBuilder sbInput = new StringBuilder(inputStr);

		// Регулярные выражения составлены так, чтобы работать правильно вне
		// зависимости от порядка проверки и экстракции подстрок (полей данных).
		// Тем не менее, фактический порядок выбран от более конкретных к более
		// общим форматам.

		var genders = extractAll(sbInput, "\\b(f|m|F|M)\\b");
		var dates = extractAll(sbInput, "\\b(\\d{2}\\.\\d{2}\\.\\d{4})\\b");
		var phoneNumbers = extractAll(sbInput, "\\b(\\d+)\\b");
		var lastFirstMiddleNames = extractAll(sbInput, "((?!\\b[fmFM]{1}\\b)\\b[\\w&&\\D]+\\b)");

		String wrongData = sbInput.toString().trim();

		if (lastFirstMiddleNames.isEmpty()) {
			throw new ParseException(LAST_NAME, IssueKind.MISSING_DATA, wrongData);
		} else if (lastFirstMiddleNames.size() == 1) {
			throw new ParseException(FIRST_NAME, IssueKind.MISSING_DATA, wrongData);
		} else if (lastFirstMiddleNames.size() == 2) {
			throw new ParseException(MIDDLE_NAME, IssueKind.MISSING_DATA, wrongData);
		} else if (lastFirstMiddleNames.size() != 3) {
			throw new ParseException(MIDDLE_NAME, IssueKind.AMBIGUOUS_DATA, wrongData);
		}

		if (genders.isEmpty()) {
			throw new ParseException(GENDER, IssueKind.MISSING_DATA, wrongData);
		} else if (genders.size() != 1) {
			throw new ParseException(GENDER, IssueKind.AMBIGUOUS_DATA, wrongData);
		}

		if (dates.isEmpty()) {
			throw new ParseException(BIRTH_DATE, IssueKind.MISSING_DATA, wrongData);
		} else if (dates.size() != 1) {
			throw new ParseException(BIRTH_DATE, IssueKind.AMBIGUOUS_DATA, wrongData);
		}

		if (phoneNumbers.isEmpty()) {
			throw new ParseException(PHONE_NUMBER, IssueKind.MISSING_DATA, wrongData);
		} else if (phoneNumbers.size() != 1) {
			throw new ParseException(PHONE_NUMBER, IssueKind.AMBIGUOUS_DATA, wrongData);
		}

		var userData = new UserData();
		userData.setGender(Gender.of(genders.get(0)));
		userData.setBirthDate(parseDate(dates.get(0)));
		userData.setPhoneNumber(phoneNumbers.get(0));
		userData.setFirstName(lastFirstMiddleNames.get(1));
		userData.setMiddleName(lastFirstMiddleNames.get(2));
		userData.setLastName(lastFirstMiddleNames.get(0));
		return userData;
	}

	private static List<String> extractAll(StringBuilder from, String regex) {
		var result = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(from.toString());

		int removedLength = 0;
		while (matcher.find()) {
			result.add(matcher.group());
			int start = matcher.start();
			int end = matcher.end();
			int len = end - start;
			start -= removedLength;
			end -= removedLength;
			from.delete(start, end);
			removedLength += len;
		}

		return result;
	}

	// private static int parsePhoneNumber(String str) throws ParseException {
	// try {
	// return Integer.parseInt(str);
	// } catch (NumberFormatException e) {
	// throw new ParseException(PHONE_NUMBER, IssueKind.WRONG_FORMAT, str);
	// }
	// }

	private static LocalDate parseDate(String str) throws ParseException {
		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
		try {
			return LocalDate.parse(str, dateFormatter);
		} catch (DateTimeParseException e) {
			throw new ParseException(BIRTH_DATE, IssueKind.WRONG_FORMAT, str);
		}
	}
}
