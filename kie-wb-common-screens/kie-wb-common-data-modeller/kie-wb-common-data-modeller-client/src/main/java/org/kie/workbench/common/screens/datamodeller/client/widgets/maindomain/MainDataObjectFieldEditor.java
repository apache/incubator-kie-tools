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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.Command;

@Dependent
public class MainDataObjectFieldEditor
        extends FieldEditor
        implements MainDataObjectFieldEditorView.Presenter {

    private MainDataObjectFieldEditorView view;

    private ValidatorService validatorService;

    private Caller<DataModelerService> modelerService;

    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    @Inject
    public MainDataObjectFieldEditor(MainDataObjectFieldEditorView view,
                                     DomainHandlerRegistry handlerRegistry,
                                     Event<DataModelerEvent> dataModelerEvent,
                                     DataModelCommandBuilder commandBuilder,
                                     ValidatorService validatorService,
                                     Caller<DataModelerService> modelerService,
                                     ShowAssetUsagesDisplayer showAssetUsagesDisplayer) {
        super(handlerRegistry,
              dataModelerEvent,
              commandBuilder);
        this.view = view;
        this.validatorService = validatorService;
        this.modelerService = modelerService;
        this.showAssetUsagesDisplayer = showAssetUsagesDisplayer;
        view.init(this);
    }

    @PostConstruct
    protected void init() {
        setReadonly(true);
    }

    public void onContextChange(DataModelerContext context) {
        this.context = context;
        initTypeList();
        view.setMultipleTypeEnabled(false);
        super.onContextChange(context);
    }

    @Override
    public String getName() {
        return "MAIN_FIELD_EDITOR";
    }

    @Override
    public String getDomainName() {
        return MainDomainEditor.MAIN_DOMAIN;
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);
        view.setReadonly(readonly);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onNameChange() {
        if (getObjectField() == null) {
            return;
        }
        view.setNameOnError(false);

        final String oldValue = getObjectField().getName();
        final String newValue = DataModelerUtils.unCapitalize(view.getName());

        final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
        final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

        if (originalClassName != null) {
            showAssetUsagesDisplayer.showAssetPartUsages(Constants.INSTANCE.modelEditor_confirm_renaming_of_used_field(oldValue),
                                                         currentPath,
                                                         originalClassName,
                                                         oldValue,
                                                         PartType.FIELD,
                                                         () -> doFieldNameChange(oldValue,
                                                                                 newValue),
                                                         () -> view.setName(oldValue));
        } else {
            doFieldNameChange(oldValue,
                              newValue);
        }
    }

    @Override
    public void onLabelChange() {
        if (getObjectField() != null) {
            String value = DataModelerUtils.nullTrim(view.getLabel());
            DataModelCommand command = commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(),
                                                                                             getName(),
                                                                                             getDataObject(),
                                                                                             getObjectField(),
                                                                                             MainDomainAnnotations.LABEL_ANNOTATION,
                                                                                             MainDomainAnnotations.VALUE_PARAM,
                                                                                             value,
                                                                                             true);
            command.execute();
        }
    }

    @Override
    public void onDescriptionChange() {
        if (getObjectField() != null) {
            String value = DataModelerUtils.nullTrim(view.getDescription());
            DataModelCommand command = commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(),
                                                                                             getName(),
                                                                                             getDataObject(),
                                                                                             getObjectField(),
                                                                                             MainDomainAnnotations.DESCRIPTION_ANNOTATION,
                                                                                             MainDomainAnnotations.VALUE_PARAM,
                                                                                             value,
                                                                                             true);
            command.execute();
        }
    }

    @Override
    public void onTypeChange() {
        doTypeChange(view.getType(),
                     view.getMultipleType());
    }

    @Override
    public void onTypeMultipleChange() {
        doTypeChange(view.getType(),
                     view.getMultipleType());
    }

    private void onDataObjectChange(@Observes DataObjectChangeEvent event) {
        if (event.isFromContext(context != null ? context.getContextId() : null)) {
            if ("name".equals(event.getValueName()) ||
                    "packageName".equals(event.getValueName()) ||
                    "label".equals(event.getValueName())) {

                initTypeList();
            }
        }
    }

    @Override
    protected void loadDataObjectField(DataObject dataObject,
                                       ObjectProperty objectField) {
        clear();
        setReadonly(true);
        if (dataObject != null && objectField != null) {
            this.dataObject = dataObject;
            this.objectField = objectField;
            initTypeList();

            view.setName(getObjectField().getName());

            Annotation annotation = objectField.getAnnotation(MainDomainAnnotations.LABEL_ANNOTATION);
            if (annotation != null) {
                view.setLabel(AnnotationValueHandler.getStringValue(annotation,
                                                                    MainDomainAnnotations.VALUE_PARAM));
            }

            annotation = objectField.getAnnotation(MainDomainAnnotations.DESCRIPTION_ANNOTATION);
            if (annotation != null) {
                view.setDescription(AnnotationValueHandler.getStringValue(annotation,
                                                                          MainDomainAnnotations.VALUE_PARAM));
            }

            setReadonly(getContext() == null || getContext().isReadonly());
        } else {
            initTypeList();
        }
    }

    private void doFieldNameChange(final String oldValue,
                                   final String newValue) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                view.setNameOnError(true);
                view.selectAllNameText();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if (oldValue.equalsIgnoreCase(view.getName())) {
            view.setName(oldValue);
            view.setNameOnError(false);
            return;
        }

        validatorService.isValidIdentifier(newValue,
                                           new ValidatorCallback() {
                                               @Override
                                               public void onFailure() {
                                                   view.showErrorPopup(Constants.INSTANCE.validation_error_invalid_object_attribute_identifier(newValue),
                                                                       null,
                                                                       afterCloseCommand);
                                               }

                                               @Override
                                               public void onSuccess() {
                                                   validatorService.isUniqueAttributeName(newValue,
                                                                                          getDataObject(),
                                                                                          new ValidatorWithReasonCallback() {

                                                                                              @Override
                                                                                              public void onFailure() {
                                                                                                  showFailure(ValidatorService.MANAGED_PROPERTY_EXISTS);
                                                                                              }

                                                                                              @Override
                                                                                              public void onFailure(String reason) {
                                                                                                  showFailure(reason);
                                                                                              }

                                                                                              private void showFailure(String reason) {
                                                                                                  if (ValidatorService.UN_MANAGED_PROPERTY_EXISTS.equals(reason)) {
                                                                                                      ObjectProperty unmanagedProperty = getDataObject().getUnManagedProperty(newValue);
                                                                                                      view.showErrorPopup(Constants.INSTANCE.validation_error_object_un_managed_attribute_already_exists(unmanagedProperty.getName(),
                                                                                                                                                                                                         unmanagedProperty.getClassName()));
                                                                                                  } else {
                                                                                                      view.showErrorPopup(Constants.INSTANCE.validation_error_object_attribute_already_exists(newValue));
                                                                                                  }
                                                                                              }

                                                                                              @Override
                                                                                              public void onSuccess() {
                                                                                                  view.setNameOnError(false);
                                                                                                  objectField.setName(newValue);
                                                                                                  notifyChange(createFieldChangeEvent(ChangeType.FIELD_NAME_CHANGE)
                                                                                                                       .withOldValue(oldValue)
                                                                                                                       .withNewValue(newValue));
                                                                                              }
                                                                                          });
                                               }
                                           });
    }

    private void doTypeChange(String newType,
                              boolean isMultiple) {

        if (getObjectField() != null) {

            boolean multiple = isMultiple;

            if (getContext().getHelper().isPrimitiveType(newType)) {
                view.setMultipleTypeEnabled(false);
                view.setMultipleType(false);
                multiple = false;
            } else {
                view.setMultipleTypeEnabled(true);
            }

            DataModelCommand command = commandBuilder.buildChangeTypeCommand(getContext(),
                                                                             getName(),
                                                                             getDataObject(),
                                                                             getObjectField(),
                                                                             newType,
                                                                             multiple);
            command.execute();
            executePostCommandProcessing(command);
        }
    }

    private void initTypeList() {

        String currentFieldType = null;
        boolean currentFieldTypeMultiple = false;
        view.setMultipleTypeEnabled(true);
        view.setMultipleType(false);

        if (getDataModel() != null) {
            if (getDataObject() != null && getObjectField() != null) {
                currentFieldType = getObjectField().getClassName();
                currentFieldTypeMultiple = getObjectField().isMultiple();
                if (getContext().getHelper().isPrimitiveType(currentFieldType)) {
                    view.setMultipleTypeEnabled(false);
                    view.setMultipleType(false);
                } else {
                    view.setMultipleType(currentFieldTypeMultiple);
                }
            }

            List<Pair<String, String>> typeList = DataModelerUtils.buildFieldTypeOptions(getContext().getHelper().getOrderedBaseTypes().values(),
                                                                                         getDataModel().getDataObjects(),
                                                                                         getDataModel().getJavaEnums(),
                                                                                         getDataModel().getExternalClasses(),
                                                                                         getDataModel().getDependencyJavaEnums(),
                                                                                         currentFieldType,
                                                                                         false);
            view.initTypeList(typeList,
                              currentFieldType,
                              false);
        } else {
            view.initTypeList(new ArrayList<Pair<String, String>>(),
                              null,
                              false);
        }
    }

    public void refreshTypeList(boolean keepSelection) {
        String selectedValue = view.getType();
        initTypeList();
        if (keepSelection && selectedValue != null) {
            view.setType(selectedValue);
        }
    }

    public void clear() {
        view.setNameOnError(false);
        view.setName(null);
        view.setLabel(null);
        view.setDescription(null);
        view.setType("");
    }
}