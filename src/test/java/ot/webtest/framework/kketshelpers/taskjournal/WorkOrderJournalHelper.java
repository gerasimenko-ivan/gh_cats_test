package ot.webtest.framework.kketshelpers.taskjournal;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.kketshelpers.dataobjects.TaskSource;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;

import static ot.webtest.framework.helpers.AllureHelper.logPassed;
import static ot.webtest.framework.helpers.AllureHelper.logSkipped;

public class WorkOrderJournalHelper extends HelperBase {

    @Step("Получаем наряд-задание из таблицы по комментарию '{comment}'")
    public WorkOrderTask getWorkOrderByComment(String comment) {
        ElementsCollection elementsTd = getElements("Поиск записи о наряд-задании с комментраием '" + comment + "'", By.xpath("//tr[td/div[text()='" + comment + "']]/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("Наряд-задание не найдено.");
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'data-table')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        WorkOrderTask workOrderTask = new WorkOrderTask();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();
            if (headerText.startsWith("Номер")) {
                workOrderTask.withNumber(Integer.valueOf(tdText));
                continue;
            }
            if (headerText.equals("Источник")) {
                logPassed("'Источник': текстовое значение = '" + tdText + "'");
                TaskSource taskSource = TaskSource.getByName(tdText);
                workOrderTask.withTaskSource(taskSource);
                continue;
            }
            if (headerText.equals("Технологическая операция")) {
                workOrderTask.withTechnologicalOperation(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Элемент")) {
                workOrderTask.withElement(new Special<>(tdText));
                continue;
            }

            if (headerText.equals("Начало план.")) {
                String date = RegExpHelper.getSubstring("\\d{2}\\.\\d{2}\\.\\d{4}", tdText);
                if (date != null)
                    workOrderTask.withDateStart(new SpecialDateTime(date));
                else
                    Assert.fail("Дата начала не определена '" + tdText + "'");

                String time = RegExpHelper.getSubstring("\\d{2}:\\d{2}", tdText);
                if (time != null)
                    workOrderTask.dateStart.withTime(time);
                else
                    Assert.fail("Время начала не определено '" + tdText + "'");
                continue;
            }
            if (headerText.equals("Завершение план.")) {
                String date = RegExpHelper.getSubstring("\\d{2}\\.\\d{2}\\.\\d{4}", tdText);
                if (date != null)
                    workOrderTask.withDateEnd(new SpecialDateTime(date));
                else
                    Assert.fail("Дата завершения не определена '" + tdText + "'");

                String time = RegExpHelper.getSubstring("\\d{2}:\\d{2}", tdText);
                if (time != null)
                    workOrderTask.dateEnd.withTime(time);
                else
                    Assert.fail("Время завершения не определено '" + tdText + "'");
                continue;
            }

            if (headerText.equals("Маршрут")) {
                workOrderTask.withRouteName(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Бригадир")) {
                workOrderTask.withBrigadier(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Комментарий")) {
                workOrderTask.withComment(tdText);
                continue;
            }
            if (headerText.equals("Подразделение")) {
                workOrderTask.withSubdivision(new Special<>(tdText));
                continue;
            }
        }

        return workOrderTask;
    }

    @Step("Клик по кнопке '+ Создать'")
    public void pressCreateWorkOrder() {
        click(By.xpath("//button[@id='open-create-form']"));
    }
}
