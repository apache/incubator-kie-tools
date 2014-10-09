package org.uberfire.wbtest.selenium;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.uberfire.wbtest.client.resize.ResizeTestWidget;

/**
 * Abstraction over the {@link ResizeTestWidget} that lets us check what size it thinks it has, what size it really has,
 * and so on.
 */
public class ResizeWidgetWrapper {

    private final WebDriver driver;
    private final String id;

    public ResizeWidgetWrapper( WebDriver driver, String id ) {
        this.driver = driver;
        this.id = id;
    }

    /**
     * Searches the DOM for the resize widget having the ID this wrapper cares about. Throws a timeout exception if the
     * widget is not found within the driver's current implicit wait period.
     */
    public WebElement find() {
        return driver.findElement( By.id( "gwt-debug-ResizeTestWidget-" + id ) );
    }

    /**
     * Returns the size that the resize test widget's element actually has (according to the DOM).
     */
    public Dimension getActualSize() {
        WebElement element = find();
        return element.getSize();
    }

    /**
     * Returns the size that the resize test widget believes it has.
     */
    public Dimension getReportedSize() {
        WebElement element = find();
        String text = element.getText();
        Matcher matcher = Pattern.compile( "([0-9]+)x([0-9]+)" ).matcher( text );
        if ( matcher.matches() ) {
            return new Dimension( Integer.parseInt( matcher.group( 1 ) ),
                                  Integer.parseInt( matcher.group( 2 ) ) );
        }
        throw new IllegalStateException( "Couldn't understand reported size \"" + text + "\"" );
    }
}
