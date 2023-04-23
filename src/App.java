import java.util.Scanner;

import entities.UserData;
import exceptions.ParseException;
import utils.ConsoleUtils;
import utils.UncloseableInputStream;

public class App {

    final static String PATH_TO_DATA_DIR = "user_data_files";

    final static Scanner CONSOLE = new Scanner(UncloseableInputStream.wrap(System.in));

    final static String MAIN_PROMPT = "Введите строку со следующими данными"
            + " в произвольном порядке в указанном формате,"
            + " разделенные пробелом:\n"
            + "\tФамилия Имя Отчество \u2014 значения кириллицей или латиницей\n"
            + "\tДата_рождения \u2014 строка формата dd.mm.yyyy\n"
            + "\tНомер_телефона \u2014 целое беззнаковое число без форматирования\n"
            + "\tПол \u2014 символ латиницей f или m\n"
            + "\n>";

    final static Parser<UserData> userDataParser = new UserDataParser();
    static Saver<UserData> userDataSaver;

    public static void main(String[] args) {

        runLifecycle();
    }

    private static void runLifecycle() {

        ConsoleUtils.printEmphasized("\nЗАПРОС ДАННЫХ У ПОЛЬЗОВАТЕЛЯ, ПАРСИНГ И ЗАПИСЬ В ФАЙЛ.");

        boolean repeat = false;
        do {
            System.out.println();

            String input = ConsoleUtils.askString(CONSOLE, repeat ? ">" : MAIN_PROMPT,
                    "Некорректный ввод: требуется непустая строка! Пожалуйста попробуйте снова.");
            repeat = false;

            // 1) Задаём строку и, согласно заданию, в качестве результата
            // получаем код статуса её предварительного анализа на количество
            // введённых данных (полей):
            int status = userDataParser.setInput(input);
            // "нехорошие" коды статуса обрабатываем:
            if (status < 0) {
                ConsoleUtils.printError("Похоже, вы ввели недостаточно данных."
                        + "\nЧисло недостающих полей: " + Integer.toString(-status)
                        + "\nПожалуйста попробуйте снова.");
                repeat = true;
                continue;
            } else if (status > 0) {
                ConsoleUtils.printError("Похоже, вы ввели лишние данные."
                        + "\nЧисло лишних полей: " + Integer.toString(status)
                        + "\nПожалуйста попробуйте снова.");
                repeat = true;
                continue;
            }

            UserData userData;
            try {
                // 2) Разбираем заданную строку и конвертируем в экземпляр
                // класса сущности.
                userData = userDataParser.parse();

            } catch (ParseException e) {

                // Согласно заданию, парсинг строки должен бросать исключение
                // при невозможности разобрать строку, с обработкой исключения
                // с целью проинформировать пользователя о деталях проблемы...
                ConsoleUtils.printError(messageByException(e));
                repeat = true;
                continue;
            }

            System.out.println("Получены данные:");
            ConsoleUtils.printEmphasized(userData.toString());
            System.out.println();

            trySaveUserData(userData);

        } while (repeat || ConsoleUtils.askYesNo(CONSOLE, "\nЖелаете повторить (Д/н)? ", true));

        System.out.println("\nВы завершили работу. Спасибо что воспользовались приложением.");
    }

    private static String messageByException(ParseException e) {

        var issueKind = e.getIssueKind();
        var dataItemName = e.getDataItemName();
        var wrongData = e.getWrongValue();

        String message = switch (issueKind) {
            case WRONG_FORMAT ->
                String.format("Строка содержит некорректное значение поля %s.",
                        dataItemName);

            case MISSING_DATA ->
                String.format("Значение обязательного поля %s отсутствует"
                        + " или задано в неверном формате.",
                        dataItemName);

            case AMBIGUOUS_DATA ->
                String.format("Неоднозначное содержимое строки:"
                        + " обнаружено несколько значений для поля %s.",
                        dataItemName);
            // case UNSPECIFIED -> "";
            default -> "Не удалось разобрать строку: вероятно данные введены некорректно.";
        };

        if (wrongData != null && !wrongData.isEmpty()) {
            message += String.format("\nНекорректные или лишние данные: \"%s\".", wrongData);
        }

        return message
                + "\nПожалуйста попробуйте ввести корректные данные ещё раз.";
    }

    private static void trySaveUserData(UserData userData) {

        var saver = getSaver();
        if (saver == null) {
            ConsoleUtils.printEmphasized("\nСохранение данных в файл отменено.");
            return;
        }

        try {
            var filePath = saver.save(userData);
            ConsoleUtils.printEmphasized(String.format("\nДанные успешно сохранены в файл '%s'.", filePath));

        } catch (Exception e) {
            ConsoleUtils.printError("Произошло исключение при попытке сохранить данные в файл.");
            printExceptionDetails(e);
        }
    }

    private static Saver<UserData> getSaver() {
        if (userDataSaver != null) {
            return userDataSaver;
        }

        try {
            userDataSaver = new UserDataSaver(PATH_TO_DATA_DIR, UserData::toString);
            return userDataSaver;

        } catch (Exception e) {
            ConsoleUtils.printError(
                    String.format("Сохранение файлов в заданной директории '%s' невозможно.", PATH_TO_DATA_DIR));
            printExceptionDetails(e);
            return null;
        }
    }

    private static void printExceptionDetails(Exception e) {
        var cause = e.getCause();
        if (cause == null) {
            cause = e;
        }

        ConsoleUtils.printError("Детали исключения: " + cause.getMessage());
        System.err.println("Трассировка стека в момент исключения:");
        cause.printStackTrace();
    }

}
