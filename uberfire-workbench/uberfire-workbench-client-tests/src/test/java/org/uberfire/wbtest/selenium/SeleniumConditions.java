/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
