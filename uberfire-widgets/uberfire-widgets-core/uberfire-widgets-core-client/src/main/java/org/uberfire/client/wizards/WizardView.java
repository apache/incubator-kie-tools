/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.uberfire.client.mvp.UberView;

/**
 * View and Presenter definitions for the generic Wizard
 */
public interface WizardView
        extends
        UberView<WizardPresenter> {

    interface Presenter {

        /**
         * A page has been selected from the UI
         * @param pageNumber
         */
        void pageSelected( int pageNumber );

        /**
         * The Wizard has been finished
         */
        void complete();

        /**
         * Hide the Wizard
         */
        void hide();

    }

    /**
     * Show the Wizard
     */
    void show();

    /**
     * The title for the Wizard
     * @param title
     */
    void setTitle( String title );

    /**
     * The individual page titles
     * @param pages
     */
    void setPageTitles( List<WizardPage> pages );

    /**
     * Select a page
     * @param page
     */
    void selectPage( int page );

    /**
     * Set the Widget to display in the body panel of the generic Wizard
     * @param w
     */
    void setBodyWidget( Widget w );

    /**
     * Set the body panel preferred height
     * @param height
     */
    void setPreferredHeight( int height );

    /**
     * Set the body panel preferred width
     * @param width
     */
    void setPreferredWidth( int width );

    /**
     * The state (completed, not completed) of a page has changed.
     * @param pageIndex
     * @param isComplete
     */
    void setPageCompletionState( int pageIndex,
                                 boolean isComplete );

    /**
     * The state (completed, not completed) of the whole Wizard has changed
     * @param isComplete
     */
    void setCompletionStatus( boolean isComplete );

    /**
     * Hide the Wizard
     */
    void hide();

}
