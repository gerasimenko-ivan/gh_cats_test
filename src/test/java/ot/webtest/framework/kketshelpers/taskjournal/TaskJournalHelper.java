package ot.webtest.framework.kketshelpers.taskjournal;

import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.helpers.RegExpHelper;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.TaskSource;
import ot.webtest.framework.kketshelpers.dataobjects.WaybillStatus;

import static ot.webtest.framework.helpers.AllureHelper.*;

public class TaskJournalHelper extends HelperBase {
    // в "Задания -> Журнал заданий" только Децентрализованные задания!!!

    @Step("Получение децентрализованного задания из журнала заданий по полю Комментарий = '{comment}'")
    public DecentralizedTask getTaskByComment(String comment) {
        ElementsCollection elementsTd = getElements("Поиск записи о задании с комментраием '" + comment + "'", By.xpath("//tr[td/div[text()='" + comment + "']]/td"));
        if (elementsTd == null || elementsTd.size() == 0) {
            logSkipped("Задание не найдено.");
            return null;
        }
        ElementsCollection elementsHeader = getElements("Получаем заголовки столбцов", By.xpath("//div[contains(@class,'data-table')]//table/thead/tr/th"));
        if (elementsHeader.size() != elementsTd.size()) {
            Assert.fail("Не совпадает кол-во столбцов для записи n=" + elementsTd.size() + " и для заголовка n=" + elementsHeader.size());
        }

        DecentralizedTask task = new DecentralizedTask();
        for (int i = 0; i < elementsHeader.size(); i++) {
            String headerText = elementsHeader.get(i).getText();
            String tdText = elementsTd.get(i).getText();
            if (headerText.startsWith("Номер задания")) {
                task.withNumber(Integer.valueOf(tdText));
                continue;
            }
            if (headerText.equals("Путевой лист")) {
                logSkipped("Разграничение между статусами 'Путевой лист' затруднено. Для созданного задания со статусами ПЛ '" +
                        WaybillStatus.CREATE_SKETCH_OF_WAIBILL + " / " + WaybillStatus.ADD_TO_SKETCH_OF_WAIBILL +
                        "' значение колонки пустого. И только для статуса ПЛ '" + WaybillStatus.ADD_TO_ACTIVE_WAIBILL +
                        "' значение колонки равно номеру ПЛ.");
            }
            if (headerText.equals("Номер колонны")) {
                if (tdText.equals("-")) {
                    task.withIsTaskForColumn(false);
                } else {
                    try {
                        int columnNumber = Integer.parseInt(tdText);
                        logPassed("Номер колонны = " + columnNumber);
                        task.withIsTaskForColumn(true);
                    } catch(NumberFormatException e) {
                        logException(e);
                        Assert.fail("Проблема с определением номера колонны = '" + tdText + "'");
                    } catch(NullPointerException e) {
                        logException(e);
                        Assert.fail("Проблема с определением номера колонны = '" + tdText + "'");
                    }
                }
                continue;
            }
            if (headerText.equals("Источник")) {
                logPassed("'Источник': текстовое значение = '" + tdText + "'");
                TaskSource taskSource = TaskSource.getByName(tdText);
                task.withTaskSource(taskSource);
                continue;
            }
            if (headerText.equals("Начало")) {
                String date = RegExpHelper.getSubstring("\\d{2}\\.\\d{2}\\.\\d{4}", tdText);
                if (date != null)
                    task.withDateStart(new SpecialDateTime(date));
                else
                    Assert.fail("Дата начала не определена '" + tdText + "'");

                String time = RegExpHelper.getSubstring("\\d{2}:\\d{2}", tdText);
                if (time != null)
                    task.dateStart.withTime(time);
                else
                    Assert.fail("Время начала не определено '" + tdText + "'");
                continue;
            }
            if (headerText.equals("Завершение")) {
                String date = RegExpHelper.getSubstring("\\d{2}\\.\\d{2}\\.\\d{4}", tdText);
                if (date != null)
                    task.withDateEnd(new SpecialDateTime(date));
                else
                    Assert.fail("Дата завершения не определена '" + tdText + "'");

                String time = RegExpHelper.getSubstring("\\d{2}:\\d{2}", tdText);
                if (time != null)
                    task.dateEnd.withTime(time);
                else
                    Assert.fail("Время завершения не определено '" + tdText + "'");
                continue;
            }
            if (headerText.equals("Рег. номер ТС")) {
                task.withVehicleRegNumber(tdText);
                continue;
            }
            if (headerText.equals("Тип техники")) {
                task.withVehicleType(tdText);
                continue;
            }
            if (headerText.equals("Маршрут")) {
                task.withRouteName(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Количество циклов")) {
                task.withPassesCount(Integer.valueOf(tdText));
                continue;
            }
            if (headerText.equals("Технологическая операция")) {
                task.withTechnologicalOperation(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Элемент")) {
                task.withElement(new Special<>(tdText));
                continue;
            }
            if (headerText.equals("Комментарий")) {
                task.withComment(tdText);
                continue;
            }
            if (headerText.equals("Подразделение")) {
                task.withSubdivision(new Special<>(tdText));
                continue;
            }
        }
        return task;
    }

    @Step("Клик по кнопке '+ Создать децентрализованное задание'")
    public void pressCreateDecentralizedTask() {
        click(By.xpath("//button[@id='open-create-form']"));
    }
}
