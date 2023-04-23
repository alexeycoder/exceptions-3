package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserData implements Serializable{
	
	private String firstName;
	private String middleName;
	private String lastName;
	private LocalDate birthDate;
	private String phoneNumber; // может начинаться с 00...
	private Gender gender;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		final String leftBrace = "<";
		final String rightBrace = ">";
		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
		StringBuilder sb = new StringBuilder()
				.append(leftBrace).append(lastName).append(rightBrace)
				.append(leftBrace).append(firstName).append(rightBrace)
				.append(leftBrace).append(middleName).append(rightBrace)
				.append(leftBrace).append(birthDate.format(dateFormatter)).append(rightBrace)
				.append(leftBrace).append(phoneNumber).append(rightBrace)
				.append(leftBrace).append(gender.toString()).append(rightBrace);
		return sb.toString();
	}

}
