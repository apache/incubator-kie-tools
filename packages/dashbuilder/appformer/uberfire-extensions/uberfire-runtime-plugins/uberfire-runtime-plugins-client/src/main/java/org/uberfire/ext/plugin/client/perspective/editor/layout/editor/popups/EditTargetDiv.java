/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.uberfire.ext.plugin.client.perspective.editor.layout.editor.TargetDivDragComponent.ID_PARAMETER;

public class EditTargetDiv
        extends BaseModal {

    public static String PROPERTY_EDITOR_KEY = "EditTargetDiv";
    private static Binder uiBinder = GWT.create(Binder.class);
    private final ModalConfigurationContext configContext;
    @UiField
    PropertyEditorWidget propertyEditor;
    private ButtonPressed buttonPressed = ButtonPressed.CLOSE;
    private Map<String, String> lastParametersSaved = new HashMap<String, String>();

    public EditTargetDiv(ModalConfigurationContext configContext) {
        this.configContext = configContext;
        setTitle(CommonConstants.INSTANCE.EditComponent());
        setBody(uiBinder.createAndBindUi(EditTargetDiv.this));
        propertyEditor.handle(generateEvent(generatedPropertyEditor()));
        saveOriginalState();
        add(new ModalFooterOKCancelButtons(
                    () -> okButton(),
                    () -> cancelButton()
            )
        );
        addHiddenHandler();
    }

    private void saveOriginalState() {
        lastParametersSaved = new HashMap<>();
        Map<String, String> layoutComponentProperties = configContext.getComponentProperties();
        for (String key : layoutComponentProperties.keySet()) {
            lastParametersSaved.put(key,
                                    layoutComponentProperties.get(key));
        }
    }

    protected void addHiddenHandler() {
        addHiddenHandler(hiddenEvent -> {
            if (userPressedCloseOrCancel()) {
                revertChanges();
                configContext.configurationCancelled();
            }
        });
    }

    private boolean userPressedCloseOrCancel() {
        return ButtonPressed.CANCEL.equals(buttonPressed) || ButtonPressed.CLOSE.equals(buttonPressed);
    }

    private void revertChanges() {
        configContext.resetComponentProperties();
        for (String key : lastParametersSaved.keySet()) {
            configContext.setComponentProperty(key,
                                               lastParametersSaved.get(key));
        }
    }

    public void show() {
        super.show();
    }

    void okButton() {
        buttonPressed = ButtonPressed.OK;

        if (configContext.getComponentProperty(ID_PARAMETER) == null) {
            configContext.setComponentProperty(ID_PARAMETER,
                                               generateRandomID());
            configContext.configurationFinished();
        } else {
            configContext.configurationFinished();
        }

        hide();
    }

    private String generateRandomID() {
        Date date = new Date();
        DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMddHHmmss");
        String randomID = "id-" + dtf.format(date,
                                             TimeZone.createTimeZone(0));
        return randomID;
    }

    void cancelButton() {
        buttonPressed = ButtonPressed.CANCEL;
        hide();
    }

    @Override
    public void hide() {
        super.hide();
    }

    private PropertyEditorCategory generatedPropertyEditor() {

        PropertyEditorCategory category = new PropertyEditorCategory(CommonConstants.INSTANCE.TargetDivConfiguration());

        final Map<String, String> parameters = configContext.getComponentProperties();
        String selectedID = parameters.get(ID_PARAMETER);

        category.withField(new PropertyEditorFieldInfo(CommonConstants.INSTANCE.TargetDivPlaceHolder(),
                                                       selectedID == null ? "" : selectedID,
                                                       PropertyEditorType.TEXT)
                                   .withKey(configContext.hashCode() + ID_PARAMETER));

        propertyEditor.addExpandedCategory(category.getName());
        return category;
    }

    private PropertyEditorEvent generateEvent(PropertyEditorCategory category) {
        PropertyEditorEvent event = new PropertyEditorEvent(PROPERTY_EDITOR_KEY,
                                                            category);
        return event;
    }

    protected ModalConfigurationContext getConfigContext() {
        return this.configContext;
    }

    interface Binder
            extends
            UiBinder<Widget, EditTargetDiv> {

    }
}