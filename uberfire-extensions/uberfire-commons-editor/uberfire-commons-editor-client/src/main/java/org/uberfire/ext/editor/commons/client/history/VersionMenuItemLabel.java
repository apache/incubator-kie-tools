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

package org.uberfire.ext.editor.commons.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.base.version.VersionRecord;

public class VersionMenuItemLabel
        extends Composite {

    private final Callback<VersionRecord> selectionCallback;
    private VersionRecord versionRecord;

    public interface VersionMenuItemStyle
            extends CssResource {

        String normal();

        String selected();

        String comment();

        String version();

        String author();

        String authorSelected();

    }

    interface Binder
            extends
            UiBinder<Widget, VersionMenuItemLabel> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    VersionMenuItemStyle style;

    @UiField
    FocusPanel base;

    @UiField
    InlineLabel author;

    @UiField
    InlineLabel date;

    @UiField
    Label comment;

    @UiField
    HTMLPanel panel;

    @UiField
    InlineLabel number;

    @UiField
    DivElement authorContainer;

    public VersionMenuItemLabel(
            VersionRecord versionRecord,
            Integer number,
            boolean isSelected,
            Callback<VersionRecord> selectionCallback) {

        this.versionRecord = versionRecord;
        initWidget(uiBinder.createAndBindUi(this));

        if (isSelected) {
            setSelected();
        }

        this.selectionCallback = selectionCallback;
        this.number.setText(number.toString());
        author.setText(versionRecord.author());
        date.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(versionRecord.date()));
        comment.setText(snip(versionRecord.comment()));
        base.setTitle(versionRecord.comment());
    }

    private void setSelected() {
        panel.addStyleName(style.selected());
        panel.removeStyleName(style.normal());
        authorContainer.addClassName(style.authorSelected());
        authorContainer.removeClassName(style.author());
    }

    private String snip(String comment) {
        if (comment != null && comment.length() >= 60) {
            return comment.substring(0, 58) + " ...";
        } else {
            return comment;
        }
    }

    @UiHandler("base")
    public void handleClick(ClickEvent event) {
        if (selectionCallback != null) {
            selectionCallback.callback(versionRecord);
        }
    }
}
