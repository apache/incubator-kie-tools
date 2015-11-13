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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.callbacks.Callback;

/**
 * A page for a Wizard
 */
public interface WizardPage
        extends
        IsWidget {

    /**
     * Page title
     * @return
     */
    String getTitle();

    /**
     * Is the page; i.e. has all the necessary information been captured. What
     * constitutes necessary data is up to the Wizard implementation, but a login
     * page might consider the User ID and Password as necessary.
     * @param callback True if the page is complete
     */
    void isComplete( Callback<Boolean> callback );

    /**
     * Initialise the page with things that don't change between page visits
     */
    void initialise();

    /**
     * Prepare the page before it is displayed with things that can change
     * between page visits
     */
    void prepareView();

}
