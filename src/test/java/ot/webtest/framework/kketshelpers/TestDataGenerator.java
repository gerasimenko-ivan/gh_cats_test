package ot.webtest.framework.kketshelpers;

import io.qameta.allure.Step;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.DecentralizedTask;
import ot.webtest.framework.kketshelpers.dataobjects.TaskSource;
import ot.webtest.framework.kketshelpers.dataobjects.WaybillStatus;
import ot.webtest.framework.kketshelpers.dataobjects.WorkOrderTask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ot.webtest.framework.helpers.AllureHelper.logFailed;
import static ot.webtest.framework.helpers.AllureHelper.logPassed;

public class TestDataGenerator {

    @Step("Данные для теста: децентрализованное задание с жёстко прописанными Тех.операцией, Элементом, ТС, Подразделением, Статусом платёжного листа.")
    public DecentralizedTask getDecentralizedTaskItemWithSomeHardcodedFieldValuesForTaskCreation() {
        logPassed("Используем только статус платёжного листа '" + WaybillStatus.ADD_TO_SKETCH_OF_WAIBILL + "' и жёстко прописанные многие прочие данные, т.к. (сейчас) нет возможности определять есть ли у ТС активный ПЛ или его черновик");

        RandomDataGenerator rnd = new RandomDataGenerator();
        // Время выполнения задания для ОДХ должно составлять не более 5 часов
        int hourStart = rnd.nextInt(24);
        int minuteStart = (rnd.nextInt(hourStart == 23 ? 59 : 60) * 5) % 60;
        int minutesWithDeltaLessOrEqualTo300 = (hourStart*60 + minuteStart) + 5 + (rnd.nextInt(295/5)*5); // plus delta from 5 to 300 minutes
        int hourEnd, minuteEnd;
        if (minutesWithDeltaLessOrEqualTo300 >= (23*60 + 55)) {
            hourEnd = 23;
            minuteEnd = 55;
        } else {
            hourEnd = minutesWithDeltaLessOrEqualTo300 / 60;
            minuteEnd = minutesWithDeltaLessOrEqualTo300 % 60;
        }
        SpecialDateTime dateStartRandom =
                rnd.getSpecialDateTime(LocalDate.now().plusDays(-28), LocalDate.now().plusDays(1000))
                        .withHour(hourStart).withMinute(minuteStart);
        SpecialDateTime dateEndRandom =
                new SpecialDateTime(dateStartRandom.date)
                        .withHour(hourEnd).withMinute(minuteEnd);

        logFailed("Приходится прописывать жёсткий, а не случайный Маршрут, т.к. есть маршрут с дублями пробелов '1-2   ПМ   ПРАВИЛЬНЫЙ , Проезжая часть', а он валит Селенид");
        DecentralizedTask decentralizedTask =
                (DecentralizedTask) new DecentralizedTask()
                        //.withVehicle(Special.RANDOM())                                // HARDCODED
                        //.withWaybillStatus(WaybillStatus.getRandom())                 // HARDCODED
                        .withIsTaskForColumn(false)         // for column - another test-case
                        //.withTechnologicalOperation(Special.RANDOM())                 // HARDCODED
                        //.withElement(Special.RANDOM())                                // HARDCODED
                        //.withRouteName(Special.RANDOM())
                        .withRouteName(new Special<>("МАРШРУТ №1 ОДХ Мойка проезжей части, Проезжая часть"))
                        .withDateStart(dateStartRandom)
                        .withDateEnd(dateEndRandom)
                        .withTaskSource(TaskSource.getRandomWithoutFACKSOGRAM())
                        .withComment(rnd.getCyrillicWords(5, 15, rnd.nextInt(5)+1));
        //.withSubdivision(Special.RANDOM());                           // HARDCODED
        //.withSubdivision(Special.RANDOM());                           // HARDCODED
        // HARDCODED - because random values (without DB info) gives too many branching of execution
        decentralizedTask
                .withPassesCount(rnd.nextInt(10) + 1)
                .withWaybillStatus(WaybillStatus.ADD_TO_SKETCH_OF_WAIBILL)
                .withVehicle(new Special<>("В097ВВ777 [мкду-1/КАМАЗ-65115/ДКМ (ПМ+ПЩ+ЖР)]"))
                .withTechnologicalOperation(new Special<>("Сплошная мойка"))
                .withElement(new Special<>("Проезжая часть"))
                .withSubdivision(new Special<>("ДЭУ.ОДХ"));
        /** Почему статус ПЛ только "Добавить в черновик ПЛ":
         * Чтобы статуст ПЛ был "Создать черновик ПЛ" на ТС не дожно быть уже созданных ПЛ - это без доступа к БД не обеспечить
         * Если найти ТС с активным ПЛ и выбрать статус ПЛ "Добавить в активный ПЛ" возможно -- "Временные рамки задания не соответствуют временным рамкам ПЛ #11 (06.08.2018 13:00 - 07.08.2018 08:59)"
         */return decentralizedTask;
    }

    @Step("Данные для теста: наряд-задание с жёстко прописанными Тех.операцией, Элементом, Подразделением.")
    public static WorkOrderTask getWorkOrderTaskWithSomeHardcodedFieldValuesForTaskCreation() {
        RandomDataGenerator rnd = new RandomDataGenerator();
        int hourStart = rnd.nextInt(24);
        int minuteStart = (rnd.nextInt(hourStart == 23 ? 59 : 60) * 5) % 60;
        int hourEnd = rnd.nextInt(24);
        int minuteEnd = (rnd.nextInt(hourStart == 23 ? 59 : 60) * 5) % 60;
        SpecialDateTime dateStartRandom =
                rnd.getSpecialDateTime(LocalDate.now().plusDays(-28), LocalDate.now().plusDays(1000))
                        .withHour(hourStart).withMinute(minuteStart);
        SpecialDateTime dateEndRandom =
                rnd.getSpecialDateTime(dateStartRandom.date.plusDays(1), LocalDate.now().plusDays(1001))
                        .withHour(hourEnd).withMinute(minuteEnd);
        List<Special<String>> brigade = new ArrayList<>();
        for (int i = 0; i < 1 + rnd.nextInt(5); i++) {
            brigade.add(Special.RANDOM());  // 1-5 people
        }
        // Маршрут зависит от Элемента и Подразделения
        // В наряд-задание можно добавить только активного на данный момент времени сотрудника!!! - for 'Витова Наталья Владимировна'
        return (WorkOrderTask) new WorkOrderTask()
                .withBrigadier(Special.RANDOM())
                .withBrigade(brigade)
                .withTechnologicalOperation(new Special<>("Покос"))
                .withElement(new Special<>("Обочины"))
                .withSubdivision(new Special<>("ДЭУ.ОДХ"))
                .withDateStart(dateStartRandom)
                .withDateEnd(dateEndRandom)
                .withTaskSource(TaskSource.getRandomWithoutFACKSOGRAM())
                .withComment(rnd.getCyrillicWords(5, 15, rnd.nextInt(5)+1))
                .withRouteName(Special.RANDOM());
    }
}
