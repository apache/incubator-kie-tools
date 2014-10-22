package org.uberfire.wbtest.selenium;

import static org.junit.Assert.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Selenium Page Object for interacting with UberTabPanel.
 */
public class UberTabPanelWrapper {

    private final WebDriver driver;

    public UberTabPanelWrapper( WebDriver driver ) {
        this.driver = PortablePreconditions.checkNotNull( "driver", driver );
    }

    /**
     * Returns the &lt;li&gt; elements of all the tabs that are not nested under the dropdown tab. The returned list
     * will include the dropdown tab if it's currently displayed.
     */
    public List<WebElement> getNonNestedTabs() {
        return driver.findElements( By.cssSelector( ".nav.nav-tabs > li" ) );
    }

    /**
     * Returns the &lt;li&gt; elements of all the tabs that are nested under the dropdown tab.
     */
    public List<WebElement> getNestedTabs() {
        return driver.findElements( By.cssSelector( ".dropdown-menu > li" ) );
    }

    /**
     * Returns the dropdown tab if it is present. Contains an &lt;a&gt; tag (the tab UI itself) and a nested &lt;ul&gt;
     * (the dropdown menu). Returns null if the dropdown is not currently visible.
     */
    public WebElement getDropdownTab() {
        List<WebElement> dropdowns = driver.findElements( By.cssSelector( ".nav.nav-tabs > li.dropdown" ) );
        if ( dropdowns.isEmpty() ) {
            return null;
        }
        if ( dropdowns.size() > 1 ) {
            fail( "Found too many dropdown tabs: " + dropdowns );
        }
        return dropdowns.get( 0 );
    }

    /**
     * Returns the current label text of the dropdown tab itself.
     */
    public String getDropdownTabLabel() {
        WebElement dropdownTab = getDropdownTab();
        return dropdownTab.findElement( By.cssSelector( "a.dropdown-toggle" ) ).getText();
    }

    /**
     * Returns the currently-selected tab that is not the dropdown tab (but might be a tab nested under the dropdown).
     * Returns null if no tab is selected (should only happen when there are no tabs at all). Causes test failure if
     * multiple tabs are selected (other than the dropdown tab).
     */
    public WebElement getSelectedTab() {
        WebElement foundTab = null;
        for ( WebElement selectedTab : driver.findElements( By.cssSelector( "li.active" ) ) ) {
            if ( selectedTab.getAttribute( "class" ).contains( "dropdown" ) ) {
                continue;
            }
            if ( foundTab != null ) {
                fail( "Too many selected tabs! Found \"" + foundTab.getText() + "\" and \"" + selectedTab.getText() + "\"" );
            }
            foundTab = selectedTab;
        }
        return foundTab;
    }

    /**
     * Returns the label of the currently-selected tab (which may be under the dropdown, but will not return the label
     * of the dropdown tab itself).
     */
    public String getSelectedTabLabel() throws InterruptedException {
        WebElement selectedTab = getSelectedTab();
        if ( selectedTab == null ) {
            return null;
        }

        // selenium only returns text of visible elements, so we have to open the dropdown
        WebElement dropdownTab = getDropdownTab();
        if ( dropdownTab != null ) {
            dropdownTab.findElement( By.tagName( "a" ) ).click();
        }

        String allText = selectedTab.findElement( By.tagName( "a" ) ).getText();
        String buttonText = selectedTab.findElement( By.tagName( "button" ) ).getText();

        if ( dropdownTab != null ) {
            dropdownTab.click();
        }

        return allText.substring( 0, allText.indexOf( buttonText ) ).trim();
    }

    /**
     * Clicks on the close button of the currently selected tab. Fails the test if no tab is selected.
     */
    public void closeSelectedTab() {
        WebElement selectedTab = getSelectedTab();
        if ( selectedTab == null ) {
            fail( "No tab is selected" );
        }
        selectedTab.findElement( By.tagName( "button" ) ).click();
    }

    /**
     * Closes the tab that contains the given text in its label. The tab need not be the currently selected tab.
     *
     * @param label
     *            the label of the tab. Beware that the match is done by partial link text (because the close button
     *            gets in the way of exact matching) so ensure the given text is not a substring of some other tab's label!
     */
    public void closeTab( String label ) {
        WebElement tab = driver.findElement( By.partialLinkText( label ) );
        tab.findElement( By.tagName( "button" ) ).click();
    }
}
