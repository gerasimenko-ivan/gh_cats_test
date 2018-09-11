package ot.webtest.framework.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringHelper {

    public static String get(Double number, int mantissaLength, String delimeter) {
        //DecimalFormat df = new DecimalFormat("#." + new String(new char[mantissaLength]).replace("\0", "0"));
        return String.format("%." + mantissaLength + "f", number)
                .replace(",", delimeter)
                .replace(".", delimeter);
    }

    public static String getDevByDotsDDMMYYYY(LocalDate localDate) {
        if (localDate == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.LL.yyyy");
        return localDate.format(formatter);
    }

    /** Возвращает ФИО вида "Торокулов М.М." из ФИО вида "Торокулов Максатбек Мисирович".
     * Любое дополнительныое имя за отчеством опускается!
     * Если Имя или отчество начинается со строчной буквы, регистр не меняется.
     * @param fio
     * @return
     */
    public static String fioToSurname_XX(String fio) {
        String[] split = fio.split("\\s+");
        String Surname_XX = null;
        if (split.length > 0 && split[0].length() > 0)
            Surname_XX = split[0];
        else
            return null;
        if (split.length > 1 && split[1].length() > 0)
            Surname_XX += " ";
        else
            return Surname_XX;
        for (int i = 1; i < split.length && i < 3; i++) {
            String nextFioPart = split[i];
            if (nextFioPart.length() > 0) {
                Surname_XX += nextFioPart.substring(0, 1) + ".";
            }
        }
        return Surname_XX;
    }

    public static String fioToSingleString(String surname, String name, String middleName) {
        if (!isNotEmptyString(surname)) {
            throw new IllegalArgumentException("Фамилия не может быть пустой или NULL.");
        }
        String fioSingleLine = surname;
        if (isNotEmptyString(name)) {
            fioSingleLine += " " + name;
        }
        if (isNotEmptyString(middleName)) {
            fioSingleLine += " " + middleName;
        }
        return fioSingleLine;
    }

    /** Получаем значения true, если строка не NULL и не пустая
     * @param string
     * @return
     */
    public static boolean isNotEmptyString(String string) {
        return (string != null && !string.equals(""));
    }
}
