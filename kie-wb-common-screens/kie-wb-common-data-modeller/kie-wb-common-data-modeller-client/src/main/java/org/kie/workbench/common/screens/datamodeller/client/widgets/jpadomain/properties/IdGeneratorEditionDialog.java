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
package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

import static org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.IdGeneratorField.GENERATOR;
import static org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.IdGeneratorField.STRATEGY;

@Dependent
public class IdGeneratorEditionDialog
        implements PropertyEditionPopup {

    public interface View extends IsWidget {

        void init(IdGeneratorEditionDialog presenter);

        void setGeneratorType(String generatorType);

        String getGeneratorType();

        void setGeneratorName(String generatorName);

        String getGeneratorName();

        void setEnabled(boolean enabled);

        void show();

        void hide();
    }

    private View view;

    private PropertyEditorFieldInfo property;

    private Command okCommand;

    @Inject
    public IdGeneratorEditionDialog(View view) {
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
        String strategy = (String) fieldInfo.getCurrentValue(STRATEGY);
        String generator = (String) fieldInfo.getCurrentValue(GENERATOR);

        strategy = strategy != null ? strategy : UIUtil.NOT_SELECTED;
        view.setGeneratorType(strategy);
        view.setGeneratorName(generator);

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

    @Override
    public String getStringValue() {
        //return the value to show in the property editor simple text field.
        return UIUtil.NOT_SELECTED.equals(view.getGeneratorType()) ? IdGeneratorField.NOT_CONFIGURED_LABEL : view.getGeneratorType();
    }

    @Override
    public void setStringValue(String value) {
        //do nothing
    }

    void onOK() {
        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;
        fieldInfo.clearCurrentValues();
        String strategy = view.getGeneratorType();
        if (!UIUtil.NOT_SELECTED.equals(strategy)) {
            fieldInfo.setCurrentValue(STRATEGY, strategy);
            fieldInfo.setCurrentValue(GENERATOR, view.getGeneratorName());
        }

        view.hide();
        if (okCommand != null) {
            okCommand.execute();
        }
    }

    void onCancel() {
        view.hide();
    }
}
