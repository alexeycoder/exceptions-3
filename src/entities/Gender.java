package entities;

public enum Gender {
	MALE("m"),
	FEMALE("f");

	private final String strValue;

	Gender(String strValue) {
		this.strValue = strValue;
	}

	@Override
	public String toString() {
		return strValue;
	}

	public static Gender of(String str) {
		str = str.toLowerCase();
		if (str.startsWith(MALE.strValue)) {
			return MALE;
		} else if (str.startsWith(FEMALE.strValue)) {
			return FEMALE;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
