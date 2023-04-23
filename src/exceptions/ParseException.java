package exceptions;

public class ParseException extends Exception {

	private final String dataItemName;
	private final IssueKind issueKind;
	private final String wrongValue;

	public ParseException(String dataItemName, IssueKind issueKind) {
		this(dataItemName, issueKind, "");
	}

	public ParseException(String dataItemName, IssueKind issueKind, String wrongValue) {
		this.dataItemName = dataItemName != null ? dataItemName : "";
		this.issueKind = issueKind;
		this.wrongValue = wrongValue != null ? wrongValue : "";
	}

	/**
	 * @return Наименование поля данных, к которому относится возникшая
	 *         проблема при синтаксическом анализе строки,
	 *         либо пустая строка (гарантированно не null).
	 */
	public String getDataItemName() {
		return dataItemName;
	}

	/**
	 * @return Род проблемы, возникшей в процессе синтаксического анализа.
	 */
	public IssueKind getIssueKind() {
		return issueKind;
	}

	/**
	 * @return Подстрока с некорректными и/или лишними данными разбираемой
	 *         строки, либо пустая строка (гарантированно не null).
	 */
	public String getWrongValue() {
		return wrongValue;
	}
}
