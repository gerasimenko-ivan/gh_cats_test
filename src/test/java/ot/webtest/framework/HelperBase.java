package ot.webtest.framework;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import ot.webtest.dataobject.DropDownResponse;
import ot.webtest.dataobject.ListNode;
import ot.webtest.dataobject.Special;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.helpers.RobotHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ot.webtest.framework.BrowserManager.EXCEPTION_IGNORE_TIMEOUT;
import static ot.webtest.framework.BrowserManager.GLOBAL_TIMEOUT;
import static ot.webtest.framework.helpers.AllureHelper.*;
import static ot.webtest.framework.helpers.AssertHelper.checkFalse;
import static ot.webtest.framework.helpers.AssertHelper.checkTrue;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillis;
import static ot.webtest.framework.helpers.TimerHelper.sleepMillisSilent;

/** HelperBase must contain only (ONLY!!!) functions which could be used across ANY projects!!!
 */
public class HelperBase {

    /*******************************************************************************************************************
     * ДЕЙСТВИЯ
     *******************************************************************************************************************/


    @Step("Обновление страницы (F5)")
    public void refreshPage() {
        RobotHelper rh = new RobotHelper();
        rh.pressF5();
        sleepMillis(500);
    }

    @Step("Открываем ссылку <<{url}>> в браузере")
    public void openUrl(String url) {
        open(url);
    }

    // TEXT

    @Step("Получаем текст элемента <<{webElementName}>>")
    public String getText(By locator, String webElementName) {
        String text = $(locator).getText();
        logPassed("text: " + text);
        return text;
    }
    public void setValue(String fieldName, By locator, String text) {
        setValue(fieldName, $(locator), text);
    }
    public void setValue(By locator, String text) {
        setValue(locator.toString(), $(locator), text);
    }
    @Step("Ввод в поле <<{fieldName}>> строки <<{text}>>")
    public void setValue(String fieldName, SelenideElement element, String text) {
        element.click();
        element.clear();
        element.setValue(text);
    }
    @Step("Нажатие на клавишу ENTER в поле")
    public void sendKeyEnter(By locator) {
        $(locator).sendKeys(Keys.ENTER);
    }
    /** Ввод значение в поле (если параметр text содержит не нулевую строку)
     * @param fieldName
     * @param locator
     * @param text
     */
    public void setValueOrLogNoDataToSet(String fieldName, By locator, String text) {
        if (text != null && !text.equals("")) {
            setValue(fieldName, locator, text);
        } else {
            logSkipped("Значение поля '" + fieldName + "' не задано. Ввод не производится.");
        }
    }

    /** Установка значения чек-бокса (если параметр isSelected содержит не нулевое значение)
     * @param checkBoxName
     * @param inputLocator
     * @param isSelected
     */
    public void setCheckBoxOrLogNoDataToSet(String checkBoxName, By inputLocator, Boolean isSelected) {
        if (isSelected != null) {
            setCheckBox(checkBoxName, inputLocator, isSelected);
        } else {
            logSkipped("Значение чек-бокса '" + checkBoxName + "' не задано. Изменение значения не производится.");
        }
    }

    // DROP-DOWN

    /** Раскрытие дроп-дауна (списка) с ожиданием появления одного из элементов списка (удостовериваемся в открытии)
     * @param dropDownName
     * @param openDropDownButtonElement
     * @param dropDownListItem
     */
    @Step("Раскрытие списка '{dropDownName}' (с ожиданием появления одного из элементов списка)")
    public void openDropDown(String dropDownName, By openDropDownButtonElement, By dropDownListItem) {
        long startTime = System.currentTimeMillis();
        do {
            SelenideElement openDropDownButton = findElementHavingInvisibleDuplicates(openDropDownButtonElement, "Кнопка раскрытия списка элементов");
            click("Кнопка раскрытия списка элементов", openDropDownButton);
            if (isDisplayed("Проверка появления элементата списка", dropDownListItem)) {
                logPassed("Список раскрыт");
                return;
            }
            logScreenshot("Повтор через 500мс., т.к. не открылся список");
            sleepMillisSilent(500);

            // DEBUGGING PART STARTS
            /*if (dropDownName.equals("Бригада")) {
                logPassed("innerHTML: " + findElementHavingInvisibleDuplicates(By.xpath("//div[label[text()='Бригада']]")).innerHtml());
                //Assert.fail("DEBUGGING!!!!");
            }
            // DEBUGGING PART ENDS */
        } while (System.currentTimeMillis() - startTime < GLOBAL_TIMEOUT);
        Assert.fail("Раскрытия списка не произошло (за " + GLOBAL_TIMEOUT + "мс.; проверка по элементу <<" + dropDownListItem + ">>");
    }

