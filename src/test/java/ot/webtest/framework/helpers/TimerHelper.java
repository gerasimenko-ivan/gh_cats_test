package ot.webtest.framework.helpers;

import io.qameta.allure.Step;

import static java.lang.Thread.sleep;
import static ot.webtest.framework.helpers.AllureHelper.logException;

public class TimerHelper {
    /** Пауза в выполнении (безусловная)
     * @author Gerasimenko I.S.
     * @param millis - время (мс.)
     */
    @Step("Пауза в выполнении {0}мс.")
    public static void sleepMillis(int millis) {
        sleepMillisSilent(millis);
    }

    /** Пауза в выполнении (безусловная) с комментарием
     * @author Gerasimenko I.S.
     * @param millis - время (мс.)
     */
    @Step("Пауза {0}мс. {1}")
    public static void sleepMillis(int millis, String comment) {
        sleepMillisSilent(millis);
    }

    /** Пауза в выполнении (безусловная)
     * Без логирования в аллюр
     * @author Gerasimenko I.S.
     * @param millis - время (мс.)
     */
    public static void sleepMillisSilent(int millis) {
        // TODO: is it best practice?
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            logException(e);
            // set the interrupt flag of the thread, so higher level interrupt handlers will notice it and can handle it appropriately
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        // terminate and quit the operation
        return;
    }
}
