/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.socialscreen.client.discussion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.java.nio.base.version.VersionRecord;

public class CommitLabel
        extends Composite
        implements HasClickHandlers {

    interface Binder
            extends
            UiBinder<Widget, CommitLabel> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    FocusPanel base;

    @UiField
    Label author;

    @UiField
    Label date;

    @UiField
    Label comment;

    public CommitLabel(VersionRecord versionRecord) {
        initWidget(uiBinder.createAndBindUi(this));

        author.setText(versionRecord.author());
        date.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(versionRecord.date()));
        comment.setText(versionRecord.comment());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return base.addClickHandler(handler);
    }
}
