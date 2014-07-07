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

package org.kie.workbench.common.screens.socialscreen.client;

import java.util.Date;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.workbench.common.screens.socialscreen.client.discussion.DiscussionWidgetPresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.client.workbench.type.ClientResourceType;

public class OverviewScreenViewImpl
        extends Composite
        implements OverviewScreenView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, OverviewScreenViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    TextArea filePreview;

    @UiField
    TextArea description;

    @UiField
    Label resourceType;

    @UiField
    Label projects;

    @UiField
    Label lastModifiedLabel;

    @UiField
    Label createdLabel;

    @UiField(provided = true)
    MetadataWidget metadata;

    @UiField
    TabPanel tabPanel;

    @UiField(provided = true)
    DiscussionWidgetPresenter discussionArea;

    @Inject
    public OverviewScreenViewImpl(MetadataWidget metadata, DiscussionWidgetPresenter discussionArea) {

        this.metadata = metadata;

        this.discussionArea = discussionArea;

        initWidget(uiBinder.createAndBindUi(this));

        tabPanel.selectTab(0);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly(boolean isReadOnly) {
        description.setEnabled(!isReadOnly);
    }

    @Override
    public void setPreview(String text) {
        filePreview.setText(text);
    }

    @Override
    public void setDescription(String description) {
        this.description.setText(description);
    }

    @UiHandler("description")
    public void onDescriptionChange(KeyUpEvent event) {
        presenter.onDescriptionEdited(description.getText());
    }

    @Override
    public void setResourceType(ClientResourceType type) {
        resourceType.setText(type.getDescription());
    }

    @Override
    public void setProject(String project) {
        projects.setText(project);
    }

    @Override
    public void setLastModified(String lastContributor, Date lastModified) {
        lastModifiedLabel.setText("By/" + lastContributor + " on " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(lastModified));
    }

    @Override
    public void setCreated(String creator, Date dateCreated) {
        createdLabel.setText("By/" + creator + " on " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(dateCreated));
    }

    @Override
    public String getTitle(String fileName, String fileType) {
        return fileName + "- Business Rule";
    }

    @Override
    public void setMetadata(Metadata metadata, boolean isReadOnly) {
        this.metadata.setContent(metadata, isReadOnly);
        this.discussionArea.setContent(metadata);
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public Widget asWidget() {
        return super.asWidget();
    }

    @Override
    public void showSavingIndicator() {
        showBusyIndicator(CommonConstants.INSTANCE.Saving());
    }

    @Override
    public void showLoadingIndicator() {
        showBusyIndicator(CommonConstants.INSTANCE.Loading());
    }

}
