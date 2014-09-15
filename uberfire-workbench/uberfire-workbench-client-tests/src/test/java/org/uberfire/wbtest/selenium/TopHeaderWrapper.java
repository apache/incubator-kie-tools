package org.uberfire.wbtest.selenium;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;

/**
 * A Selenium "page object" for the TopHeader, which contains facilities for adding child panels to the root panel.
 */
public class TopHeaderWrapper {

    private final WebDriver driver;

    public TopHeaderWrapper( WebDriver driver ) {
        this.driver = checkNotNull( "driver", driver );
    }

    public void addPanelToRoot( CompassPosition position,
                                Class<? extends WorkbenchPanelPresenter> panelType,
                                Class<? extends WorkbenchScreenActivity> screen,
                                String... params ) {

        WebElement placeIdBox = driver.findElement( By.id( "newPanelPartPlace" ) );
        placeIdBox.clear();
        StringBuilder placeWithParams = new StringBuilder();
        placeWithParams.append( screen.getName() );
        for ( int i = 0; i < params.length; i += 2 ) {
            if ( i == 0 ) {
                placeWithParams.append( "?" );
            } else {
                placeWithParams.append( "&" );
            }
            placeWithParams.append( params[i] ).append( "=" ).append( params[i + 1] );
        }
        placeIdBox.sendKeys( placeWithParams );

        WebElement panelTypeBox = driver.findElement( By.id( "newPanelType" ) );
        panelTypeBox.clear();
        panelTypeBox.sendKeys( panelType.getName() );

        WebElement positionBox = driver.findElement( By.id( "newPanelPosition" ) );
        positionBox.clear();
        positionBox.sendKeys( position.name() );

        WebElement newPanelButton = driver.findElement( By.id( "newPanelButton" ) );
        newPanelButton.click();
    }
}
