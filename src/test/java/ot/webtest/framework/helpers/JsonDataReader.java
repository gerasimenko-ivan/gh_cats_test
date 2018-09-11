package ot.webtest.framework.helpers;

import org.json.JSONObject;
import org.testng.Assert;

import java.io.*;

import static ot.webtest.framework.helpers.AllureHelper.logException;
import static ot.webtest.framework.helpers.AllureHelper.logPassed;

public class JsonDataReader {
    private JSONObject testData;

    public JSONObject getJsonObjectFromFile(String filePath) {
        File testDataFile = new File(filePath);
        FileInputStream testDataFileInputStream = null;
        try {
            testDataFileInputStream = new FileInputStream(testDataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Не найден файл с входными данными для теста. Путь к файлу: '" + filePath + "'");
        }
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(testDataFileInputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("UnsupportedEncodingException - UTF-8 seems not to be supported :(");
        }
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            testDataFileInputStream.close();
        } catch (IOException e) {
            logException(e);
            e.printStackTrace();
        }

        testData = new JSONObject(stringBuffer.toString());
        return testData;
    }

    /** Получаем значение по ключу (первый уровень пар ключ-значение)
     * @param key
     * @return
     */
    public String getValueByKey(String key) {
        if (testData.has(key)) {
            return testData.get(key).toString();
        }
        return null;
    }
}
