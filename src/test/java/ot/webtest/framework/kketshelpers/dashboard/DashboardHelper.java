package ot.webtest.framework.kketshelpers.dashboard;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.kketshelpers.LoaderHelper;

public class DashboardHelper extends HelperBase {

    @Step("Нажимаем 'Создать децентрализованное задание' (В блоке 'Управление')")
    public void managementCreateDecentralizedTask() {
        click(By.xpath("//button[text()='Создать децентрализованное задание']"));
    }

    @Step("Нажимаем 'Создать наряд-задание' (В блоке 'Управление')")
    public void managementCreateWorkOrderTask() {
        click(By.xpath("//button[text()='Создать наряд-задание']"));
    }

    @Step("Нажимаем 'Создать путевой лист' (В блоке 'Управление')")
    public void managementCreateWaybillTask() {
        click(By.xpath("//button[text()='Создать путевой лист']"));
        LoaderHelper loader = new LoaderHelper();
        loader.waitLoaderDisappear();
    }
}