    @Step("Получаем все элементы списка '{dropDownName}'")
    public List<String> getItemsOfOpenedDropDownList(String dropDownName, By itemsLocator) {
        ElementsCollection elements = getElements(itemsLocator);
        List<String> options = new ArrayList<>();
        for (SelenideElement element : elements) {
            String option = element.getText();
            logPassed("{option: " + option + "}");
            options.add(option);
        }
        return options;
    }

    @Step("Получаем количество элементов списка '{dropDownName}'")
    public Integer getNumberOfItemsOfOpenedDropDownList(String dropDownName, By itemsLocator) {
        ElementsCollection elements = getElements(itemsLocator);
        int count = elements.size();
        logPassed("count = " + count);
        return count;
    }

    @Step("В выпадающем списке '{fieldName}' выбираем опцию '{value}'")
    public Special<String> dropDownSelect(String fieldName, Special<String> value, By openDropDownButtonLocator, By dropDownListAnyItemLocator) {
        DropDownResponse dropDownResponse = dropDownSelect(
                fieldName,
                value,
                openDropDownButtonLocator,
                dropDownListAnyItemLocator,
                null);
        return dropDownResponse == null ? null : dropDownResponse.selectedValue;
    }

    /** Special version of dropDownSelect if there are too many items in drop-down list (ATTENTION TO INDEXED ARGUMENT!!!)
     * @param fieldName
     * @param value
     * @param openDropDownButtonLocator
     * @param dropDownListAnyItemXPathWithINDEX - String-xpath with text '[INDEX]' for random selection by index, e.g. '//ul/li[INDEX]'
     */
    @Step("В выпадающем списке '{fieldName}' выбираем опцию '{value}'")
    public Special<String> dropDownSelect(String fieldName, Special<String> value, By openDropDownButtonLocator, String dropDownListAnyItemXPathWithINDEX) {
        DropDownResponse dropDownResponse = dropDownSelect(
                fieldName,
                value,
                openDropDownButtonLocator,
                null,
                dropDownListAnyItemXPathWithINDEX);
        return dropDownResponse == null ? null : dropDownResponse.selectedValue;
    }

    /** MUST use only one of arguments "dropDownListAnyItemLocator" || "dropDownListAnyItemXPathWithINDEX" (leave one as NULL)
     */
    @Step("В выпадающем списке '{fieldName}' выбираем опцию '{value}'")
    public DropDownResponse dropDownSelectGeneral (String fieldName, Special<String> value, By openDropDownButtonLocator, By dropDownListAnyItemLocator, String dropDownListAnyItemXPathWithINDEX) {
        return
                dropDownSelect(
                        fieldName,
                        value,
                        openDropDownButtonLocator,
                        dropDownListAnyItemLocator,
                        dropDownListAnyItemXPathWithINDEX);
    }

