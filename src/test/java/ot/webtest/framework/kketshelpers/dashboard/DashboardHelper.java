package ot.webtest.framework.kketshelpers.dashboard;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.framework.HelperBase;

public class DashboardHelper extends HelperBase {

    @Step("Нажимаем 'Создать децентрализованное задание' (В блоке 'Управление')")
    public void managementCreateDecentralizedTask() {
        click(By.xpath("//button[text()='Создать децентрализованное задание']"));
    }

    @Step("Нажимаем 'Создать наряд-задание' (В блоке 'Управление')")
    public void managementCreateWorkOrderTask() {
        click(By.xpath("//button[text()='Создать наряд-задание']"));
    }
}
