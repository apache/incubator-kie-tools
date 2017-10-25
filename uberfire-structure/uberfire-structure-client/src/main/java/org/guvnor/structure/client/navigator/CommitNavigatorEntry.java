/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.client.navigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class CommitNavigatorEntry extends Composite {

    interface CommitNavigatorEntryViewBinder
            extends
            UiBinder<Widget, CommitNavigatorEntry> {

    }

    private static final DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd h:mm a");

    private static CommitNavigatorEntryViewBinder uiBinder = GWT.create(CommitNavigatorEntryViewBinder.class);

    @UiField
    Label message;

    @UiField
    InlineLabel author;

    @UiField
    InlineLabel date;

    @UiField
    SimplePanel revertButtonContainer;

    public CommitNavigatorEntry(final boolean readOnly,
                                final VersionRecord versionRecord,
                                final ParameterizedCommand<VersionRecord> onRevertCommand) {
        initWidget(uiBinder.createAndBindUi(this));
        initWidget(readOnly,
                   versionRecord,
                   onRevertCommand);
    }

    private void initWidget(final boolean readOnly,
                            final VersionRecord versionRecord,
                            final ParameterizedCommand<VersionRecord> onRevertCommand) {
        message.setText(versionRecord.comment());
        author.setText(versionRecord.author() + " - ");
        date.setText(fmt.format(versionRecord.date()));

        if (onRevertCommand != null) {
            revertButtonContainer.setWidget(new Button(CoreConstants.INSTANCE.RevertToThis()) {{
                setEnabled(!readOnly);
                setBlock(true);
                setType(ButtonType.DANGER);
                addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(final ClickEvent event) {
                        final YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Warning(),
                                                                                                       CoreConstants.INSTANCE.ConfirmStateRevert(),
                                                                                                       new Command() {
                                                                                                           @Override
                                                                                                           public void execute() {
                                                                                                               onRevertCommand.execute(versionRecord);
                                                                                                           }
                                                                                                       },
                                                                                                       new Command() {
                                                                                                           @Override
                                                                                                           public void execute() {
                                                                                                           }
                                                                                                       },
                                                                                                       null
                        );
                        yesNoCancelPopup.setClosable(false);
                        yesNoCancelPopup.show();
                    }
                });
            }});
        }
    }
}