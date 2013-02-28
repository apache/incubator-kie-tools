/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.wizards;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

/**
 * Things a Wizard needs to implement
 */
public interface Wizard<T extends WizardContext> {

    /**
     * Provide a title
     * @return
     */
    String getTitle();

    /**
     * Provide a list of pages
     * @return
     */
    List<WizardPage> getPages();

    /**
     * Return the widget for a particular page
     * @param pageNumber The index of of the page
     * @return
     */
    Widget getPageWidget( int pageNumber );

    /**
     * The preferred height of the page
     * @return
     */
    int getPreferredHeight();

    /**
     * The preferred width of the page
     * @return
     */
    int getPreferredWidth();

    /**
     * Is the page complete; i.e. has all the necessary information for the page
     * been captured. What constitutes necessary data is up to the Wizard
     * implementation, but a login page might consider the User ID and Password
     * as necessary.
     * @return True if the page is considered complete
     */
    boolean isComplete();

    /**
     * The Wizard has been completed
     */
    void complete();

}
