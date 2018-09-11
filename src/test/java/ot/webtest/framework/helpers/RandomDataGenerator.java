package ot.webtest.framework.helpers;

import io.qameta.allure.Step;
import ot.webtest.dataobject.SpecialDateTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ot.webtest.framework.helpers.AllureHelper.logPassed;

public class RandomDataGenerator {

    // TODO: refactor methods to be NON-static: to shorten in code
    // from 'RandomDataGenerator.getApplicationNumber()'
    // to 'rnd.getApplicationNumber()'

    /** Генерация случайного номера для заявления: строка из чисел, длина от 5 до 14 символов
     * @author Gerasimenko I.S.
     * @return
     */
    @Step("Генерируем случайный номер для заявления: строка из чисел, длина от 5 до 14 символов")
    public static String getApplicationNumber() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        // TODO: need to know constraints on this field

        int length = rdg.nextInt(10) + 5;
        String applicationNumber = rdg.getNumbers(length);
        logPassed("Сгенерирован номер '" + applicationNumber + "'");
        return applicationNumber;
    }

    /** Генерация случайного номера для заявления: строка из чисел, длина length
     * @author Gerasimenko I.S.
     * @return
     */
    public String getNumbers(int length) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        String stringOfNumbers = "";
        for (int i = 0; i < length; i++) {
            stringOfNumbers += rdg.getFigure();
        }
        return stringOfNumbers;
    }

    /** Генерация случайных серии и номера для Исполнительного документа: /!\ ФОРМАТ УТОЧНЯЕТСЯ
     * @author Gerasimenko I.S.
     * @return
     */
    @Step("Генерируем случайные серию и номер для Исполнительного документа: /!\\ ФОРМАТ УТОЧНЯЕТСЯ")
    public static String getSeriaNumberOfExecutionDocument() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        // TODO: need to know constraints on this field
        String seriaAndNumber = "серия ";
        Random random = new Random();
        int lengthOfSeria = 2 + random.nextInt(5);
        for (int i = 0; i < lengthOfSeria; i++) {
            seriaAndNumber += rdg.getFigure();
        }
        seriaAndNumber += " № ";
        int lengthOfNumber = 4 + random.nextInt(5);
        for (int i = 0; i < lengthOfNumber; i++) {
            seriaAndNumber += rdg.getFigure();
        }
        logPassed("Сгенерирован серия и номер '" + seriaAndNumber + "'");
        return seriaAndNumber;
    }

    /** Возвращает случайный номер исполнительного производства в формате n..n/yy/dd/rr или n..n/yy/ddddd-ИП
     * @author Gerasimenko I.S.
     * @return
     */
    public static String getNumberOfEnforcementProceeding(){
        RandomDataGenerator rdg = new RandomDataGenerator();
        String enforcementProceedingNumber = "";
        Random random = new Random();
        int lengthOfN_N = 4 + random.nextInt(10);
        for (int i = 0; i < lengthOfN_N; i++) {
            enforcementProceedingNumber += rdg.getFigure();
        }
        enforcementProceedingNumber += ("/" + rdg.getFigure() + rdg.getFigure() + "/" + rdg.getFigure() + rdg.getFigure());
        // already have: n..n/yy/dd/rr

        if (rdg.nextInt(10) > 4) {
            // n..n/yy/ddddd-ИП
            enforcementProceedingNumber += (rdg.getFigure() + rdg.getFigure() + rdg.getFigure() + "-ИП");
        }
        return enforcementProceedingNumber;
    }

    /** Случайная последовательность кириллических символов 'Ххх Ххх Ххх' (длина каждого блока от 2 до 20)
     * @return
     */
    public String getFIO() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        String fio = "";
        int lastnameLength = 2 + rdg.nextInt(19);
        int firstLength = 2 + rdg.nextInt(19);
        int middleLength = 2 + rdg.nextInt(19);

        fio += rdg.getCyrillicWordWithLeadingUpperCase(lastnameLength);
        fio += " " + rdg.getCyrillicWordWithLeadingUpperCase(firstLength);
        fio += " " + rdg.getCyrillicWordWithLeadingUpperCase(middleLength);

        return fio;
    }


    /** Случайная последовательность кириллических символов вида 'Хххххххххх'
     * @param length - длина последовательности
     * @return
     */
    public String getCyrillicWordWithLeadingUpperCase(int length) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        String wordWithLeadingUpperCaseLetter = String.valueOf(rdg.getCyrillicUpperCaseChar());
        for (int i = 1; i < length; i++) {
            wordWithLeadingUpperCaseLetter += rdg.getCyrillicLowerCaseChar();
        }
        return wordWithLeadingUpperCaseLetter;
    }



    public char getCyrillicUpperCaseChar() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        int nextChar = 1040 + rdg.nextInt(33);
        if (nextChar == 1072) {
            nextChar = 1025; // Ё
        }
        return (char)nextChar;
    }

    public char getCyrillicLowerCaseChar() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        int nextChar = 1072 + rdg.nextInt(33);
        if (nextChar == 1104) {
            nextChar = 1105; // Ё
        }
        return (char)nextChar;
    }

    public String getFigure() {
        Random random = new Random();
        return String.valueOf(random.nextInt(10));
    }

    /** Returns random number in range [0; upperBound-1]
     * @param upperBound
     * @return
     */
    public int nextInt(int upperBound) {
        Random random = new Random();
        return random.nextInt(upperBound);
    }
    /** Returns random number in range [0; upperBound)
     * @param upperBound
     * @param mantissaLength
     * @return
     */
    public Double nextDouble(int upperBound, int mantissaLength) {
        if (mantissaLength <= 0) {
            mantissaLength = 1;
        }
        if (upperBound <= 0) {
            upperBound = 1;
        }
        Integer integerPart = nextInt(upperBound);
        Integer mantissaPart = nextInt((int)Math.pow(10, mantissaLength));
        return integerPart*1.0 + ((mantissaPart*1.0)/Math.pow(10.0,mantissaLength*1.0));
    }

    /** Возвращает не нулевое значение (E.g. 0.0001 и более)
     * @param upperBound
     * @param mantissaLength
     * @return
     */
    public String getNumberWithDecimalPart(int upperBound, int mantissaLength) {
        if (mantissaLength <= 0) {
            mantissaLength = 1;
        }
        if (upperBound <= 0) {
            upperBound = 1;
        }
        String integerPart = Integer.toString(nextInt(upperBound));
        String mantissaPart = Integer.toString(1 + nextInt((int)Math.pow(10, mantissaLength) - 1));
        return integerPart + "." + mantissaPart;
    }

    public String getCyrillicAndNumber(int length) {
        String result = "";
        RandomDataGenerator rdg = new RandomDataGenerator();
        for (int i = 0; i < length; i++) {
            int nextInt = rdg.nextInt(3);
            if (nextInt == 0) {
                result += rdg.getCyrillicUpperCaseChar();
            } else if (nextInt == 1) {
                result += rdg.getCyrillicLowerCaseChar();
            } else if (nextInt == 2) {
                result += rdg.getFigure();
            }
        }
        return result;
    }

    public String getCyrillic(int length) {
        String result = "";
        RandomDataGenerator rdg = new RandomDataGenerator();
        for (int i = 0; i < length; i++) {
            int nextInt = rdg.nextInt(2);
            if (nextInt == 0) {
                result += rdg.getCyrillicUpperCaseChar();
            } else if (nextInt == 1) {
                result += rdg.getCyrillicLowerCaseChar();
            }
        }
        return result;
    }

    public String getLatinAndNumber(int length) {
        String result = "";
        RandomDataGenerator rdg = new RandomDataGenerator();
        for (int i = 0; i < length; i++) {
            int nextInt = rdg.nextInt(3);
            if (nextInt == 0) {
                result += rdg.getLatinUpperCaseChar();
            } else if (nextInt == 1) {
                result += rdg.getLatinLowerCaseChar();
            } else if (nextInt == 2) {
                result += rdg.getFigure();
            }
        }
        return result;
    }

    public String getLatin(int length) {
        String result = "";
        RandomDataGenerator rdg = new RandomDataGenerator();
        for (int i = 0; i < length; i++) {
            int nextInt = rdg.nextInt(2);
            if (nextInt == 0) {
                result += rdg.getLatinUpperCaseChar();
            } else if (nextInt == 1) {
                result += rdg.getLatinLowerCaseChar();
            }
        }
        return result;
    }

    public char getLatinUpperCaseChar() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        int nextChar = 65 + rdg.nextInt(25);
        return (char)nextChar;
    }

    public char getLatinLowerCaseChar() {
        RandomDataGenerator rdg = new RandomDataGenerator();
        int nextChar = 97 + rdg.nextInt(25);
        return (char)nextChar;
    }

    public String getCyrillicWords(int minWordLength, int maxWordLength, int wordsCount) {
        String randomString = getCyrillic(minWordLength + nextInt(maxWordLength - minWordLength + 1));
        for (int i = 1; i < wordsCount; i++) {
            randomString += " " + getCyrillic(minWordLength + nextInt(maxWordLength - minWordLength + 1));
        }
        return randomString;
    }

    /** Рандомная дата (изменяется день) в пределах диапазона (включительно)
     * @param startDate
     * @param endDate
     * @return
     */
    public LocalDate getDate(LocalDate startDate, LocalDate endDate) {
        int startDayOfYear = startDate.getDayOfYear();
        int startYear = startDate.getYear();
        int endDayOfYear = endDate.getDayOfYear();
        int endYear = endDate.getYear();

        int dayDiff = (endYear - startYear) * 365 + (endDayOfYear - startDayOfYear);
        int randomDayShift = nextInt(dayDiff + 1);
        LocalDate randomDate;
        int attempts = 0;
        do {
            randomDate = startDate.plusDays(randomDayShift);
            if (endDate.plusDays(1).isAfter(randomDate)) {
                break;
            }
            attempts++;
        } while (attempts < 10);
        if (attempts == 10) {
            return endDate;
        }
        return randomDate;
    }

    public SpecialDateTime getSpecialDateTime(LocalDate startDate, LocalDate endDate) {
        LocalDate date = getDate(startDate, endDate);
        SpecialDateTime specialDateTime =
                new SpecialDateTime(date)
                        .withHour(nextInt(24))
                        .withMinute(nextInt(60));
        return specialDateTime;
    }

    public boolean getBooleanNoNull() {
        return (this.nextInt(2) == 0 ? false : true);
    }
    public Boolean getBoolean() {
        Boolean value = null;
        switch (this.nextInt(3)) {
            case 0:
                value = null; break;
            case 1:
                value = true; break;
            case 2:
                value = false; break;
        }
        return value;
    }

    /** Random sorting of a List
     * @param listToSort
     * @param <T>
     */
    public <T> void sortList(List<T> listToSort) {
        List<T> copyOfListToSort = new ArrayList<>(listToSort);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < listToSort.size(); i++) {
            numbers.add(i);
        }
        for (int i = 0; i < numbers.size(); i++) {
            int randomPosition = nextInt(numbers.size());
            int buf = numbers.get(i);
            numbers.add(i, numbers.get(randomPosition));
            numbers.remove(i+1);
            numbers.add(randomPosition, buf);
            numbers.remove(randomPosition+1);
        }
        for (int i = 0; i < copyOfListToSort.size(); i++) {
            listToSort.remove(0);
            listToSort.add(copyOfListToSort.get(numbers.get(i)));
        }
    }

    public List<Integer> getUniqueIndexes(int numberOfIndexesToReturn, int maxIndex) {
        List<Integer> fullList = new ArrayList<>();
        for (int i = 0; i <= maxIndex; i++)
            fullList.add(i);
        sortList(fullList);
        return fullList.subList(0, numberOfIndexesToReturn);
    }



    /*****************************************************************************************************************
     * DOCS NUMBERS
     ****************************************************************************************************************/

    /** Format: 00 XX 000000 / 00 00 000000 / 0000 000000
     * @return
     */
    public String getDriverLicenseNumberRUS() {
        int licenseType = nextInt(3);
        String number = null;
        switch (licenseType) {
            case 0:
                // 1999 - 2011
                number = getNumbers(2) + " " + getCyrillic(2) + " " + getNumbers(6);
                break;
            case 1:
                // 2011 - 2014
                number = getNumbers(2) + " " + getNumbers(2) + " " + getNumbers(6);
                break;
            case 2:
                // after 2014
                number = getNumbers(4) + " " + getNumbers(6);
                break;
                default:
                    throw new IllegalArgumentException("licenseType = " + licenseType + ": not expected value");
        }
        return number;
    }
}
