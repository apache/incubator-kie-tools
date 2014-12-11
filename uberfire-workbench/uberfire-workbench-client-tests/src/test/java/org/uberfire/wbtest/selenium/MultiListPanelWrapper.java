package org.uberfire.wbtest.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Selenium Page Object for the UberFire MultiListWorkbenchPanelView.
 */
public class MultiListPanelWrapper extends AbstractWorkbenchPanelWrapper {

    public MultiListPanelWrapper( WebDriver driver,
                                  String panelId ) {
        super( driver, panelId );
    }

    // TODO methods for listing parts and switching parts, checking if panel is focused, etc.

}
