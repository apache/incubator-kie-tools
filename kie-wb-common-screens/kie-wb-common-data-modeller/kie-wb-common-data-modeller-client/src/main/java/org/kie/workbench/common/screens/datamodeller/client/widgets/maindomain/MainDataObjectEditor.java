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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

@Dependent
public class MainDataObjectEditor
        extends ObjectEditor
        implements MainDataObjectEditorView.Presenter {

    private MainDataObjectEditorView view;

    private ValidatorService validatorService;

    private Caller<DataModelerService> modelerService;

    private ShowAssetUsagesDisplayer showAssetUsagesDisplayer;

    @Inject
    public MainDataObjectEditor(MainDataObjectEditorView view,
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

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "MAIN_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return MainDomainEditor.MAIN_DOMAIN;
    }

    public void onContextChange(DataModelerContext context) {
        this.context = context;
        view.initPackageSelector(context);
        super.onContextChange(context);
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void refreshTypeList(boolean keepSelection) {
        initSuperClassList(keepSelection);
    }

    public void setReadonly(boolean readonly) {
        super.setReadonly(readonly);
        view.setReadonly(readonly);
    }

    @Override
    public void onNameChange() {
        if (getDataObject() != null) {

            view.setNameOnError(false);

            final String packageName = getDataObject().getPackageName();
            final String oldValue = getDataObject().getName();
            final String newValue = view.getName();

            final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
            final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

            if (originalClassName != null) {
                showAssetUsagesDisplayer.showAssetUsages(Constants.INSTANCE.modelEditor_confirm_renaming_of_used_class(originalClassName),
                                                         currentPath,
                                                         originalClassName,
                                                         ResourceType.JAVA,
                                                         () -> doClassNameChange(packageName,
                                                                                 oldValue,
                                                                                 newValue),
                                                         () -> view.setName(oldValue));
            } else {
                doClassNameChange(packageName,
                                  oldValue,
                                  newValue);
            }
        }
    }

    @Override
    public void onLabelChange() {
        if (getDataObject() != null) {
            String value = DataModelerUtils.nullTrim(view.getLabel());
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand(getContext(),
                                                                                                  getName(),
                                                                                                  getDataObject(),
                                                                                                  MainDomainAnnotations.LABEL_ANNOTATION,
                                                                                                  "value",
                                                                                                  value,
                                                                                                  true);
            command.execute();
        }
    }

    @Override
    public void onDescriptionChange() {
        if (getDataObject() != null) {
            String value = DataModelerUtils.nullTrim(view.getDescription());
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand(getContext(),
                                                                                                  getName(),
                                                                                                  getDataObject(),
                                                                                                  MainDomainAnnotations.DESCRIPTION_ANNOTATION,
                                                                                                  "value",
                                                                                                  value,
                                                                                                  true);
            command.execute();
        }
    }

    @Override
    public void onSuperClassChange() {
        if (getDataObject() != null) {

            view.setSuperClassOnError(false);

            final String newSuperClass = view.getSuperClass();
            final String oldSuperClass = getDataObject().getSuperClassName();

            // No change needed
            if ((("".equals(newSuperClass) || UIUtil.NOT_SELECTED.equals(newSuperClass)) && oldSuperClass == null) ||
                    newSuperClass.equals(oldSuperClass)) {
                return;
            }

            if (newSuperClass != null && !"".equals(newSuperClass) && !UIUtil.NOT_SELECTED.equals(newSuperClass)) {
                validatorService.canExtend(getContext(),
                                           getDataObject().getClassName(),
                                           newSuperClass,
                                           new ValidatorCallback() {
                                               @Override
                                               public void onFailure() {
                                                   view.showErrorPopup(Constants.INSTANCE.validation_error_cyclic_extension(getDataObject().getClassName(),
                                                                                                                            newSuperClass),
                                                                       null,
                                                                       new Command() {
                                                                           @Override
                                                                           public void execute() {
                                                                               view.setSuperClassOnError(true);
                                                                               view.setSuperClassOnFocus();
                                                                           }
                                                                       });
                                               }

                                               @Override
                                               public void onSuccess() {
                                                   commandBuilder.buildDataObjectSuperClassChangeCommand(getContext(),
                                                                                                         getName(),
                                                                                                         getDataObject(),
                                                                                                         newSuperClass).execute();

                                                   getDataObject().setSuperClassName(newSuperClass);
                                               }
                                           });
            } else {
                commandBuilder.buildDataObjectSuperClassChangeCommand(getContext(),
                                                                      getName(),
                                                                      getDataObject(),
                                                                      null).execute();
            }
        }
    }

    @Override
    public void onPackageAdded() {
        if (getDataObject() != null) {
            doPackageChange(view.getNewPackageName());
        }
    }

    @Override
    public void onPackageChange() {
        if (getDataObject() != null) {
            doPackageChange(view.getPackageName());
        }
    }

    public void doPackageChange(String packageName) {

        if (getDataObject() != null) {

            view.setPackageNameOnError(false);

            final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
            final String newPackageName = packageName != null && !"".equals(packageName) && !UIUtil.NOT_SELECTED.equals(packageName) ? packageName : null;
            final String oldPackageName = getDataObject().getPackageName();
            final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

            if ((oldPackageName != null && !oldPackageName.equals(newPackageName)) ||
                    (oldPackageName == null && newPackageName != null)) {
                //the user is trying to change the package name

                showAssetUsagesDisplayer.showAssetUsages(Constants.INSTANCE.modelEditor_confirm_package_change_of_used_class(originalClassName),
                                                         currentPath,
                                                         originalClassName,
                                                         ResourceType.JAVA,
                                                         () -> doPackageChange(oldPackageName,
                                                                               newPackageName),
                                                         () -> view.setPackageName(oldPackageName));
            } else {
                doPackageChange(oldPackageName,
                                newPackageName);
            }
        }
    }

    @Override
    protected void loadDataObject(DataObject dataObject) {
        clear();
        setReadonly(true);
        if (dataObject != null) {
            this.dataObject = dataObject;

            view.setName(dataObject.getName());

            Annotation annotation = dataObject.getAnnotation(MainDomainAnnotations.LABEL_ANNOTATION);
            if (annotation != null) {
                view.setLabel(AnnotationValueHandler.getStringValue(annotation,
                                                                    MainDomainAnnotations.VALUE_PARAM));
            }

            annotation = dataObject.getAnnotation(MainDomainAnnotations.DESCRIPTION_ANNOTATION);
            if (annotation != null) {
                view.setDescription(AnnotationValueHandler.getStringValue(annotation,
                                                                          MainDomainAnnotations.VALUE_PARAM));
            }

            view.setPackageName(dataObject.getPackageName());

            initSuperClassList(false);

            setReadonly(getContext() == null || getContext().isReadonly());
        }
    }

    private void initSuperClassList(boolean keepSelection) {
        String currentValue = keepSelection ? view.getSuperClass() : (dataObject != null ? dataObject.getSuperClassName() : null);
        view.initSuperClassList(
                DataModelerUtils.buildSuperclassOptions(getDataModel(),
                                                        dataObject),
                currentValue);
    }

    private void doClassNameChange(final String packageName,
                                   final String oldValue,
                                   final String newValue) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                view.setNameOnError(true);
                view.setAllNameNameText();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if (oldValue.equals(newValue)) {
            view.setNameOnError(false);
            return;
        }
        // Otherwise validate
        validatorService.isValidIdentifier(newValue,
                                           new ValidatorCallback() {
                                               @Override
                                               public void onFailure() {
                                                   view.showErrorPopup(Constants.INSTANCE.validation_error_invalid_object_identifier(newValue),
                                                                       null,
                                                                       afterCloseCommand);
                                               }

                                               @Override
                                               public void onSuccess() {
                                                   validatorService.isUniqueEntityName(packageName,
                                                                                       newValue,
                                                                                       getDataModel(),
                                                                                       new ValidatorCallback() {
                                                                                           @Override
                                                                                           public void onFailure() {
                                                                                               view.showErrorPopup(Constants.INSTANCE.validation_error_object_already_exists(newValue,
                                                                                                                                                                             packageName),
                                                                                                                   null,
                                                                                                                   afterCloseCommand);
                                                                                           }

                                                                                           @Override
                                                                                           public void onSuccess() {
                                                                                               view.setNameOnError(false);

                                                                                               commandBuilder.buildDataObjectNameChangeCommand(getContext(),
                                                                                                                                               getName(),
                                                                                                                                               getDataObject(),
                                                                                                                                               newValue).execute();
                                                                                           }
                                                                                       });
                                               }
                                           });
    }

    private void doPackageChange(String oldPackageName,
                                 String newPackageName) {

        commandBuilder.buildDataObjectPackageChangeCommand(getContext(),
                                                           getName(),
                                                           getDataObject(),
                                                           newPackageName).execute();
    }

    public void clear() {
        view.setNameOnError(false);
        view.setName(null);
        view.setLabel(null);
        view.setDescription(null);
        view.setPackageNameOnError(false);

        view.clearPackageList();
        view.clearSuperClassList();
        view.setSuperClassOnError(false);
    }
}