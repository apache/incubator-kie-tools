package org.uberfire.wbtest.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A Selenium Page Object representing the MenuSplashList widget.
 */
public class SplashListWrapper {

    private final WebElement splashList;

    public SplashListWrapper( WebDriver driver, String elementId ) {
        splashList = driver.findElement( By.id( elementId ) );
    }

    /**
     * Returns a list of the items in the dropdown list. Each string in the result is trimmed of leading and trailing
     * whitespace.
     */
    public List<String> getContents() {
        List<String> contents = new ArrayList<String>();
        splashList.findElement( By.className( "dropdown-toggle" ) ).click();
        for ( WebElement e : splashList.findElements( By.tagName( "li" ) ) ) {
            contents.add( e.getText() );
        }
        splashList.findElement( By.className( "dropdown-toggle" ) ).click();
        return contents;
    }
}
