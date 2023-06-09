## Задание №3. Продвинутая работа с исключениями в Java

Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
Фамилия Имя Отчество дата_рождения номер_телефона пол.

Форматы данных:

* фамилия, имя, отчество &mdash; строки;
* дата_рождения &mdash; строка формата dd.mm.yyyy;
* номер_телефона &mdash; целое беззнаковое число без форматирования;
* пол &mdash; символ латиницей f или m.

Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым, вернуть код ошибки, обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.

Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры. Если форматы данных не совпадают, нужно бросить исключение, соответствующее типу проблемы. Можно использовать встроенные типы java и создать свои. Исключение должно быть корректно обработано, пользователю выведено сообщение с информацией, что именно неверно.

Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну строку должны записаться полученные данные, вида

	<Фамилия><Имя><Отчество><датарождения><номертелефона><пол>

Однофамильцы должны записаться в один и тот же файл, в отдельные строки.

Не забудьте закрыть соединение с файлом.

При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано, пользователь должен увидеть стектрейс ошибки.

## Реализация приложения

Точка запуска &mdash; метод `main()` в [App.java](src/App.java)\
Версия целевой Java runtime &mdash; 19

Для соответствия требованиям API синтаксического разборщика [Parser.java](src/Parser.java) определяет два метода:

* `int setInput(String input)` &mdash; возвращающий ***код результата*** предварительного анализа строки:
	* 0 &mdash; если количество данных (полей) в строке соответствует необходимому;
	* отрицательное значение &mdash; соответствует количеству недостающих полей в строке;
	* положительное значение &mdash; соответствует количеству лишних полей в строке.
* `T parse() throws ParseException` &mdash; метод непосредственно разбора строки, для которого декларируется, что метод ***выбрасывает проверяемое исключение*** `ParseException` в случаях, если формат строки задан некорректно и преобразование невозможно.

Определённый тип проверяемого исключения [ParseException](src/exceptions/ParseException.java) позволяет, если необходимо, указывать как наименование поля данных, относящееся к возникшей проблеме, так и род самой проблемы и подстроку с проблемными данными, что используется в последующей обработке исключения в "клиентской" части и соответствующем оповещении пользователя о деталях ошибки.

Реализация парсера &mdash; [UserDataParser.java](src/UserDataParser.java)

Реализация сервиса сохранения данных в файлы &mdash; [UserDataSaver.java](src/UserDataSaver.java) (API [Saver.java](src/Saver.java)) &mdash; декларирует, что как создание объекта сервиса, так и метод сохранения выбрасывают проверяемое исключение `Exception` в случаях ошибок работы с файловой системой и любых иных исключениях времени выполнения (которые оборачиваются как исходная причина в экземпляр `Exception`).\
Такой подход явно требует от пользователя `UserDataSaver` обрабатывать возможные указанные возможные исключения и даёт возможность получить детали и трассировку стека исходного исключения.

### Пример работы:

![example-1](https://user-images.githubusercontent.com/109767480/233836566-7312893e-c678-4e05-bcf4-4b98a39e0f68.png)

### Пример вывода трассировки исключения, возникшего при попытке сохранения данных:

![example-2](https://user-images.githubusercontent.com/109767480/233836568-a9fa7b41-4e61-4f12-983a-b0cf8829cc8a.png)
