/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.core.client.wizards;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;

/**
 * Things a Wizard needs to implement
 */
public interface Wizard {

    /**
     * Start the Wizard
     */
    void start();

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
     * A page has been selected from the UI
     * @param pageNumber
     */
    void pageSelected( final int pageNumber );

    /**
     * Provide a title
     * @return
     */
    String getTitle();

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
     * Is the Wizard complete; i.e. has all the necessary information for all
     * pages in the Wizard been captured. What constitutes necessary data is up to
     * the Wizard implementation, but a login page might consider the User ID and
     * Password as necessary.
     * @param callback True if the page is complete
     */
    void isComplete( Callback<Boolean> callback );

    /**
     * The Wizard has been completed
     */
    void complete();

    /**
     * The Wizard has been closed
     */
    void close();

}
