package org.uberfire.wbtest.selenium;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Abstraction over the state and behaviour of the stuff in the {@code org.uberfire.wbtest.client.panels.custom} package.
 */
public class CustomPanelScreenWrapper {

    private final WebDriver driver;

    public CustomPanelScreenWrapper( WebDriver driver ) {
        this.driver = checkNotNull( "driver", driver );
    }

    public int getLiveInstanceCount() {
        WebElement label = findElement( "liveCustomPanelInstances" );
        Pattern p = Pattern.compile( "Live Instances: ([0-9]*)" );
        Matcher m = p.matcher( label.getText() );
        if ( m.matches() ) {
            return Integer.parseInt( m.group( 1 ) );
        }
        throw new IllegalStateException( "Couldn't find live instance count label on page" );
    }

    public int getTotalInstanceCount() {
        WebElement label = findElement( "totalCustomPanelInstances" );
        Pattern p = Pattern.compile( "Total Instances: ([0-9]*)" );
        Matcher m = p.matcher( label.getText() );
        if ( m.matches() ) {
            return Integer.parseInt( m.group( 1 ) );
        }
        throw new IllegalStateException( "Couldn't find total instance count label on page" );
    }

    public String createCustomPopup() {
        int previousTotalInstances = getTotalInstanceCount();
        WebElement button = findElement( "open" );
        button.click();
        return "CustomPanelContentScreen-" + previousTotalInstances;
    }

    public boolean customPopupExistsInDom( String id ) {
        List<WebElement> found = driver.findElements( By.id( "gwt-debug-" + id ) );
        return !found.isEmpty();
    }

    public void closeLatestPopupUsingPlaceManager() {
        WebElement button = findElement( "closeWithPlaceManager" );
        button.click();
    }

    public void closeLatestPopupByRemovingFromDom() {
        WebElement button = findElement( "closeByRemovingFromDom" );
        button.click();
    }

    /**
     * Allows looking up CustomPanelContentScreen elements by their unqualified name.
     * @param shortName the name without the gwt-debug-CustomPanelMakerScreen- prefix.
     */
    private WebElement findElement( String shortName ) {
        return driver.findElement( By.id( "gwt-debug-CustomPanelMakerScreen-" + shortName ) );
    }

}
