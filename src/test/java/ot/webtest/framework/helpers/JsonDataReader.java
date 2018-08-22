package ot.webtest.framework.helpers;

import org.json.JSONObject;
import org.testng.Assert;

import java.io.*;

import static ot.webtest.framework.helpers.AllureHelper.logException;

public class JsonDataReader {
    private JSONObject testData;

    public JSONObject getJsonObjectFromFile(String filePath) {
        File testDataFile = new File(filePath);
        FileReader testDataFileReader = null;
        try {
            testDataFileReader = new FileReader(testDataFile);
        } catch (FileNotFoundException e) {
            Assert.fail("Не найден файл с входными данными для теста. Путь к файлу: '" + filePath + "'");
            logException(e);
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(testDataFileReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            testDataFileReader.close();
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
