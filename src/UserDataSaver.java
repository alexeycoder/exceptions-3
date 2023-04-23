import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Function;

import entities.UserData;

public class UserDataSaver implements Saver<UserData> {

	private final Path dataDir;
	private final Function<UserData, String> getAsString;

	/**
	 * Объект, предоставляющий сервис сохранения пользовательских данных
	 * в заданной директории.
	 * Данные пользователей сохраняются в файлы, соответствующие фамилии
	 * пользователя.
	 * 
	 * @param pathToDataDir Путь к директории данных.
	 * @param getAsString   Функция приведения экземпляра пользовательских данных
	 *                      к строковому представлению.
	 * @throws Exception если указанный путь невозможно использовать в качестве
	 *                   пути к директории для сохранения файлов.
	 */
	public UserDataSaver(String pathToDataDir, Function<UserData, String> getAsString) throws Exception {
		try {
			this.dataDir = prepareDir(pathToDataDir);
			this.getAsString = getAsString != null ? getAsString : UserData::toString;
		} catch (RuntimeException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Сохраняет экземпляр данных в текстовый файл с именем соответствующие фамилии
	 * пользователя и используя предоставленную функцию преобразования к строковому
	 * представлению.
	 * 
	 * @param data Экземпляр данных.
	 * @throws Exception если попытка сохранения не удалась в результате какой-либо
	 *                   ошибки ввода-вывода или любого иного исключения времени
	 *                   выполнения.
	 */
	public String save(UserData userData) throws Exception {
		try {
			String lastName = userData.getLastName();
			Path pathToFile = findFileByNameCaseInsensitive(lastName, dataDir);
			if (pathToFile == null) {
				pathToFile = dataDir.resolve(lastName);
			}

			var file = pathToFile.toFile();

			try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, true)) {
				if (file.length() > 0) {
					fileWriter.write(System.lineSeparator());
				}
				fileWriter.write(getAsString.apply(userData));
			}

			return pathToFile.toAbsolutePath().toString();

		} catch (RuntimeException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Нечувствительный к регистру поиск по указанному пути существующего файла
	 * или директории по имени.
	 * 
	 * @param fileName Имя файла.
	 * @param baseDir  Путь к директории для поиска.
	 * @return Объект, представляющий системно-зависимый путь к файлу,
	 *         либо null, если файла или директории с таким именем не найдено.
	 * @throws IOException
	 * @throws SecurityException
	 */
	private static Path findFileByNameCaseInsensitive(final String fileName, Path baseDir) throws IOException {
		assert fileName != null && !fileName.isBlank();

		try (var resultAsStream = Files.find(baseDir, 1,
				(path, attr) -> path.getFileName().toString().equalsIgnoreCase(fileName))) {
			return resultAsStream.findAny().orElse(null);
		}
	}

	/**
	 * Подготовка директории: при необходимости создаёт директорию, если таковой
	 * ещё не существует по указанному пути.
	 * 
	 * @param pathToDir Путь к директории.
	 * @return Объект, представляющий системно-зависимый путь к директории,
	 *         либо null, если не удалось создать директорию со всеми необходимыми
	 *         родительскими директориями.
	 * @throws IllegalArgumentException      если по заданному пути уже существует
	 *                                       файл.
	 * @throws InvalidPathException
	 * @throws UnsupportedOperationException
	 * @throws SecurityException
	 * @throws RuntimeException              если не удалось создать директорию по
	 *                                       указанному пути по неизвестной причине.
	 */
	private static Path prepareDir(String pathToDir) {
		var path = Path.of(pathToDir);
		var file = path.toFile();

		if (file.exists()) {
			if (file.isDirectory()) {
				return path;
			} else {
				throw new IllegalArgumentException(
						String.format("По заданному пути '%s' уже существует файл.", pathToDir));
			}
		}

		if (file.mkdirs()) {
			return path;
		} else {
			throw new RuntimeException(
					String.format("По заданному пути '%s' не удалось создать директорию.", pathToDir));
		}
	}
}
