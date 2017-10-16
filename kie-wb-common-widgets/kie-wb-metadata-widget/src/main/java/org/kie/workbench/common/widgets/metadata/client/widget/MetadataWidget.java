/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.metadata.client.resources.ImageResources;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class MetadataWidget
        extends Composite
        implements HasBusyIndicator {

    private static Binder uiBinder = GWT.create(Binder.class);

    interface Binder
            extends
            UiBinder<Widget, MetadataWidget> {

    }

    @UiField
    TagWidget tags;

    @UiField
    FormControlStatic note;

    @UiField
    FormControlStatic uri;

    @UiField
    TextBox subject;

    @UiField
    TextBox type;

    @UiField(provided = true)
    ExternalLinkPresenter external;

    @UiField
    TextBox source;

    @UiField
    FormControlStatic lockedBy;

    @UiField
    PushButton unlock;

    private Metadata metadata = null;
    private boolean readOnly;
    private Runnable forceUnlockHandler;
    private String currentUser;
    private BusyIndicatorView busyIndicatorView;

    @Inject
    public MetadataWidget(final BusyIndicatorView busyIndicatorView,
                          final ExternalLinkPresenter external) {
        this.external = external;
        this.busyIndicatorView = busyIndicatorView;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setContent(final Metadata metadata,
                           final boolean readOnly) {
        this.metadata = checkNotNull("metadata",
                                     metadata);
        this.readOnly = readOnly;

        loadData();
    }

    public void setForceUnlockHandler(final Runnable forceUnlockHandler) {
        this.forceUnlockHandler = forceUnlockHandler;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    private void loadData() {

        tags.setContent(metadata,
                        this.readOnly);

        note.setText(metadata.getCheckinComment());

        uri.setText(metadata.getRealPath().toURI());

        subject.setText(metadata.getSubject());
        subject.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                metadata.setSubject(subject.getText());
            }
        });

        type.setText(metadata.getType());
        type.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                metadata.setType(type.getText());
            }
        });

        external.setLink(metadata.getExternalRelation());
        external.addChangeCallback(new Callback<String>() {
            @Override
            public void callback(final String result) {
                metadata.setExternalRelation(result);
            }
        });

        source.setText(metadata.getExternalSource());
        source.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                metadata.setExternalSource(source.getText());
            }
        });

        setLockStatus(metadata.getLockInfo());
    }

    public void setLockStatus(final LockInfo lockInfo) {
        lockedBy.setText(getLockStatusText(lockInfo));
        maybeShowForceUnlockButton(lockInfo);
    }

    String getLockStatusText(final LockInfo lockInfo) {
        final String lockStatusText;

        if (lockInfo.isLocked()) {
            if (lockInfo.lockedBy().equals(currentUser)) {
                lockStatusText = MetadataConstants.INSTANCE.LockedByHintOwned();
            } else {
                lockStatusText = MetadataConstants.INSTANCE.LockedByHint() + " " + lockInfo.lockedBy();
            }
        } else {
            lockStatusText = MetadataConstants.INSTANCE.UnlockedHint();
        }

        return lockStatusText;
    }

    private void maybeShowForceUnlockButton(final LockInfo lockInfo) {
        final Image unlockImage = new Image(ImageResources.INSTANCE.unlock());
        unlock.setHTML("<span>" + unlockImage.toString() + " " + unlock.getText() + "</span>");
        unlock.getElement().setAttribute("data-uf-lock",
                                         "false");
        unlock.setVisible(lockInfo.isLocked());
        unlock.setEnabled(true);
    }

    @Deprecated
    public Metadata getContent() {
        return metadata;
    }

    @Override
    public void showBusyIndicator(final String message) {
        busyIndicatorView.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    public void setNote(String text) {
        note.setText(text);
    }

    @UiHandler("unlock")
    public void onForceUnlock(ClickEvent e) {
        final YesNoCancelPopup yesNoCancelPopup =
                YesNoCancelPopup.newYesNoCancelPopup(MetadataConstants.INSTANCE.ForceUnlockConfirmationTitle(),
                                                     MetadataConstants.INSTANCE.ForceUnlockConfirmationText(metadata.getLockInfo().lockedBy()),
                                                     new Command() {
                                                         @Override
                                                         public void execute() {
                                                             forceUnlockHandler.run();
                                                             unlock.setEnabled(false);
                                                         }
                                                     },
                                                     new Command() {

                                                         @Override
                                                         public void execute() {
                                                         }
                                                     },
                                                     null);
        yesNoCancelPopup.setClosable(false);
        yesNoCancelPopup.show();
    }
}

