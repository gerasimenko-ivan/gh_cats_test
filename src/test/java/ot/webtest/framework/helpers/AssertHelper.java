package ot.webtest.framework.helpers;

import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.Collections;
import java.util.List;

import static ot.webtest.framework.helpers.AllureHelper.logPassed;

public class AssertHelper {

    public static void assertEquals(Boolean actual, Boolean expected, String actionComment) {
        Assert.assertEquals(actual, expected, actionComment);
        logPassed("PASSED: " + actionComment + " >> Actual value = '" + actual + "'");
    }

    public static void assertEquals(Integer actual, Integer expected, String actionComment) {
        Assert.assertEquals(actual, expected, actionComment);
        logPassed("PASSED: " + actionComment + " >> Actual value = '" + actual + "'");
    }

    public static void assertEquals(Double actual, Double expected, String actionComment) {
        Assert.assertEquals(actual, expected, actionComment);
        logPassed("PASSED: " + actionComment + " >> Actual value = '" + actual + "'");
    }

    public static void assertEquals(String actual, String expected, String actionComment) {
        Assert.assertEquals(actual, expected, actionComment);
        logPassed("PASSED: " + actionComment + " >> Actual value = '" + actual + "'");
    }

    public static void assertEquals(Object actual, Object expected, String actionComment) {
        Assert.assertEquals(actual, expected, actionComment);
        logPassed("PASSED: " + actionComment + " >> Actual value = '" + actual + "'");
    }

    // COLLECTIONs

    public static void assertCollectionEquals(java.util.Collection<?> collectionActual, java.util.Collection<?> collectionExpected, String actionComment) {
        Assert.assertEquals(collectionActual, collectionExpected, actionComment);
        logPassed("PASSED: " + actionComment);
    }

    public static <T extends Comparable> void assertListEqualsWithSort(java.util.List<T> collectionActual, java.util.List<T> collectionExpected, String actionComment) {
        if (collectionActual == null && collectionExpected == null) {
            logPassed("PASSED: оба списка имеют значение null; " + actionComment);
            return;
        }
        Collections.sort(collectionActual);
        Collections.sort(collectionExpected);
        Assert.assertEquals(collectionActual, collectionExpected, actionComment);
        logPassed("PASSED: " + actionComment);
    }

    @Step("Проверяем, что все элементы списка содержат подстроку '{textToFind}'")
    public static void checkAllListItemsContainString(String textToFind, List<String> list, boolean caseSensitive) {
        int index = 0;
        for (String item : list) {
            String failString = "Элемент списка {#" + index + "; '" + item + "'} не содержит подстроки '" + textToFind + "'";
            if (caseSensitive) {
                if (!item.contains(textToFind)) {
                    Assert.fail(failString);
                }
            } else {
                if (!item.toLowerCase().contains(textToFind.toLowerCase())) {
                    Assert.fail(failString);
                }
            }
            index++;
        }
        logPassed("PASSED: все " + list.size() + " строки(а) содержат подстроку '" + textToFind + "'");
    }

    // BOOLEAN: TRUE / FALSE

    public static void checkTrue(boolean condition, String passedMessage, String failedMessage) {
        if (condition) {
            logPassed("PASSED: " + passedMessage);
        } else {
            Assert.fail("FAIL: " + failedMessage);
        }
    }
    public static void checkFalse(boolean condition, String passedMessage, String failedMessage) {
        checkTrue(!condition, passedMessage, failedMessage);
    }
}
