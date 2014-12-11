package org.uberfire.wbtest.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Predicate;


public class SeleniumConditions {

    /**
     * Returns a predicate that evaluates to true iff the given element has the given size.
     */
    public static Predicate<WebDriver> reportedSizeIs( final MaximizeTestScreenWrapper testScreen,
                                                       final Dimension expectedSize ) {
        return new Predicate<WebDriver>() {
            @Override
            public boolean apply( WebDriver input ) {
                Dimension actualSize = testScreen.getReportedSize();
                return actualSize.equals( expectedSize );
            }
        };
    }
}
