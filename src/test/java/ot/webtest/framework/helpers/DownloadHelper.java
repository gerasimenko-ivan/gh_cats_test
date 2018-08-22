package ot.webtest.framework.helpers;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import ot.webtest.framework.HelperBase;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ot.webtest.framework.BrowserManager.GLOBAL_TIMEOUT;
import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillisSilent;

public class DownloadHelper extends HelperBase {
    private static List<String> partOfFileNamesThatWereDownloaded = new ArrayList<>();
    private static String downloadsFolderPath = System.getProperty("user.home") + "\\Downloads";


    /** Открывает страницу загрузок в новом табе
     * @author Gerasimenko I.S.
     */
    @Step("Открываем страницу загрузок в новом табе")
    public void openDownloadsPageInNewTab() {
        //driver.get("chrome://downloads");
        //To open a new tab
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            Assert.fail(e.getMessage());
        }
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_T);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_T);

        //add check that there are 2 tabs opened
        ArrayList<String> tabs;
        int checksCount = 0;
        WebDriver driver = getWebDriver();
        do {
            tabs = new ArrayList<>(driver.getWindowHandles());
            sleepMillisSilent(500);
            checksCount++;
        } while (checksCount < 20 && tabs.size() < 2);
        if (tabs.size() < 2) {
            Assert.fail("Не был открыт новый таб за 10 секунд.");
        }
        //To switch to the new tab
        driver.switchTo().window(tabs.get(1));
        //To navigate to new link/URL in 2nd new tab
        driver.get("chrome://downloads");
    }

    @Step("Закрываем текущий таб")
    private void closeCurrentTab() { // сделал private, т.к. по-хорошему ей здесь не место
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            Assert.fail(e.getMessage());
        }
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_W);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_W);
    }

    public void gotoTab(int index) {
        WebDriver driver = getWebDriver();
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(index));
    }

    /*   /** Проверка загрузки файла (в течении 60 секунд) с использованием вкладки 'Загрузки'
     * @author Gerasimenko I.S.
     * @param partOfFileName - часть из имени загружаемого файла
     * @return - имя скачанноого файла
     */
    /*@Step("Проверяем загрузку файла (в течении 60 секунд) с использованием вкладки 'Загрузки'")
    public String checkDownloadingOnDownloadsPage(String partOfFileName) {
        // 60 секундный цикл из переходов "Основна страница" <-> "Загрузка", т.к. загрузка в режиме тестов не стартует,
        // когда фокус не на табе, где она была инициирована, а на странице загрузке контроллируем её выполнение.
        String fileName = null;
        boolean doesExistDownload = false;

        setImplicitlyWait(0);
        long startTime = System.currentTimeMillis();
        long currentTime, finishTime;
        openDownloadsPageInNewTab();

        logInfo("Ожидаем завершения загрузки... (появления файла на странице)");
        do {
            sleepMillisSilent(1000);
            gotoTab(0);
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < 10000) {
                logScreenshot("С начала загрузки прошло " + (elapsed/1000) + "с.(+/-1с.). Контроллируем 'уплывающие' ошибки ПИРа...");
            }
            sleepMillisSilent(1000);
            gotoTab(1);
            // именно такая ступенчатая схема прохода по элементам необходима при работе с элементами на странице загрузок
            // т.к. здесь используются вложенные Shadow элементы (в них, кстати, получилось работать только по By.cssSelector)
            boolean exist = doesWebElementExist(0, By.xpath("//downloads-manager"));
            if (exist) {
                WebElement element = driver.findElement(By.xpath("//downloads-manager"));
                WebElement elementFirstShadow = expandShadowRootElement(element);
                exist = doesElementExistInParent(elementFirstShadow, 0, By.cssSelector("iron-list>downloads-item"));
                if (exist) {
                    WebElement elementDownloadsItem = elementFirstShadow.findElement(By.cssSelector("iron-list>downloads-item"));
                    WebElement elementSecondShadow = expandShadowRootElement(elementDownloadsItem);
                    exist = doesElementExistInParent(elementSecondShadow, 0, By.cssSelector("div[id=content]>div[id=details]>div[id=title-area]"));
                    if (exist) {
                        finishTime = System.currentTimeMillis();
                        WebElement divFileName = elementSecondShadow.findElement(By.cssSelector("div[id=content]>div[id=details]>div[id=title-area]"));
                        WebElement downloadedFrom = elementSecondShadow.findElement(By.cssSelector("div[id=content]>div[id=details]>a"));
                        fileName = divFileName.getText();
                        if (fileName.contains(partOfFileName)) {
                            logInfo("Имя файла: " + fileName);
                            logInfo("Ссылка: " + downloadedFrom.getText());
                            logInfo("Время загрузки: " + (finishTime - startTime)/1000 + "с.(+/-1с.)"); // время загрузки можно оценить только c погрешностью
                            doesExistDownload = true;
                        }
                    }
                }
            }
            currentTime = System.currentTimeMillis();
        } while ((currentTime < startTime + 60000) && !doesExistDownload);
        setImplicitlyWait(GLOBAL_TIMEOUT);
        if (doesExistDownload != true) {
            logFailScreenshot("Страница загрузок");
            gotoTab(0);
            logFailScreenshot("Страница приложения");
            Assert.fail("Загрузка не была завершена в отведённое время (60 секунд). Либо загруженный файл не содержит в названии строки '" + partOfFileName + "'");
        }
        checkDownloadedFile(fileName);
        closeCurrentTab();
        gotoTab(0);
        return fileName;
    }*/

    /** Возвращает список файлов (и директорий) в папке загрузок
     * @return
     */
    @Step("Получаем список загруженных файлов")
    public List<String> getListOfDownloadedFiles() {
        String filePathFull = System.getProperty("user.home") + "\\Downloads";
        File[] listOfFiles = (new File(filePathFull)).listFiles();
        logInfo("Кол-во файлов: " + listOfFiles.length);
        return Arrays.asList(listOfFiles).stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList());
    }

    /** Проверяет появление нового файла в директории загрузок и соответветствие имени файла шаблону.
     * @param partOfFileName
     * @param listOfDownloadedFilesBefore
     * @return
     */
    @Step("Проверка загрузки файла, часть имени которого '{0}'")
    public String checkDownloading(String partOfFileName, List<String> listOfDownloadedFilesBefore) {
        String newFilePath = null;
        long startTime = System.currentTimeMillis();
        do {
            sleepMillisSilent(1000);
            long timePassed = System.currentTimeMillis() - startTime;
            if (timePassed < 20000) {
                logScreenshot("С начала загрузки прошло " + timePassed/1000 + "с.(+/-1с.)");
            }
            File[] listOfFiles = (new File(downloadsFolderPath)).listFiles();
            List<String> listOfDownloadedFilesAfter = Arrays.asList(listOfFiles).stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList());
            if(listOfDownloadedFilesBefore.size() + 1 == listOfDownloadedFilesAfter.size()) {
                long finishTime = System.currentTimeMillis();
                List<String> newFiles = new ArrayList<>(listOfDownloadedFilesAfter);
                newFiles.removeAll(listOfDownloadedFilesBefore);
                newFilePath = newFiles.get(0);
                if (newFilePath.contains(".crdownload") || newFilePath.contains(".tmp")) {
                    continue;
                }
                if (newFilePath.contains(partOfFileName)) {
                    logInfo("Новый файл: " + newFilePath);
                    logInfo("Время загрузки: " + (finishTime - startTime)/1000 + "с.(+/-1с.)"); // время загрузки можно оценить только c погрешностью
                    File downloadedFile = new File(newFilePath);
                    if (downloadedFile.length() <= 0) {
                        Assert.fail("Размер файла равен нулю.");
                    } else {
                        logPassed("Размер файла " + downloadedFile.length() + " байт.");
                    }
                    break;
                } else {
                    Assert.fail("Новый файл <" + newFilePath + "> не содержит строки <" + partOfFileName + ">");
                }
            } else if(listOfDownloadedFilesBefore.size() + 1 < listOfDownloadedFilesAfter.size()) {
                listOfDownloadedFilesAfter.removeAll(listOfDownloadedFilesBefore);
                logInfo("Новые файлы: " + listOfDownloadedFilesAfter);
                Assert.fail("Количество загруженных файлов увеличелось больше, чем на один");
            }

        } while (System.currentTimeMillis() < startTime + 60000);
        if (newFilePath == null) {
            Assert.fail("Загрузка не была завершена в отведённое время (60 секунд). Либо загруженный файл не содержит в названии строки '" + partOfFileName + "'");
        }
        partOfFileNamesThatWereDownloaded.add(partOfFileName);
        return newFilePath;
    }

    @Step("Проверка существования файла.")
    private void checkDownloadedFile(String fileName) {
        String filePathFull = System.getProperty("user.home") + "\\Downloads\\" + fileName;

        File downloadedFile = new File(filePathFull);
        if (downloadedFile.exists() && !downloadedFile.isDirectory()) {
            logPassed("Проверено, что файл <" + filePathFull + "> существует");
        } else {
            Assert.fail("Файл <" + filePathFull + "> не существует (или это директория).");
        }

        if (downloadedFile.length() <= 0) {
            Assert.fail("Размер файла равен нулю.");
        } else {
            logPassed("Размер файла " + downloadedFile.length() + " байт.");
        }
    }

   /* private WebElement expandShadowRootElement(WebElement element) {
        WebElement shadowRootElement = (WebElement) ((JavascriptExecutor)driver)
                .executeScript("return arguments[0].shadowRoot", element);
        return shadowRootElement;
    }*/

    /** Проверка соответствия имени файла шаблону (регулярное выражение)
     * @author Gerasimenko I.S.
     * @param pattern
     * @param downloadFileName
     */
    @Step("Проверка соответствия имени файла шаблону")
    public void checkFileNameByPattern(String pattern, String downloadFileName) {
        if (Pattern.compile(pattern).matcher(downloadFileName).find()) {
            logPassed("Имя скачанного файла {" + downloadFileName + "} соответствует шаблону '" + pattern + "'");
        } else {
            Assert.fail("Имя скачанного файла {" + downloadFileName + "} не соответствует шаблону '" + pattern + "'");
        }
    }

    /** Удаляет все файлы, шаблоны которых использовались в тесте, и с возрастом более 2 дней
     * @author Gerasimenko I.S.
     */
    @Step("Удаляем все файлы, шаблоны которых использовались в тесте, и с возрастом более 2 дней")
    public void cleanDownloadFolder() {
        File[] listOfFiles = (new File(downloadsFolderPath)).listFiles();
        int deletedFilesCount = 0;
        for (File file : listOfFiles) {
            for (String partOfFileName : partOfFileNamesThatWereDownloaded) {
                if (file.getName().contains(partOfFileName)) {
                    if (((System.currentTimeMillis() - file.lastModified()) / (60*60*1000)) > 34) {   // с возрастом более 34 часов (т.е. храним все загрузки вчерашнего дня)
                        file.delete();
                        deletedFilesCount++;
                    }
                    break;
                }
            }
        }
        logPassed("Удалено " + deletedFilesCount + " файлов.");
        partOfFileNamesThatWereDownloaded.clear();
    }
}
