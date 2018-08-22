package ot.webtest.framework.helpers;

import io.qameta.allure.Step;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import static ot.webtest.framework.helpers.AllureHelper.logException;

public class RobotHelper {
    public void typeString(String text) throws Exception {
        char[] chars = text.toCharArray();
        for (char c : chars) {
            int ascii = (int) c;
            // handling cyrillic symbols
            if (1040 <= ascii && ascii <= 1071) {   // A-Я, без Ё
                ascii = ascii - (1040 - 128);
            } else if (1072 <= ascii && ascii <= 1087) {    // а-п
                ascii = ascii - (1072 - 160);
            } else if (1088 <= ascii && ascii <= 1103) {    // р-я
                ascii = ascii - (1088 - 224);
            } else if (ascii == 1025) {  // Ё
                ascii = 240;
            } else if (ascii == 1105) {  // ё
                ascii = 241;
            }
            // 128 = A, 159 = Я, 160 = а, 175 = п // 224 = р, 239 = я
            // 240 = Ё, 241 = ё
            System.out.println(ascii + " : " + c);
            typeByAsciiCode(ascii);
        }
    }

    public void typeByAsciiCode(int code) throws Exception {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            logException(e);
            e.printStackTrace();
        }
        LinkedList<Integer> stack = new LinkedList<>();
        while (code > 0) {
            stack.push( code % 10 );
            code = code / 10;
        }

        r.keyRelease(KeyEvent.VK_ALT);
        r.keyPress(KeyEvent.VK_ALT);
        while (!stack.isEmpty()) {
            typeNumpad(stack.pop());
        }
        r.keyRelease(KeyEvent.VK_ALT);
    }

    private void typeNumpad(int index) throws Exception {
        if (index < 0 || index > 9) {
            throw new Exception("Метод typeNumpad может принимать значение от 0 до 9, но было получено значение: " + index);
        }
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            logException(e);
            e.printStackTrace();
        }
        switch (index) {
            case 0:
                r.keyPress(KeyEvent.VK_NUMPAD0);
                r.keyRelease(KeyEvent.VK_NUMPAD0);
                break;
            case 1:
                r.keyPress(KeyEvent.VK_NUMPAD1);
                r.keyRelease(KeyEvent.VK_NUMPAD1);
                break;
            case 2:
                r.keyPress(KeyEvent.VK_NUMPAD2);
                r.keyRelease(KeyEvent.VK_NUMPAD2);
                break;
            case 3:
                r.keyPress(KeyEvent.VK_NUMPAD3);
                r.keyRelease(KeyEvent.VK_NUMPAD3);
                break;
            case 4:
                r.keyPress(KeyEvent.VK_NUMPAD4);
                r.keyRelease(KeyEvent.VK_NUMPAD4);
                break;
            case 5:
                r.keyPress(KeyEvent.VK_NUMPAD5);
                r.keyRelease(KeyEvent.VK_NUMPAD5);
                break;
            case 6:
                r.keyPress(KeyEvent.VK_NUMPAD6);
                r.keyRelease(KeyEvent.VK_NUMPAD6);
                break;
            case 7:
                r.keyPress(KeyEvent.VK_NUMPAD7);
                r.keyRelease(KeyEvent.VK_NUMPAD7);
                break;
            case 8:
                r.keyPress(KeyEvent.VK_NUMPAD8);
                r.keyRelease(KeyEvent.VK_NUMPAD8);
                break;
            case 9:
                r.keyPress(KeyEvent.VK_NUMPAD9);
                r.keyRelease(KeyEvent.VK_NUMPAD9);
                break;
        }
    }

    private void pressButton(int keyEvent) {
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            logException(e);
            e.printStackTrace();
        }
        r.keyPress(keyEvent);
        r.keyRelease(KeyEvent.VK_F1);
    }
    @Step("Нажимаем на клавишу <F1>")
    public void pressF1() {
        pressButton(KeyEvent.VK_F1);
    }
    @Step("Нажимаем на клавишу <F5>")
    public void pressF5() {
        pressButton(KeyEvent.VK_F5);
    }
    @Step("Нажимаем на клавишу <ENTER>")
    public void pressEnter() {
        pressButton(KeyEvent.VK_ENTER);
    }
    @Step("Нажимаем на клавишу <Tab>")
    public void pressTab() {
        pressButton(KeyEvent.VK_TAB);
    }
}
