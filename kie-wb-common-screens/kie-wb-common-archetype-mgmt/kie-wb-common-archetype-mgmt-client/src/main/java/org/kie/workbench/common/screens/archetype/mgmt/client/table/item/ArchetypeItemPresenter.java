/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.archetype.mgmt.client.table.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.item.model.ArchetypeItem;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;

@Dependent
public class ArchetypeItemPresenter extends ListItemPresenter<ArchetypeItem, AbstractArchetypeTablePresenter, ArchetypeItemPresenter.View>
        implements HasBusyIndicator {

    static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MMMM dd, yyyy");
    private final TranslationService ts;
    private final Caller<ArchetypeService> archetypeService;
    private final BusyIndicatorView busyIndicatorView;
    private ArchetypeItem archetypeItem;
    private AbstractArchetypeTablePresenter parentPresenter;

    @Inject
    public ArchetypeItemPresenter(final View view,
                                  final TranslationService ts,
                                  final Caller<ArchetypeService> archetypeService,
                                  final BusyIndicatorView busyIndicatorView) {
        super(view);

        this.ts = ts;
        this.archetypeService = archetypeService;
        this.busyIndicatorView = busyIndicatorView;
    }

    @Override
    public ArchetypeItemPresenter setup(final ArchetypeItem archetypeItem,
                                        final AbstractArchetypeTablePresenter parentPresenter) {
        final Archetype archetype = archetypeItem.getArchetype();

        this.archetypeItem = archetypeItem;
        this.parentPresenter = parentPresenter;

        view.init(this);

        view.setIncluded(archetypeItem.isSelected());
        view.setGroupId(archetype.getGav().getGroupId());
        view.setArtifactId(archetype.getGav().getArtifactId());
        view.setVersion(archetype.getGav().getVersion());
        view.setCreatedDate(DATE_FORMAT.format(archetype.getCreatedDate()));

        view.showInclude(parentPresenter.isShowIncludeColumn());
        view.showStatus(parentPresenter.isShowStatusColumn());
        view.showDeleteAction(parentPresenter.isShowDeleteAction());
        view.showValidateAction(parentPresenter.isShowValidateAction());
        view.showDefaultBadge(archetypeItem.isDefaultValue());

        view.enableIncludeCheckbox(isValid());
        view.enableSetDefault(isValid());

        if (parentPresenter.isShowDeleteAction()) {
            view.setDeleteCommand(createDeleteCommand(archetypeItem));
        }

        if (archetypeItem.isDefaultValue()) {
            view.setDefaultBadgeTooltip(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_DefaultMessage));
        }

        resolveStatus(archetype);

        return this;
    }

    @Override
    public ArchetypeItem getObject() {
        return archetypeItem;
    }

    @Override
    public void showBusyIndicator(final String message) {
        busyIndicatorView.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    public void setIncluded(final boolean isIncluded) {
        if (parentPresenter.canMakeChanges() && isValid()) {
            view.checkIncluded(isIncluded);
            archetypeItem.setSelected(isIncluded);
            parentPresenter.setSelected(archetypeItem,
                                        isIncluded);
        }
    }

    public void makeDefault() {
        if (parentPresenter.canMakeChanges() && isValid()) {
            setIncluded(true);
            parentPresenter.makeDefaultValue(archetypeItem.getArchetype().getAlias(),
                                             true);
        }
    }

    public void validate() {
        if (parentPresenter.canMakeChanges()) {
            showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));

            archetypeService.call(v -> hideBusyIndicator(),
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .validate(archetypeItem.getArchetype().getAlias());
        }
    }

    private boolean isValid() {
        return archetypeItem.getArchetype().getStatus() == ArchetypeStatus.VALID;
    }

    private void resolveStatus(final Archetype archetype) {
        if (archetype.getStatus() == ArchetypeStatus.VALID) {
            view.showValidStatus(true);
            view.showInvalidStatus(false);
            view.setValidTooltip(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ValidMessage));
        } else if (archetype.getStatus() == ArchetypeStatus.INVALID) {
            view.showValidStatus(false);
            view.showInvalidStatus(true);
            view.setInvalidTooltip(ts.format(ArchetypeManagementConstants.ArchetypeManagement_InvalidMessage,
                                             archetype.getMessage()));
        }
    }

    Command createDeleteCommand(final ArchetypeItem item) {
        return () -> {
            if (parentPresenter.canMakeChanges()) {
                showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));
                archetypeService.call(v -> hideBusyIndicator(),
                                      new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                        .delete(item.getArchetype().getAlias());
            }
        };
    }

    public interface View extends ListItemView<ArchetypeItemPresenter>,
                                  IsElement {

        void showInclude(boolean isVisible);

        void showStatus(boolean isVisible);

        void setIncluded(boolean isIncluded);

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void setCreatedDate(String createdDate);

        void showInvalidStatus(boolean isVisible);

        void setInvalidTooltip(String message);

        void showValidStatus(boolean isVisible);

        void setValidTooltip(String message);

        void setDeleteCommand(Command deleteCommand);

        void showDeleteAction(boolean isVisible);

        void showValidateAction(boolean isVisible);

        void showDefaultBadge(boolean isVisible);

        void setDefaultBadgeTooltip(String message);

        void enableIncludeCheckbox(boolean isEnabled);

        void enableSetDefault(boolean isEnabled);

        void checkIncluded(boolean isChecked);
    }
}