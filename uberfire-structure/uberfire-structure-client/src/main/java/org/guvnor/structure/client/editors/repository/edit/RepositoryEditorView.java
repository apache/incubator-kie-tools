/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.edit;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.editors.repository.common.CopyRepositoryUrlBtn;
import org.guvnor.structure.client.navigator.CommitNavigatorEntry;
import org.guvnor.structure.repositories.PublicURI;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

public class RepositoryEditorView extends Composite
        implements
        RepositoryEditorPresenter.View {

    interface RepositoryEditorViewBinder
            extends
            UiBinder<Widget, RepositoryEditorView> {

    }

    private static RepositoryEditorViewBinder uiBinder = GWT.create(RepositoryEditorViewBinder.class);

    @UiField
    public Heading repoName;

    @UiField
    public Paragraph repoDesc;

    @UiField
    public TextBox gitDaemonURI;

    @UiField
    public CopyRepositoryUrlBtn myGitCopyButton;

    @UiField
    public Paragraph linksPanel;

    @UiField
    public FlowPanel history;

    @UiField
    public Button loadMore;

    private RepositoryEditorPresenter presenter;
    private boolean readOnly;

    public RepositoryEditorView() {
        initWidget(uiBinder.createAndBindUi(this));

        myGitCopyButton.addDomHandler((e) -> presenter.onGitUrlCopied(gitDaemonURI.getText()),
                                      ClickEvent.getType());
    }

    @Override
    public void init(final RepositoryEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setRepositoryInfo(final String repositoryName,
                                  final String owner,
                                  final boolean readOnly,
                                  final List<PublicURI> publicURIs,
                                  final String description,
                                  final List<VersionRecord> initialVersionList) {
        this.readOnly = readOnly;
        if (owner != null && !owner.isEmpty()) {
            repoName.setText(owner + " / " + repositoryName);
        } else {
            repoName.setText(repositoryName);
        }
        repoDesc.setText(description);
        int count = 0;
        if (publicURIs.size() > 0) {
            linksPanel.setText(CoreConstants.INSTANCE.AvailableProtocols());
        }
        for (final PublicURI publicURI : publicURIs) {
            if (count == 0) {
                gitDaemonURI.setText(publicURI.getURI());
            }
            final String protocol = publicURI.getProtocol() == null ? "default" : publicURI.getProtocol();
            final Button anchor = new Button(protocol);
            anchor.getElement().getStyle().setMarginLeft(5,
                                                         Style.Unit.PX);
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    gitDaemonURI.setText(publicURI.getURI());
                }
            });
            if (count != 0) {
                anchor.getElement().getStyle().setPaddingLeft(5,
                                                              Style.Unit.PX);
            }
            linksPanel.add(anchor);
            count++;
        }

        if (initialVersionList != null && !initialVersionList.isEmpty()) {
            loadContent(initialVersionList);
        } else {
            history.setVisible(false);
        }

        loadMore.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onLoadMoreHistory(history.getWidgetCount());
            }
        });

        final String uriId = "uri-for-" + repositoryName;
        gitDaemonURI.getElement().setId(uriId);

        myGitCopyButton.init(false,
                             uriId,
                             gitDaemonURI.getText());

        glueCopy(myGitCopyButton.getElement());
    }

    @Override
    public void reloadHistory(final List<VersionRecord> versionList) {
        loadContent(versionList);
        BusyPopup.close();
    }

    @Override
    public void addHistory(List<VersionRecord> versionList) {
        if (!versionList.isEmpty()) {
            loadContent(versionList);
        } else {
            loadMore.setEnabled(false);
        }
    }

    private void loadContent(final List<VersionRecord> versionRecordList) {
        for (VersionRecord vr : versionRecordList) {
            history.add(new CommitNavigatorEntry(readOnly,
                                                 vr,
                                                 new ParameterizedCommand<VersionRecord>() {
                                                     @Override
                                                     public void execute(final VersionRecord record) {
                                                         BusyPopup.showMessage(CoreConstants.INSTANCE.Reverting());
                                                         presenter.onRevert(record);
                                                     }
                                                 }));
        }
    }

    public static native void glueCopy(final Element element) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;
}