    /** general method to handle DropDowns:
     * MUST use only one of arguments "dropDownListAnyItemLocator" || "dropDownListAnyItemXPathWithINDEX" (leave one as NULL)
     * @param fieldName
     * @param value
     * @param openDropDownButtonLocator
     * @param dropDownListAnyItemLocator
     * @param dropDownListAnyItemXPathWithINDEX
     * @return
     */
    private DropDownResponse dropDownSelect(String fieldName, Special<String> value, By openDropDownButtonLocator, By dropDownListAnyItemLocator, String dropDownListAnyItemXPathWithINDEX) {
        DropDownResponse dropDownResponse = new DropDownResponse();
        final String INDEX = "[INDEX]";
        String dropDownListAnyItemXPath = "";
        if (dropDownListAnyItemXPathWithINDEX != null) {
            dropDownListAnyItemXPath = dropDownListAnyItemXPathWithINDEX.replace(INDEX, "");
            dropDownListAnyItemLocator = By.xpath(dropDownListAnyItemXPath);
        }
        if (value == null || value.getType() == Special.Type.NULL) {
            logSkipped("Нет данных для ввода в поле '" + fieldName + "'");
            return null;
        } else {
            if (value.getType() == Special.Type.RANDOM) {
                openDropDown(
                        fieldName,
                        openDropDownButtonLocator,
                        dropDownListAnyItemLocator);
                if (dropDownListAnyItemXPathWithINDEX != null) {
                    Integer optionsCount =
                            getNumberOfItemsOfOpenedDropDownList(
                                    fieldName,
                                    dropDownListAnyItemLocator);
                    Integer randomOptionIndex = new Random().nextInt(optionsCount) + 1;

                    List<String> tenRandomOptions = new ArrayList<>();
                    RandomDataGenerator rnd = new RandomDataGenerator();
                    if (optionsCount > 10 ) {
                        List<Integer> tenRandomIndexes = rnd.getUniqueIndexes(10, optionsCount - 1);
                        for (int i : tenRandomIndexes) {
                            i = i + 1;
                            tenRandomOptions.add(
                                    getText(By.xpath(dropDownListAnyItemXPathWithINDEX.replace(INDEX, "[" + i + "]")), "Текст опции #" + i));
                        }
                    } else {
                        for (int i = 0; i < optionsCount; i++) {
                            int index = i + 1;
                            tenRandomOptions.add(
                                    getText(By.xpath(dropDownListAnyItemXPathWithINDEX.replace(INDEX, "[" + index + "]")), "Текст опции #" + index));
                        }
                    }

                    By optionToSelectLocator = By.xpath(dropDownListAnyItemXPathWithINDEX.replace(INDEX, "[" + randomOptionIndex + "]"));
                    String optionToSelectText = getText(optionToSelectLocator, "Текст опции #" + randomOptionIndex);
                    sleepMillis(100);
                    logPassed("Будет выбрана опция #" + randomOptionIndex + " со значением '" + optionToSelectText + "'");
                    click("Выбираем опцию #" + randomOptionIndex + " (случайным образом)", optionToSelectLocator);
                    dropDownResponse
                            .withSelectedValue(new Special<>(optionToSelectText))
                            .withAvailableOptions(tenRandomOptions);
                } else {
                    List<String> options =
                            getItemsOfOpenedDropDownList(
                                    fieldName,
                                    dropDownListAnyItemLocator);
                    String randomOption = options.get(new Random().nextInt(options.size()));
                    clickDropDownOption(dropDownListAnyItemLocator.toString().replace("By.xpath: ", ""), randomOption);
                    dropDownResponse
                            .withSelectedValue(new Special<>(randomOption))
                            .withAvailableOptions(options);
                }
                return dropDownResponse;
            } else {
                openDropDown(
                        fieldName,
                        openDropDownButtonLocator,
                        dropDownListAnyItemLocator);
                String optionToSelect = value.toString();
                if (dropDownListAnyItemXPathWithINDEX != null) {
                    clickDropDownOption(dropDownListAnyItemXPath, optionToSelect);
                } else {
                    clickDropDownOption(dropDownListAnyItemLocator.toString().replace("By.xpath: ", ""), optionToSelect);
                }
                return dropDownResponse.withSelectedValue(value);
            }
        }
    }

    @Step("Выбираем опцию '{optionText}' в выпадающем списке.")
    private void clickDropDownOption(String xpathToOption, String optionText) {
        String optionXpath = xpathToOption + "[text()='" + optionText + "']";
        try {
            Configuration.timeout = 5000;
            click("Клик по опции '" + optionText + "'", By.xpath(optionXpath));
            return;
        } catch (Throwable e) {
            logBroken("Опция '" + optionXpath + "' не найдена (за 5с.).");
        } finally {
            Configuration.timeout = GLOBAL_TIMEOUT;
        }

        String optionXpathWithWhitespaceAtTheEnd = xpathToOption + "[text()='" + optionText + " ']";
        try {
            Configuration.timeout = 1000;
            click("Клик по опции '" + optionText + "' (добавляем пробел в конце текста опции)", By.xpath(optionXpathWithWhitespaceAtTheEnd));
            return;
        } catch (Throwable e) {
            logBroken("Опция '" + optionXpathWithWhitespaceAtTheEnd + "' не найдена (за 1с.).");
        } finally {
            Configuration.timeout = GLOBAL_TIMEOUT;
        }

        click("Клик по опции'" + optionText + "'", By.xpath(xpathToOption + "[text()='" + optionText + "']"));
    }

