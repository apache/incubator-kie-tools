/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.editors.repository.common.CopyRepositoryUrlBtn;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

public class RepositoryItemViewImpl
        extends Composite
        implements RepositoryItemView {

    private static RepositoriesViewItemBinder uiBinder = GWT.create(RepositoriesViewItemBinder.class);

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
    public Select branchesDropdown;

    @UiField
    public Button btnChangeBranch;

    private RepositoryItemPresenter presenter;

    @Inject
    public RepositoryItemViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        myGitCopyButton.addDomHandler((e) -> presenter.onGitUrlCopied(gitDaemonURI.getText()),
                                      ClickEvent.getType());

        glueCopy(myGitCopyButton.getElement());
    }

    public static native void glueCopy(final com.google.gwt.user.client.Element element) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

    @Override
    public void setRepositoryName(final String repositoryName) {
        repoName.setText(repositoryName);
    }

    @Override
    public void setRepositoryDescription(final String description) {
        repoDesc.setText(description);
    }

    @Override
    public void showAvailableProtocols() {
        linksPanel.setText(CoreConstants.INSTANCE.AvailableProtocols());
    }

    @Override
    public void setDaemonURI(final String uri) {
        gitDaemonURI.setText(uri);
    }

    @Override
    public void addProtocol(final String protocol) {
        linksPanel.add(new ProtocolButton(protocol,
                                          new ClickHandler() {
                                              @Override
                                              public void onClick(ClickEvent event) {
                                                  presenter.onAnchorSelected(protocol);
                                              }
                                          },
                                          linksPanel.getWidgetCount() != 0));
    }

    @Override
    public void setPresenter(final RepositoryItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setUriId(final String uriId) {
        gitDaemonURI.getElement().setId(uriId);

        myGitCopyButton.init(true,
                             uriId,
                             gitDaemonURI.getText());
    }

    @Override
    public void clearBranches() {
        branchesDropdown.clear();
    }

    @Override
    public void addBranch(final String branch) {
        final Option option = new Option();
        option.setText(branch);
        option.setValue(branch);
        branchesDropdown.add(option);
    }

    @Override
    public void setSelectedBranch(final String currentBranch) {
        branchesDropdown.setValue(currentBranch);
        branchesDropdown.refresh();
    }

    @Override
    public String getSelectedBranch() {
        return branchesDropdown.getValue();
    }

    @Override
    public void refresh() {
        branchesDropdown.refresh();
    }

    @UiHandler("btnChangeBranch")
    public void onUpdateRepository(final ClickEvent event) {
        presenter.onUpdateRepository(branchesDropdown.getValue());
    }

    interface RepositoriesViewItemBinder
            extends
            UiBinder<Widget, RepositoryItemViewImpl> {

    }
}
