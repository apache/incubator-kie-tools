package org.uberfire.wbtest.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Selenium Page Object for the UberFire SimpleWorkbenchPanelView.
 */
public class SimplePanelWrapper extends AbstractWorkbenchPanelWrapper {

    public SimplePanelWrapper( WebDriver driver,
                               String panelId ) {
        super( driver,
               panelId );
    }
}