    // MENU

    @Step("Расрываем подменю меню '{nameOfMenuItemToBeOpened}'")
    public void openSubMenu(String nameOfMenuItemToBeOpened, By menuItemLocator, By openedSubmenuAnyItemLocator) {
        int trialsCount = 0;
        do {
            click(menuItemLocator);
            sleepMillis(100);
            if (isDisplayedWithTimeout(openedSubmenuAnyItemLocator, 0)) {
                break;
            } else
                sleepMillis(500);
            trialsCount++;
        } while (trialsCount < 10);
    }
    // HOVERING

    @Step("{actionDescription}")
    public void hover(String actionDescription, By locator) {
        $(locator).hover();
    }

    // SCROLLING

    @Step("Прокрутка к элементу  '{locator}'")
    public void scrollIntoView(By locator) {
        $(locator).scrollIntoView(true);
    }

    // CLICKING

    @Step("{actionDescription}")
    public void click(String actionDescription, By locator) {
        click(locator);
    }
    @Step("Клик по <<{locator}>>")
    public void click(By locator) {
        scrollIntoView(locator);
        logPassed("Теперь клик...");
        $(locator).click();
    }
    @Step("Клик по элементу '{elementName}'")
    public void click(String elementName, SelenideElement selenideElement) {
        selenideElement.click();
    }
    @Step("{actionDescription}")
    public void clickIgnoreException(String actionDescription, By locator) {
        clickIgnoreException(locator);
    }
    @Step("Клик по <<{locator}>> (с игнорированием исключения WebDriverException)")
    public void clickIgnoreException(By locator) {
        long startTime = System.currentTimeMillis();
        boolean isClickSuccessful = false;
        do {
            try {
                $(locator).click();
                isClickSuccessful = true;
                break;
            } catch (ElementNotVisibleException ex) {
                logBroken("При клике произошло исключение ElementNotVisibleException. Следующая попытка через 500мс.");
                sleepMillisSilent(500);
            } catch (WebDriverException ex) {
                logBroken("При клике произошло исключение WebDriverException. Следующая попытка через 500мс.");
                sleepMillisSilent(500);
            }
        } while (System.currentTimeMillis() - startTime < EXCEPTION_IGNORE_TIMEOUT);
        if (isClickSuccessful) {
            logPassed("Клик выполнен успешно");
        } else {
            Assert.fail("Клик НЕ выполнен успешно за " + EXCEPTION_IGNORE_TIMEOUT + "мс.");
        }
    }
    /** Клик с использованием класса Actions (работает и по невидимым элементам!)
     * @param actionDescription
     * @param locator
     */
    @Step("{actionDescription}")
    public void clickAction(String actionDescription, By locator) {
        clickAction(locator);
    }
    /** Клик с использованием класса Actions (работает и по невидимым элементам!)
     * @param locator
     */
    @Step("Клик по элементу {locator}")
    public void clickAction(By locator) {
        Actions action = new Actions(getWebDriver());
        action.click($(locator)).perform();
    }

    // CHECKBOXing

    @Step("Устанавливаем чекбокс '{checkBoxName}' в значение {isSelected}")
    public void setCheckBox(String checkBoxName, By inputLocator, boolean isSelected) {
        boolean isSelectedCurrently = isCheckBoxSelected(inputLocator);

        if (isSelected != isSelectedCurrently) {
            int trialsCount = 0;
            do {
                clickAction("Клик по чекбоксу", inputLocator);
                isSelectedCurrently = isCheckBoxSelected(inputLocator);
                if (isSelected == isSelectedCurrently)
                    break;
                sleepMillis(500);
                isSelectedCurrently = isCheckBoxSelected(inputLocator);
                if (isSelected == isSelectedCurrently)
                    break;
                trialsCount++;
            } while (trialsCount < 10);
            if (isSelected != isSelectedCurrently) {
                Assert.fail("Чекбокс так и не был установлен в " + isSelected);
            }
        } else {
            logPassed("Чекбокс уже в состоянии isSelected = " + isSelected);
        }
    }

