package ot.webtest.framework.kketshelpers.dashboard;

import org.openqa.selenium.By;
import ot.webtest.framework.HelperBase;
import ot.webtest.framework.kketshelpers.dataobjects.Route;

public class RouteCreationHelper extends HelperBase {

    public Route createAndSaveRouteFromTask(Route route) {
        Route newRoute = new Route();
        click("Нажимаем кнопку 'Создать новый'", By.xpath("//button[@id='create-route' and text()='Создать новый']"));


        return newRoute;
    }
}
