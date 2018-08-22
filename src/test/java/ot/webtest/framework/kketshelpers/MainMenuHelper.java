package ot.webtest.framework.kketshelpers;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import ot.webtest.framework.HelperBase;

import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;

public class MainMenuHelper extends HelperBase {

    /////////// Рабочий стол

    @Step("Выбираем меню 'Рабочий стол'")
    public void gotoDashboard() {
        LoaderHelper loaderHelper = new LoaderHelper();

        click(By.xpath("//*[@id='link-dashboard']"));
        checkAppear(By.xpath("//div[@id='dashboard-time']"));
        loaderHelper.waitLoaderDisappear();
    }

    /////////// Задания

    private void openTasksMenu() {
        openSubMenu("Задания", By.xpath("//*[@id='show-missions']"), By.xpath("//ul[@aria-labelledby='show-missions']/li"));
    }

    @Step("Выбираем меню 'Задания --> Журнал заданий'")
    public void gotoTasks_TaskJournal() {
        LoaderHelper loaderHelper = new LoaderHelper();

        openTasksMenu();
        click(By.xpath("//ul/li/a[text()='Журнал заданий']"));
        checkAppear(By.xpath("//div[contains(@class,'data-table')]/div/div/div[text()='Журнал заданий']"));
        loaderHelper.waitLoaderDisappear();
    }

    @Step("Выбираем меню 'Задания --> Журнал наряд-заданий'")
    public void gotoTasks_WorkOrderTaskJournal() {
        LoaderHelper loaderHelper = new LoaderHelper();

        openTasksMenu();
        click(By.xpath("//ul/li/a[text()='Журнал наряд-заданий']"));
        checkAppear(By.xpath("//div[contains(@class,'data-table')]/div/div/div[text()='Журнал наряд-заданий']"));
        loaderHelper.waitLoaderDisappear();
    }

    //////////// НСИ

    @Step("")
    private void openNsiSubMenu() {
        openSubMenu("НСИ", By.xpath("//*[@id='show-nsi']"), By.xpath("//ul[@aria-labelledby='show-nsi']/li"));
    }

    @Step("Выбираем меню 'НСИ --> Показатели для расчета --> Операции для расчета топлива'")
    public void gotoNSI_MetrixForCalculations_FuelCalculationOperation() {
        LoaderHelper loaderHelper = new LoaderHelper();

        openNsiSubMenu();
        openSubMenu("Показатели для расчета", By.xpath("//*[@id='show-dataForCalculation']"), By.xpath("//ul[@aria-labelledby='show-dataForCalculation']/li"));
        click(By.xpath("//ul/li/a[text()='Операции для расчета топлива']"));

        checkAppear(By.xpath("//div[contains(@class,'data-table')]/div/div/div[text()='Операции для расчета топлива']"));
        loaderHelper.waitLoaderDisappear();
    }

    @Step("Выбираем меню 'НСИ --> Нормативные показатели --> Нормы расхода топлива'")
    public void gotoNSI_NormativeIndicators_FuelConsumptionNorms() {
        LoaderHelper loaderHelper = new LoaderHelper();

        openNsiSubMenu();
        openSubMenu("Нормативные показатели", By.xpath("//li/a[@id='show-normative']"), By.xpath("//ul[@aria-labelledby='show-normative']/li"));
        click(By.xpath("//ul/li/a[text()='Нормы расхода топлива']"));

        checkAppear(By.xpath("//div[contains(@class,'data-table')]/div/div/div[text()='Нормы расхода топлива']"));
        loaderHelper.waitLoaderDisappear();
    }
}
