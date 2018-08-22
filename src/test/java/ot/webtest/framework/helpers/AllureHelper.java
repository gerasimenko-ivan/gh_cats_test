package ot.webtest.framework.helpers;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import ot.webtest.framework.HelperBase;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created by KlimakovAE on 13.02.2018.
 */
public class AllureHelper extends HelperBase {

    /**
     * Добавляет строку в лог аллюра
     * @author Gerasimenko I.S.
     * @param message - текст лога
     */
    @Step("{0}")
    public static void logInfo(final String message) {
    }

    // Logging below inspired by: https://github.com/allure-framework/allure1/issues/967
    /** Лог со статусом 'Passed'
     * @param message
     */
    public static void logPassed(final String message) { logWithStatus(message, Status.PASSED); }
    /** Лог со статусом 'Failed'
     * @param message
     */
    public static void logFailed(final String message) { logWithStatus(message, Status.FAILED); }
    /** Лог со статусом 'Skipped': белый 'кирпич' в сером круге
     * @param message
     */
    public static void logSkipped(final String message) { logWithStatus(message, Status.SKIPPED); }
    /** Лог со статусом 'Broken': белый '!' в оранжевом круге
     * @param message
     */
    public static void logBroken(final String message) { logWithStatus(message, Status.BROKEN); }
    private static void logWithStatus(final String message, final Status status) {
        StepResult stepResult =
                new StepResult()
                        .withStatus(status)
                        .withName(message);
        Allure.getLifecycle().startStep("uuid-test", stepResult);
        Allure.getLifecycle().stopStep();
    }

    /** Логирование исключения
     * @param exception
     */
    public static void logException(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        exception.printStackTrace(printWriter);
        logFailed("EXCEPTION: " + exception.getMessage());
        logFailed("EXCEPTION: " + stringWriter.toString());
    }

    @Step("FAILED: {message}")
    public static void logFailedMessageBlock(final String message, final List<String> lines) { logLinesWithStatus(message, Status.FAILED, lines); }
    @Step("{message}")
    public static void logPassedMessageBlock(final String message, final List<String> lines) { logLinesWithStatus(message, Status.PASSED, lines); }
    private static void logLinesWithStatus(final String message, final Status status, final List<String> lines) {
        for (String s : lines) {
            logWithStatus(s, status);
        }
    }


    /** Добавляет строку-разделитель в лог аллюра
     * @author Gerasimenko I.S.
     */
    @Step("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -")
    public static void logDelimeter() {
    }
    /** Добавляет толстую строку-разделитель в лог аллюра
     * (для разделения значительных этапов теста; больше внутри тестов бизнес-процессов)
     * @author Gerasimenko I.S.
     */
    @Step("●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●")
    public static void logBoldDelimeter() {
    }

    /**
     * Вставляет скриншот в лог аллюра
     * @author Gerasimenko I.S.
     */
    @Step("[ o︢ ] screenshot")
    public static void logScreenshot() {
        saveScreenshotCoreMethod("screenshot");
    }

    /** Вставляет скриншот с комментарием в лог аллюра
     * @author Gerasimenko I.S.
     * @param message - комментарий после текста 'screenshot:'
     */
    @Step("[ o︢ ] screenshot: {0}")
    public static void logScreenshot(String message) {
        saveScreenshotCoreMethod("screenshot: " + message);
    }

    /** Вставляет скриншот с комментарием в лог аллюра (маркер у комментария красного цвета)
     * @author Gerasimenko I.S.
     * @param message - комментарий после текста 'screenshot:'
     */
    public static void logFailScreenshot(String message) {
        logScreenshot(message, Status.FAILED);
    }

    /** Вставляет скриншот с комментарием в лог аллюра (маркер у комментария оранжевого цвета)
     * @author Gerasimenko I.S.
     * @param message - комментарий после текста 'screenshot:'
     */
    public static void logBrokenScreenshot(String message) {
        logScreenshot(message, Status.BROKEN);
    }
    private static void logScreenshot(String message, Status status) {
        StepResult stepResult = new StepResult().withStatus(status).withName("[ o︢ ] screenshot: " + message);
        Allure.getLifecycle().startStep("uuid-test", stepResult);
        saveScreenshotCoreMethod("screenshot: " + message);
        Allure.getLifecycle().stopStep();
    }

    @Attachment(value = "{0}", type = "image/png")
    private static byte[] saveScreenshotCoreMethod(String message) {
        WebDriver driver = getWebDriver();
        if(driver instanceof WebDriver) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
            logInfo("Driver uje ne driver");
        }

        return null;
    }

}