    // ELEMENTS

    @Step("{actionDescription}")
    public ElementsCollection getElements(String actionDescription, By locator) {
        return $$(locator);
    }

    public ElementsCollection getElements(By locator) {
        return getElements("Получаем все элементы для локатора {" + locator + "}", locator);
    }

    /*******************************************************************************************************************
     * Проверки (с падениями)
     *******************************************************************************************************************/

    // ENABLED / DISABLED

    @Step("Проверка: элемент '{elementName}' должен быть активен (ENABLED).")
    public void checkEnabled(String elementName, By locator) {
        checkTrue(isEnabled(locator), "ENABLED", "DISABLED");
    }

    @Step("Проверка: элемент '{elementName}' НЕ должен быть активен (DISABLED).")
    public void checkDisabled(String elementName, By locator) {
        checkFalse(isEnabled(locator), "DISABLED", "ENABLED");
    }

    // DISPLAYED

    public void checkDisplayed(String actionDescription, By locator) {
        checkTrue(
                isDisplayed(actionDescription, locator),
                "Отображается",
                "Не отображается");

    }

    // APPEARING

    @Step("Проверка появления веб-элемента <<{0}>>")
    public void checkAppear(By locator) {
        $(locator).should(Condition.appear);
    }
    @Step("Проверка НЕ появления веб-элемента <<{0}>>")
    public void checkNotAppear(By locator) {
        $(locator).shouldNot(Condition.appear);
    }

    // DISAPPEARING

    /** Проверка исчезновения элемента за время в переменной timeout.
     * @param actionDescription описание шага
     * @param locator элемент
     * @param timeout таймаут ожидания исчезновения в секундах
     */
    @Step("{actionDescription} (Проверка исчезновения элемента за {timeout}мс.)")
    protected void checkElementDisappear(String actionDescription, By locator, long timeout) {
        long startTime = System.currentTimeMillis();
        do {
            if (doesExist(locator)) {
                if (isDisplayed(locator)) {
                    logPassed("Элемент отображается (следующая проверка через 500мс.)");
                } else {
                    logScreenshot("Элемент не отображается.");
                    return;
                }
            } else {
                logScreenshot("Элемент не отображается.");
                return;
            }
            sleepMillisSilent(500);
        } while (System.currentTimeMillis() - startTime < timeout);
        Assert.fail("Элемент не исчез за " + timeout + "мс.");
    }

    // COUNT

    /** Проверка количества элементов соответствующих локатору
     * @param locator
     * @param expectedCount
     * @param message - предполагается формат: Проверка количества [пунктов в меню 'Разделы']
     */
    @Step("{2}")
    public void checkItemsCount(By locator, int expectedCount, String message) {
        int itemsCount = $$(locator).size();
        Assert.assertEquals(itemsCount, expectedCount, message);
        logPassed("PASSED");
    }


    /*******************************************************************************************************************
     * Получение информации о состояниях (без падений)
     *******************************************************************************************************************/

    // DISPLAYED

    @Step("{0}")
    public boolean isDisplayed(String actionDescription, By locator) {
        return isDisplayed(locator);
    }
    @Step("Проверка отображения элемента <<{0}>>")
    public boolean isDisplayed(By locator) {
        boolean isDisplayed = isDisplayedWithTimeout(locator, 0);
        logPassed("isDisplayed = " + isDisplayed);
        return isDisplayed;
    }
    @Step("Проверка отображения элемента <<{0}>> (со стандартным ожиданием)")
    public boolean isDisplayedWithTimeout(By locator) {
        return isDisplayedWithTimeout(locator, GLOBAL_TIMEOUT);
    }
    @Step("{actionDescription} (ожидание {timeout}мс.)")
    public boolean isDisplayedWithTimeout(String actionDescription, By locator, long timeout) {
        return isDisplayedWithTimeout(locator, timeout);
    }
    @Step("Проверка отображения элемента <<{locator}>> (с ожиданием {timeout}мс.)")
    public boolean isDisplayedWithTimeout(By locator, long timeout) {
        boolean isDisplayed = false;
        long startTime = System.currentTimeMillis();
        do {
            // hope this method won't break anything, but I need it for handling very similar drop-down elements
            isDisplayed = isAtLeastOneDisplayedWithTimeout(locator, 0);
            if (!isDisplayed) {
                logScreenshot("Элемент не отображается. Повторная проверка через 100мс.");
                sleepMillisSilent(100);
            } else {
                break;
            }
        } while (System.currentTimeMillis() - startTime < timeout);
        logPassed("isDisplayed = " + isDisplayed);
        return isDisplayed;
    }

