package ot.webtest.framework.kketshelpers;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.RobotHelper;

import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class LoginHelper extends HelperBase {

    By loginInputLocator = By.xpath("//input[@id='login']");
    By passwordInputLocator = By.xpath("//input[@id='password']");

    By exitButtonLocator = By.xpath("//li/a[text()='Выйти']");

    @Step("Логин в систему <{login}; {password}>")
    public void login(String login, String password) {
        setValue(loginInputLocator, login);
        setValue(passwordInputLocator, password);

        click(By.xpath("//button[@id='submit']"));
    }

    @Step("Проверяем успешность логина")
    public void checkSuccessfulLogin() {
        checkAppear(By.xpath("//a[@id='link-main-page']"));
        checkAppear(exitButtonLocator);
        checkAppear(By.xpath("//span[starts-with(text(), 'Версия ')]"));
    }

    @Step("Залогинен ли пользователь?")
    public boolean isLoggedIn() {
        long start = System.currentTimeMillis();
        do {
            if (isDisplayed("Отображается ли поле 'Логин'?", loginInputLocator))
                return false;
            if (isDisplayed("Отображается ли кнопка 'Выйти'?", exitButtonLocator))
                return true;
            sleepMillis(500, "Повторная проверка через 500мс.");
        } while (System.currentTimeMillis() - start < 20000);

        logFailScreenshot("За 20с. не было определено состояние приложения (logged in/out). Предполагаем, что залогинен...");
        return true;
    }
}
