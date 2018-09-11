package ot.webtest.framework.kketshelpers;

import io.qameta.allure.Step;
import ot.webtest.dataobject.Special;
import ot.webtest.dataobject.SpecialDateTime;
import ot.webtest.framework.helpers.RandomDataGenerator;
import ot.webtest.framework.kketshelpers.dataobjects.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ot.webtest.framework.helpers.AllureHelper.logBroken;
import static ot.webtest.framework.helpers.AllureHelper.logPassed;

public class TestDataGenerator {

    @Step("Данные для теста: децентрализованное задание с жёстко прописанными Тех.операцией, Элементом, ТС, Подразделением, Статусом платёжного листа.")
    public DecentralizedTask getDecentralizedTaskItemWithSomeHardcodedFieldValuesForTaskCreation() {
        logPassed("Используем только статус платёжного листа '" + WaybillStatus.ADD_TO_SKETCH_OF_WAIBILL + "' и жёстко прописанные многие прочие данные, т.к. (сейчас) нет возможности определять есть ли у ТС активный ПЛ или его черновик");

        RandomDataGenerator rnd = new RandomDataGenerator();
        // Время выполнения задания для ОДХ должно составлять не более 5 часов
        int hourStart = rnd.nextInt(24);
        int minuteStart = rnd.nextInt(hourStart == 23 ? 11 : 12) * 5;
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

        DecentralizedTask decentralizedTask =
                (DecentralizedTask) new DecentralizedTask()
                        //.withVehicle(Special.RANDOM())                                // HARDCODED
                        //.withWaybillStatus(WaybillStatus.getRandom())                 // HARDCODED
                        .withIsTaskForColumn(false)         // for column - another test-case
                        //.withTechnologicalOperation(Special.RANDOM())                 // HARDCODED
                        //.withElement(Special.RANDOM())                                // HARDCODED
                        .withRouteName(Special.RANDOM())
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
                .withVehicle(new Special<>("В097ВВ777 [мкду-1/КАМАЗ-65115/ДКМ (ПМ+ЖР)]"))
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
        // 10.09.2018: change to moro simply formula of minute generation (let's check where it will fail. Or not))
        int minuteStart = rnd.nextInt(12) * 5; //(rnd.nextInt(hourStart == 23 ? 58 : 59) * 5) % 60;
        int hourEnd = rnd.nextInt(24);
        int minuteEnd = rnd.nextInt(12) * 5; //(rnd.nextInt(hourStart == 23 ? 58 : 59) * 5) % 60;
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

    @Step("Данные для теста: успешное создание карточки сотрудника (водителя/машиниста).")
    public static Employee getEmployeeForCreationOfDriverOrMechanistCard() {
        RandomDataGenerator rnd = new RandomDataGenerator();
        List<Special<String>> secondaryVehicles = new ArrayList<>();
        for (int i = 0; i < 1 + rnd.nextInt(5); i++) {
            secondaryVehicles.add(Special.RANDOM());  // 1-5 vehicles
        }
        Employee employee =
                new Employee()
                        .withSurname(rnd.getCyrillicWordWithLeadingUpperCase(2 + rnd.nextInt(19)))
                        .withName(rnd.getCyrillicWordWithLeadingUpperCase(2 + rnd.nextInt(19)))
                        .withPersonnelNumber((1 + rnd.nextInt(9)) + rnd.getNumbers(14))     // can start with zeroes!!! can have length 15+ ???? BUG BUG BUG!!!
                        .withPosition(rnd.getBooleanNoNull() ? new Special<>("водитель") : new Special<>("машинист"))
                        .withMainVehicle(Special.RANDOM())
                        .withSecondaryVehicles(secondaryVehicles);
        int licenseSet = rnd.nextInt(3);
        if (licenseSet == 0 || licenseSet == 2) {
            employee
                    .withSpecialLicenseId(rnd.getCyrillicAndNumber(5 + rnd.nextInt(10)) + " " + rnd.getCyrillicAndNumber(5 + rnd.nextInt(10)))
                    .withSpecialLicenseExpireDate(rnd.getDate(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4000)));
        }
        if (licenseSet == 1 || licenseSet == 2) {
            employee
                    .withDriverLicenseId(rnd.getDriverLicenseNumberRUS())
                    .withDriverLicenseExpireDate(rnd.getDate(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4000)));
        }
        return employee;
    }

    @Step("Данные для теста: успешное создание карточки сотрудника (НЕ водителя/машиниста).")
    public static Employee getEmployeeNoVehicles() {
        RandomDataGenerator rnd = new RandomDataGenerator();
        Employee employee =
                new Employee()
                        .withSurname(rnd.getCyrillicWordWithLeadingUpperCase(2 + rnd.nextInt(19)))
                        .withName(rnd.getCyrillicWordWithLeadingUpperCase(2 + rnd.nextInt(19)))
                        .withPersonnelNumber((1 + rnd.nextInt(9)) + rnd.getNumbers(14))     // can start with zeroes!!! BUG BUG BUG!!!
                        .withPosition(Special.RANDOM());
        int licenseSet = rnd.nextInt(3);
        if (licenseSet == 0 || licenseSet == 2) {
            employee
                    .withSpecialLicenseId(rnd.getCyrillicAndNumber(5 + rnd.nextInt(10)) + " " + rnd.getCyrillicAndNumber(5 + rnd.nextInt(10)))
                    .withSpecialLicenseExpireDate(rnd.getDate(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4000)));
        }
        if (licenseSet == 1 || licenseSet == 2) {
            employee
                    .withDriverLicenseId(rnd.getDriverLicenseNumberRUS())
                    .withDriverLicenseExpireDate(rnd.getDate(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4000)));
        }
        return employee;
    }

    @Step("Данные для теста: Создание Путевого листа.")
    public static Waybill getWaybill() {
        Waybill waybill;
        RandomDataGenerator rnd = new RandomDataGenerator();
        logBroken("dateLeavePlanned: нужно получать от даты возврата ТС (Статус ПЛ - Активный, но возможны варианты)");
        SpecialDateTime dateLeavePlanned =
                new SpecialDateTime(LocalDate.now().plusDays(1000 + rnd.nextInt(1000)))
                        .withHour(rnd.nextInt(24))
                        .withMinute(rnd.nextInt(11) * 5);   // MAX = 50, as decentralizedTask.dateStart + 5 min!!!
        SpecialDateTime dateEnd =
                new SpecialDateTime(dateLeavePlanned.date.plusDays(1))
                        .withHour(rnd.nextInt(24))
                        .withMinute(rnd.nextInt(12) * 5);
        waybill =
                new Waybill()
                        .withDateLeavePlanned(dateLeavePlanned)
                        .withDateReturnPlanned(dateEnd)
                        // hardcoded vehicle... it should have GLONASS & Task...
                        .withVehicle(new Special<>("1488НВ77 [МПУ-1М/МПУ-1М/ДКМ (ПУ+ПЩ)]"))   //  "1485НВ77 [МПУ-1М/МПУ-1М/ДКМ (ПМ+ПЩ)]"
                        .withDriver(Special.RANDOM());

        SpecialDateTime dateStart =
                new SpecialDateTime(dateLeavePlanned.date)
                        .withHour(dateLeavePlanned.hour)
                        .withMinute(dateLeavePlanned.minute + 5);
        //dateReturnPlanned.date = dateReturnPlanned.date.plusDays(-1);
        DecentralizedTask decentralizedTask =
                (DecentralizedTask) new DecentralizedTask()
                        .withDateStart(dateStart)
                        .withDateEnd(dateEnd)
                        .withRouteName(Special.RANDOM());
        decentralizedTask
                .withTechnologicalOperation(new Special<>("Сплошное подметание"))
                .withElement(new Special<>("Проезжая часть"));

        waybill.withDecentralizedTaskToCreate(decentralizedTask);
        return waybill;
    }
}