    @Step("Хотя бы один из элементов по локатору отображается?")
    public boolean isAtLeastOneDisplayedWithTimeout(By locator, long timeout) {
        if (!doesExist(locator, timeout)) {
            logPassed("Элемент даже не существует, поэтому нет смысла проверять его видимость");
            return false;
        }
        if ($(locator).isDisplayed()) {
            logPassed("Отображается.");
            return true;
        }
        boolean isDisplayed = false;
        long startTime = System.currentTimeMillis();
        do {
            ElementsCollection elements = $$(locator);
            logPassed("elements count = " + elements.size());
            int i = 0;
            for (SelenideElement element : elements) {
                isDisplayed = element.isDisplayed();
                if (!isDisplayed) {
                    logPassed("Элемент #" + i + " не отображается.");
                } else {
                    break;
                }
            }
            if (isDisplayed) {
                break;
            } else {
                logPassed("Повторная проверка через 200мс.");
                sleepMillisSilent(200);
            }
        } while (System.currentTimeMillis() - startTime < timeout);
        logPassed("isDisplayed = " + isDisplayed);
        return isDisplayed;
    }

    // ENABLED + testing git

    @Step("{actionDescription}")
    public boolean isEnabled(String actionDescription, By locator) {
        return isEnabled(locator);
    }
    @Step("Проверка активности элемента <<{locator}>>")
    public boolean isEnabled(By locator) {
        boolean isEnabled = $(locator).isEnabled();
        logPassed("isEnabled = " + isEnabled);
        return isEnabled;
    }
    /** Проверка активности для элементов фреймворка Сенча (особое внимание на локатор!)
     * @param actionDescription описание действия
     * @param locator локатор должен определять элемент <a> (или любой другой, на котором появляются классы x-item-disabled или x-btn-disabled)
     * @return
     */
    @Step("{actionDescription}")
    public boolean isEnabledBySencha(String actionDescription, By locator) {
        return isEnabledBySencha(locator);
    }
    /** Проверка активности для элементов фреймворка Сенча (особое внимание на локатор!)
     * @param locator - локатор должен определять элемент <a> (или любой другой, на котором появляются классы x-item-disabled или x-btn-disabled)
     * @return
     */
    @Step("Проверка активности элемента <<{locator}>> (по классам сенчи)")
    public boolean isEnabledBySencha(By locator) {
        sleepMillis(1000, "К сожалению, классы появляются/исчезают с запазданием :( И от этого ожидания не уйти :( Sorry, guys & girls...");
        checkTrue(doesExist(locator),
                "Элемент существует",
                "Элемента НЕТ, нечего и проверять его активность");
        checkTrue(isDisplayed(locator),
                "Элемент видим",
                "Элемент НЕвидим, нечего и проверять его активность");
        String classValue = getAttribute(locator, "class", locator.toString());
        if (classValue.contains("x-item-disabled")) {
            logPassed("Элемент содержит класс 'x-item-disabled'");
            return false;
        } else if (classValue.contains("x-btn-disabled")) {
            logPassed("Элемент содержит класс 'x-btn-disabled'");
            return false;
        } else {
            logPassed("Элемент НЕ содержит классы 'x-item-disabled' или 'x-btn-disabled'");
            return true;
        }
    }
    @Step("Проверка активности элемента <<{locator}>> (по классам сенчи) с ожиданием {timeout}мс.")
    public boolean isEnabledBySenchaWithTimeout(By locator, long timeout) {
        long startTime = System.currentTimeMillis();
        do {
            if (isEnabledBySencha(locator)) {
                return true;
            } else {
                logBroken("Возможно ещё не исчезли классы-disabled сенчи. Повторная проверка через 500мс.");
                sleepMillisSilent(500);
            }
        } while (System.currentTimeMillis() - startTime < timeout);
        logPassed("Классы-disabled сенчи не исчезли за " + timeout + "мс., элемент заблокирован.");
        return false;
    }

