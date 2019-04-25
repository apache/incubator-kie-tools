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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class RelationshipEditionDialogViewImpl
        extends BaseModal
        implements RelationshipEditionDialog.View {

    @UiField
    Select relationType;

    @UiField
    Select fetchMode;

    @UiField
    CheckBox optional;

    @UiField
    FormLabel optionalLabel;

    @UiField
    FormLabel mappedByLabel;

    @UiField
    TextBox mappedBy;

    @UiField
    FormLabel orphanRemovalLabel;

    @UiField
    CheckBox orphanRemoval;

    @UiField
    CheckBox cascadeAll;

    @UiField
    CheckBox cascadePersist;

    @UiField
    CheckBox cascadeMerge;

    @UiField
    CheckBox cascadeRemove;

    @UiField
    CheckBox cascadeRefresh;

    @UiField
    CheckBox cascadeDetach;

    private ModalFooterOKCancelButtons footer;

    private RelationshipEditionDialog presenter;

    interface Binder extends UiBinder<Widget, RelationshipEditionDialogViewImpl> {

    }

    private static RelationshipEditionDialogViewImpl.Binder uiBinder = GWT.create(RelationshipEditionDialogViewImpl.Binder.class);

    public RelationshipEditionDialogViewImpl() {
        setTitle(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_title());
        setBody(uiBinder.createAndBindUi(RelationshipEditionDialogViewImpl.this));

        footer = new ModalFooterOKCancelButtons(
                () -> presenter.onOK(),
                () -> presenter.onCancel()
        );
        add(footer);

        relationType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_value_not_configured_option_label(), UIUtil.NOT_SELECTED));
        relationType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_value_one_to_one(), RelationType.ONE_TO_ONE.name()));
        relationType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_value_one_to_many(), RelationType.ONE_TO_MANY.name()));
        relationType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_value_many_to_one(), RelationType.MANY_TO_ONE.name()));
        relationType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_relationship_edition_dialog_value_many_to_many(), RelationType.MANY_TO_MANY.name()));

        relationType.addValueChangeHandler(e -> relationTypeChanged());
        cascadeAll.addClickHandler(e -> onCascadeAllChanged());

        fetchMode.add(UIUtil.newOption(FetchMode.EAGER.name(), FetchMode.EAGER.name()));
        fetchMode.add(UIUtil.newOption(FetchMode.LAZY.name(), FetchMode.LAZY.name()));
    }

    @Override
    public void init(RelationshipEditionDialog presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getRelationType() {
        return relationType.getValue();
    }

    @Override
    public void setRelationType(String relationType) {
        UIUtil.setSelectedValue(this.relationType, relationType);
    }

    @Override
    public String getFetchMode() {
        return fetchMode.getValue();
    }

    @Override
    public void setFetchMode(String fetchMode) {
        UIUtil.setSelectedValue(this.fetchMode, fetchMode);
    }

    @Override
    public boolean getOptional() {
        return optional.getValue();
    }

    @Override
    public void setOptional(boolean optional) {
        this.optional.setValue(optional);
    }

    @Override
    public String getMappedBy() {
        return mappedBy.getText();
    }

    @Override
    public void setMappedBy(String mappedBy) {
        this.mappedBy.setText(mappedBy);
    }

    @Override
    public boolean getOrphanRemoval() {
        return orphanRemoval.getValue();
    }

    @Override
    public void setOrphanRemoval(boolean orphanRemoval) {
        this.orphanRemoval.setValue(orphanRemoval);
    }

    @Override
    public boolean getCascadeAll() {
        return cascadeAll.getValue();
    }

    @Override
    public void setCascadeAll(boolean cascadeAll) {
        this.cascadeAll.setValue(cascadeAll);
    }

    @Override
    public boolean getCascadePersist() {
        return cascadePersist.getValue();
    }

    @Override
    public void setCascadePersist(boolean cascadePersist) {
        this.cascadePersist.setValue(cascadePersist);
    }

    @Override
    public boolean getCascadeMerge() {
        return cascadeMerge.getValue();
    }

    @Override
    public void setCascadeMerge(boolean cascadeMerge) {
        this.cascadeMerge.setValue(cascadeMerge);
    }

    @Override
    public boolean getCascadeRemove() {
        return cascadeRemove.getValue();
    }

    @Override
    public void setCascadeRemove(boolean cascadeRemove) {
        this.cascadeRemove.setValue(cascadeRemove);
    }

    @Override
    public boolean getCascadeRefresh() {
        return cascadeRefresh.getValue();
    }

    @Override
    public void setCascadeRefresh(boolean cascadeRefresh) {
        this.cascadeRefresh.setValue(cascadeRefresh);
    }

    @Override
    public boolean getCascadeDetach() {
        return cascadeDetach.getValue();
    }

    @Override
    public void setCascadeDetach(boolean cascadeDetach) {
        this.cascadeDetach.setValue(cascadeDetach);
    }

    @Override
    public void setEnabled(boolean enabled) {
        relationType.setEnabled(enabled);
        fetchMode.setEnabled(enabled);
        orphanRemoval.setEnabled(enabled);
        cascadeAll.setEnabled(enabled);
        cascadePersist.setEnabled(enabled);
        cascadeMerge.setEnabled(enabled);
        cascadeRemove.setEnabled(enabled);
        cascadeRefresh.setEnabled(enabled);
        cascadeDetach.setEnabled(enabled);
        optional.setEnabled(enabled);
        mappedBy.setEnabled(enabled);
        footer.enableOkButton(enabled);
    }

    @Override
    public void enableCascadeAll(boolean enabled) {
        cascadeAll.setEnabled(enabled);
    }

    @Override
    public void enableCascadePersist(boolean enabled) {
        cascadePersist.setEnabled(enabled);
    }

    @Override
    public void enableCascadeMerge(boolean enabled) {
        cascadeMerge.setEnabled(enabled);
    }

    @Override
    public void enableCascadeRemove(boolean enable) {
        cascadeRemove.setEnabled(enable);
    }

    @Override
    public void enableCascadeRefresh(boolean enable) {
        cascadeRefresh.setEnabled(enable);
    }

    @Override
    public void enableCascadeDetach(boolean enable) {
        cascadeDetach.setEnabled(enable);
    }

    @Override
    public void setOptionalVisible(boolean visible) {
        optionalLabel.setVisible(visible);
        optional.setVisible(visible);
    }

    @Override
    public void setOrphanRemovalVisible(boolean visible) {
        orphanRemovalLabel.setVisible(visible);
        orphanRemoval.setVisible(visible);
    }

    @Override
    public void setMappedByVisible(boolean visible) {
        mappedByLabel.setVisible(visible);
        mappedBy.setVisible(visible);
    }

    private void relationTypeChanged() {
        presenter.onRelationTypeChanged();
    }

    private void onCascadeAllChanged() {
        presenter.onCascadeAllChanged();
    }
}