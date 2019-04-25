/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class SequenceGeneratorEditionDialog
        implements PropertyEditionPopup {

    public interface View extends IsWidget {

        void init(SequenceGeneratorEditionDialog presenter);

        String getGeneratorName();

        void setGeneratorName(String generatorName);

        String getSequenceName();

        void setSequenceName(String sequenceName);

        String getInitialValue();

        void setInitialValue(String initialValue);

        void setInitialValueError(String error);

        void clearInitialValueError();

        String getAllocationSize();

        void setAllocationSize(String allocationSize);

        void setAllocationSizeError(String error);

        void clearAllocationSizeError();

        void setEnabled(boolean enabled);

        void enableOkAction(boolean enabled);

        void show();

        void hide();
    }

    private View view;

    private PropertyEditorFieldInfo property;

    private Command okCommand;

    @Inject
    public SequenceGeneratorEditionDialog(View view) {
        this.view = view;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void show() {

        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        String sequenceName = (String) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME);
        String generatorName = (String) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.NAME);
        Object initialValue = fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE);
        Object allocationSize = fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE);

        view.setSequenceName(sequenceName);
        view.setGeneratorName(generatorName);
        view.setInitialValue(initialValue != null ? initialValue.toString() : null);
        view.clearInitialValueError();
        view.setAllocationSize(allocationSize != null ? allocationSize.toString() : null);
        view.clearAllocationSizeError();

        view.setEnabled(!fieldInfo.isDisabled());
        view.show();
    }

    @Override
    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    @Override
    public void setProperty(PropertyEditorFieldInfo property) {
        this.property = property;
    }

    void onOK() {
        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;
        fieldInfo.clearCurrentValues();
        String generatorName = view.getGeneratorName();
        if (generatorName != null && !generatorName.isEmpty()) {
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.NAME, generatorName);
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME, view.getSequenceName());
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE, getInitialValue());
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE, getAllocationSize());
        }

        view.hide();
        if (okCommand != null) {
            okCommand.execute();
        }
    }

    private Integer getInitialValue() {
        return isNotEmpty(view.getInitialValue()) ? Integer.parseInt(view.getInitialValue().trim()) : null;
    }

    private Integer getAllocationSize() {
        return isNotEmpty(view.getAllocationSize()) ? Integer.parseInt(view.getAllocationSize().trim()) : null;
    }

    void onCancel() {
        view.hide();
    }

    void onInitialValueChange() {
        view.clearInitialValueError();
        view.enableOkAction(true);
        if (isInvalidValidInteger(view.getInitialValue())) {
            view.enableOkAction(false);
            view.setInitialValueError(Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_initial_value_error());
        }
    }

    void onAllocationSizeChange() {
        view.clearAllocationSizeError();
        view.enableOkAction(true);
        if (isInvalidValidInteger(view.getAllocationSize())) {
            view.enableOkAction(false);
            view.setAllocationSizeError(Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_allocation_size_error());
        }
    }

    @Override
    public String getStringValue() {
        //return the value to show in the property editor simple text field.
        String value = view.getGeneratorName();
        if (value == null || value.isEmpty()) {
            value = SequenceGeneratorField.NOT_CONFIGURED_LABEL;
        }
        return value;
    }

    @Override
    public void setStringValue(String value) {
        //do nothing
    }

    private static boolean isInvalidValidInteger(String value) {
        if (isNotEmpty(value)) {
            try {
                Integer.parseInt(value.trim());
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
