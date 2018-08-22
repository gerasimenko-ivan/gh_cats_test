package ot.webtest.framework.kketshelpers;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.framework.HelperBase;

import static ot.webtest.framework.BrowserManager.GLOBAL_TIMEOUT;
import static ot.webtest.framework.helpers.AllureHelper.logPassed;
import static ot.webtest.framework.helpers.AllureHelper.logScreenshot;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class LoaderHelper extends HelperBase {

    @Step("Ожидание исчезновения прогресс-бара загрузки...")
    public void waitLoaderDisappear() {
        sleepMillis(200);
        long start = System.currentTimeMillis();
        boolean isLoaderDisplayed = false;
        logScreenshot("До проверки наличия прогресс-бара загрузки");
        do {
            isLoaderDisplayed = isDisplayedWithTimeout(By.xpath("//div[contains(@class, 'cssload-loader')]"), 0);
        } while (System.currentTimeMillis() - start < GLOBAL_TIMEOUT && isLoaderDisplayed);
        if (isLoaderDisplayed) {
            Assert.fail("Прогресс-бар загрузки НЕ исчез за " + GLOBAL_TIMEOUT + "мс.");
        } else {
            logScreenshot("Прогресс-бар загрузки исчез примерно за " + (System.currentTimeMillis() - start) + "мс.");
        }
    }
}