    // APPEAR

    @Step("{actionDescription}")
    public SelenideElement waitElementAppear(String actionDescription, By locator) {
        return waitElementAppear(locator);
    }
    @Step("Ожидаем появления элемента <<{locator}>>")
    public SelenideElement waitElementAppear(By locator) {
        if (isDisplayedWithTimeout(locator, GLOBAL_TIMEOUT)) {
            return $(locator);
        } else {
            Assert.fail("Элемент <<" + locator + ">> не был найден за " + GLOBAL_TIMEOUT + "мс.");
            return null;
        }
    }

    // DISAPPEARE

    @Step("{actionDescription}")
    public boolean hasDisappeared(String actionDescription, By locator) {
        return hasDisappeared(locator);
    }
    @Step("Проверка исчезновения элемента <<{locator}>>")
    public boolean hasDisappeared(By locator) {
        long startTime = System.currentTimeMillis();
        do {
            if (!isDisplayed("Проверка наличия элемента <<" + locator + ">> (Раз в 500мс.)", locator)) {
                return true;
            }
            sleepMillisSilent(500);
        } while (System.currentTimeMillis() - startTime < GLOBAL_TIMEOUT);
        return false;
    }

    // DOES EXIST

    @Step("Существует ли элемент <<{locator}>> (без ожидания)")
    public boolean doesExist(By locator) {
        return doesExist(locator, 0);
    }
    @Step("Существует ли элемент <<{locator}>> (ожидание появления {timeout}мс.)")
    public boolean doesExist(By locator, long timeout) {
        Configuration.timeout = timeout;
        try {
            $(locator).getTagName();
        } catch (AssertionError ex) {
            logBroken(ex.getMessage());
            Configuration.timeout = GLOBAL_TIMEOUT;
            logPassed("Элемент НЕ существует");
            return false;
        }
        Configuration.timeout = GLOBAL_TIMEOUT;
        logPassed("Элемент существует");
        return true;
    }
    /** Есть ли дочерний элемент в родительском? (время ожидание 0с.)
     * @param selenideElement
     * @param locatorOfSubElement локатор дочернего элемента должен начинаться от родителя и в виде: "./tag/tag/etc..."
     * @return
     */
    protected boolean doesExistInsideElement(SelenideElement selenideElement, By locatorOfSubElement) {
        return doesExistInsideElement(selenideElement, locatorOfSubElement, 0);
    }
    /** Есть ли дочерний элемент в родительском? (время ожидание по параметру)
     * @param selenideElement
     * @param locatorOfSubElement локатор дочернего элемента должен начинаться от родителя и в виде: "./tag/tag/etc..."
     * @param timeout таймаут в миллисекундах
     * @return
     */
    protected boolean doesExistInsideElement(SelenideElement selenideElement, By locatorOfSubElement, long timeout) {
        Configuration.timeout = 0;
        long startTime = System.currentTimeMillis();
        do {
            boolean isFound = false;
            try {
                selenideElement.findElement(locatorOfSubElement);
                isFound = true;
            } catch (NoSuchElementException ex) {
                isFound = false;
            } catch (AssertionError ex) {
                isFound = false;
            }
            if (isFound) {
                logPassed("Дочерний элемент найден");
                Configuration.timeout = GLOBAL_TIMEOUT;
                return true;
            }
        } while (System.currentTimeMillis() - startTime < timeout);
        Configuration.timeout = GLOBAL_TIMEOUT;
        logPassed("Дочерний элемент НЕ найден");
        return false;
    }

    // CHECKBOX

    @Step("Чекбокс выбран?")
    public boolean isCheckBoxSelected(By locator) {
        boolean isSelected = $(locator).isSelected();
        logPassed("isSelected = " + isSelected);
        return isSelected;
    }

    // ATTRIBUTE

    /** Получение значения аттрибута веб-элемента
     * @param locator локатор веб-элемента, аттрибут которого получаем
     * @param attributeName
     * @param webElementName произвольное название веб-елемента (участвует только в логировании)
     * @return
     */
    @Step("Получаем значение атрибута '{attributeName}' элемента '{webElementName}'")
    public String getAttribute(By locator, String attributeName, String webElementName) {
        String attributeText = $(locator).getAttribute(attributeName);
        logPassed("attribute text: " + attributeText);
        return attributeText;
    }


