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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.Command;

/**
 * A widget containing the page title of a Wizard page, along with an indicator
 * (a tick) that the page has been completed and whether it is the currently
 * displayed page (title is made bold).
 */
@Dependent
public class WizardPageTitle extends Composite {

    private static WizardPageTitleViewBinder uiBinder = GWT.create(WizardPageTitleViewBinder.class);
    @UiField
    protected AnchorListItem container;
    private final Command isCompleteCommand = new Command() {
        @Override
        public void execute() {
            setComplete(true);
        }
    };

    private final Command isIncompleteCommand = new Command() {
        @Override
        public void execute() {
            setComplete(false);
        }
    };
    @Inject
    private Event<WizardPageSelectedEvent> selectPageEvent;

    public WizardPageTitle() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setContent(final WizardPage page) {
        container.setText(page.getTitle());
        page.isComplete(new Callback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                setComplete(Boolean.TRUE.equals(result));
            }
        });

        container.addDomHandler(new ClickHandler() {

                                    public void onClick(final ClickEvent event) {
                                        selectPageEvent.fire(new WizardPageSelectedEvent(page));
                                    }
                                },
                                ClickEvent.getType());
    }

    /**
     * Is the page complete
     * @param isComplete
     */
    public void setComplete(final boolean isComplete) {
        container.setIcon(isComplete ? IconType.CHECK_SQUARE_O : IconType.SQUARE_O);
    }

    /**
     * Is the page the currently displayed page. Note WizardPageTitles are
     * unaware of other WizardPageTitles and hence a mediator class needs to
     * control the setting of the "current page" and de-selecting other previous
     * "current pages".
     * @param isSelected
     */
    public void setPageSelected(final boolean isSelected) {
        container.setActive(isSelected);
    }

    interface WizardPageTitleViewBinder
            extends
            UiBinder<Widget, WizardPageTitle> {

    }
}
