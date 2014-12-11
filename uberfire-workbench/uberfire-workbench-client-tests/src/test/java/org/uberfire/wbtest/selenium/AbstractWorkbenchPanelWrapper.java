package org.uberfire.wbtest.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Base class for Selenium Page Objects that wrap UberFire workbench panels.
 */
public abstract class AbstractWorkbenchPanelWrapper {

    protected WebDriver driver;
    protected String panelId;

    public AbstractWorkbenchPanelWrapper( WebDriver driver,
                                          String panelId ) {
        this.driver = PortablePreconditions.checkNotNull( "driver", driver );
        this.panelId = PortablePreconditions.checkNotNull( "panelId", panelId );
    }

    /**
     * Clicks the maximize toggle button on the panel that this wrapper wraps.
     */
    public void clickMaximizeButton() {
        WebElement button = driver.findElement( By.id( "gwt-debug-" + panelId + "-maximizeButton" ) );
        button.click();
    }

    public boolean isObscuredBy( AbstractWorkbenchPanelWrapper bigger ) {
        Dimension mySize = getSize();
        Point myPos = getLocation();
        Dimension biggerSize = bigger.getSize();
        Point biggerPos = bigger.getLocation();

        return biggerPos.x <= myPos.x &&
                biggerPos.y <= myPos.y &&
                biggerSize.width >= mySize.width &&
                biggerSize.height >= mySize.height;
    }

    public Dimension getSize() {
        return driver.findElement( By.id( panelId ) ).getSize();
    }

    public Point getLocation() {
        return driver.findElement( By.id( panelId ) ).getLocation();
    }
}