    // DUPLICATES

    /** Поиск веб-элемента, который имеет невидимые дубликаты по тому же локатору. Возвращает видимый веб-элемент.
     * ВАЖНО: Требует обязательное наличие одного и только одного видимого элемента по этому локатору!
     * @author Gerasimenko I.S.
     * @param locator
     * @param elementName
     * @return - видимый веб-элемент по заданному локатору
     */
    protected SelenideElement findElementHavingInvisibleDuplicates(By locator, String elementName, long timeout) {
        SelenideElement singleVisibleElement = null;
        List<SelenideElement> elements = null;
        long start = System.currentTimeMillis();
        int visibleElementsCount = 0;
        do {
            elements = $$(locator);
            visibleElementsCount = 0;
            for (SelenideElement element : elements) {
                if (element.isDisplayed()) {
                    singleVisibleElement = element;
                    visibleElementsCount++;
                }
            }
        } while (System.currentTimeMillis() - start < timeout && visibleElementsCount != 1);
        if (visibleElementsCount == 0) {
            Assert.fail("Не найден ни один элемент '" + elementName + "'");
        } else if (visibleElementsCount > 1) {
            Assert.fail("Найдено более одного видимого элемента '" + elementName + "'");
        }
        return singleVisibleElement;
    }

    protected SelenideElement findElementHavingInvisibleDuplicates(By locator, String elementName) {
        return findElementHavingInvisibleDuplicates(locator, elementName, 0);
    }

    protected SelenideElement findElementHavingInvisibleDuplicates(By locator, long timeout) {
        return findElementHavingInvisibleDuplicates(locator, locator.toString(), timeout);
    }

    protected SelenideElement findElementHavingInvisibleDuplicates(By locator) {
        return findElementHavingInvisibleDuplicates(locator, locator.toString());
    }

    @Step("Поиск элемента '{elementName}' (с возможными невидимыми дублями)")
    protected SelenideElement findElementHavingInvisibleDuplicatesNoErrorOnNoElements(String elementName, By locator) {
        SelenideElement singleVisibleElement = null;
        List<SelenideElement> elements = $$(locator);
        int visibleElementsCount = 0;
        for (SelenideElement element : elements) {
            if (element.isDisplayed()) {
                singleVisibleElement = element;
                visibleElementsCount++;
            }
        }
        if (visibleElementsCount == 0) {
            logSkipped("Не найден ни один элемент '" + elementName + "'");
            singleVisibleElement = null;
        } else if (visibleElementsCount > 1) {
            Assert.fail("Найдено более одного видимого элемента '" + elementName + "'");
        }
        return singleVisibleElement;
    }


    /*******************************************************************************************************************
     * DEVELOPMENT / DEBUGGING
     *******************************************************************************************************************/

    /*public ListNode getNodesTree(By parentLocator, int maxDepth) {
        int depth = 0;
        ListNode parentNode = new ListNode();
        addChildNotes()
        do {

            depth++;
        } while (depth < maxDepth);
        return parentNode;
    }*/


    public ListNode getNodesTreeUpstairs (String childXPathStartingFromItsTag, int maxDepthUp) {
        ListNode child = new ListNode();
        setNodeParams(child, By.xpath("//" + childXPathStartingFromItsTag));
        int depth = 0;
        ListNode parent = new ListNode();
        do {
            parent = new ListNode();
            SelenideElement parentSelenideElement = $(By.xpath("//*[" + childXPathStartingFromItsTag + "]"));
            parent.tagName = parentSelenideElement.getTagName();
            parent.id = parentSelenideElement.getAttribute("id");
            parent.classes = parentSelenideElement.getAttribute("class");
            parent.childNodes = new ArrayList<>();
            parent.childNodes.add(child);
            childXPathStartingFromItsTag = parent.tagName + "/" + childXPathStartingFromItsTag;
            child = parent;

            depth++;
        } while (depth < maxDepthUp);
        return child;
    }

    private void setNodeParams(ListNode listNode, By locator) {
        SelenideElement selenideElement = $(locator);
        listNode.tagName = selenideElement.getTagName();
        listNode.id = selenideElement.getAttribute("id");
        listNode.classes = selenideElement.getAttribute("class");
    }
}
