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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.CASCADE;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.FETCH;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.MAPPED_BY;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.OPTIONAL;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.ORPHAN_REMOVAL;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.RELATION_TYPE;

@Dependent
public class RelationshipEditionDialog
        implements PropertyEditionPopup {

    public interface View extends IsWidget {

        void init(RelationshipEditionDialog presenter);

        String getRelationType();

        void setRelationType(String relationType);

        String getFetchMode();

        void setFetchMode(String fetchMode);

        boolean getOptional();

        void setOptional(boolean optional);

        String getMappedBy();

        void setMappedBy(String mappedBy);

        boolean getOrphanRemoval();

        void setOrphanRemoval(boolean orphanRemoval);

        boolean getCascadeAll();

        void setCascadeAll(boolean cascadeAll);

        boolean getCascadePersist();

        void setCascadePersist(boolean cascadePersist);

        boolean getCascadeMerge();

        void setCascadeMerge(boolean cascadeMerge);

        boolean getCascadeRemove();

        void setCascadeRemove(boolean cascadeRemove);

        boolean getCascadeRefresh();

        void setCascadeRefresh(boolean cascadeRefresh);

        boolean getCascadeDetach();

        void setCascadeDetach(boolean cascadeDetach);

        void setEnabled(boolean enabled);

        void enableCascadeAll(boolean enabled);

        void enableCascadePersist(boolean enabled);

        void enableCascadeMerge(boolean enabled);

        void enableCascadeRemove(boolean enable);

        void enableCascadeRefresh(boolean enable);

        void enableCascadeDetach(boolean enable);

        void setOptionalVisible(boolean visible);

        void setOrphanRemovalVisible(boolean visible);

        void setMappedByVisible(boolean visible);

        void show();

        void hide();
    }

    private View view;

    private PropertyEditorFieldInfo property;

    private Command okCommand;

    private boolean cascadeAllWasClicked = false;

    @Inject
    public RelationshipEditionDialog(View view) {
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
    @SuppressWarnings("unchecked")
    public void show() {
        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        RelationType relationTypeValue = (RelationType) fieldInfo.getCurrentValue(RELATION_TYPE);
        if (relationTypeValue != null) {
            view.setRelationType(relationTypeValue.name());
        } else {
            view.setRelationType(UIUtil.NOT_SELECTED);
        }

        enableRelationDependentFields(relationTypeValue);

        cascadeAllWasClicked = false;
        setCascadeTypes((List<CascadeType>) fieldInfo.getCurrentValue(CASCADE));
        enableCascadeTypes(true, true);

        FetchMode fetchModeValue = (FetchMode) fieldInfo.getCurrentValue(FETCH);

        if (fetchModeValue != null) {
            view.setFetchMode(fetchModeValue.name());
        } else {
            view.setFetchMode(UIUtil.NOT_SELECTED);
        }

        Boolean optionalValue = (Boolean) fieldInfo.getCurrentValue(OPTIONAL);
        if (optionalValue != null) {
            view.setOptional(optionalValue);
        }

        String mappedBy = (String) fieldInfo.getCurrentValue(MAPPED_BY);
        view.setMappedBy(mappedBy);

        Boolean orphanRemovalValue = (Boolean) fieldInfo.getCurrentValue(ORPHAN_REMOVAL);
        if (orphanRemovalValue != null) {
            view.setOrphanRemoval(orphanRemovalValue);
        }

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
        return UIUtil.NOT_SELECTED.equals(view.getRelationType()) ? RelationshipField.NOT_CONFIGURED_LABEL : view.getRelationType();
    }

    @Override
    public void setStringValue(String value) {
        //do nothing
    }

    void onRelationTypeChanged() {
        String strValue = view.getRelationType();
        if (!UIUtil.NOT_SELECTED.equals(strValue)) {
            RelationType type = RelationType.valueOf(view.getRelationType());
            enableRelationDependentFields(type);
        }
    }

    void onCascadeAllChanged() {
        if (view.getCascadeAll()) {
            enableCascadeTypes(true, false);
            view.setCascadePersist(true);
            view.setCascadeMerge(true);
            view.setCascadeRemove(true);
            view.setCascadeRefresh(true);
            view.setCascadeDetach(true);
        } else {
            enableCascadeTypes(true, true);
            if (cascadeAllWasClicked) {
                //if cascade is clicked for second time then we can enable the auto disabling mode
                view.setCascadePersist(false);
                view.setCascadeMerge(false);
                view.setCascadeRemove(false);
                view.setCascadeRefresh(false);
                view.setCascadeDetach(false);
            }
        }
        cascadeAllWasClicked = true;
    }

    void onOK() {
        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;
        fieldInfo.clearCurrentValues();
        String relationType = view.getRelationType();
        if (!relationType.equals(UIUtil.NOT_SELECTED)) {
            fieldInfo.setCurrentValue(RELATION_TYPE, RelationType.valueOf(relationType));
            fieldInfo.setCurrentValue(CASCADE, getCascadeTypes());
            fieldInfo.setCurrentValue(FETCH, FetchMode.valueOf(view.getFetchMode()));

            if (relationType.equals(RelationType.ONE_TO_ONE.name()) ||
                    relationType.equals(RelationType.MANY_TO_ONE.name())) {
                fieldInfo.setCurrentValue(OPTIONAL, view.getOptional());
            }

            if (relationType.equals(RelationType.ONE_TO_ONE.name()) ||
                    relationType.equals(RelationType.ONE_TO_MANY.name()) ||
                    relationType.equals(RelationType.MANY_TO_MANY.name())) {
                fieldInfo.setCurrentValue(MAPPED_BY, view.getMappedBy());
            }

            if (relationType.equals(RelationType.ONE_TO_ONE.name()) ||
                    relationType.equals(RelationType.ONE_TO_MANY.name())) {
                fieldInfo.setCurrentValue(ORPHAN_REMOVAL, view.getOrphanRemoval());
            }
        }

        view.hide();
        if (okCommand != null) {
            okCommand.execute();
        }
    }

    void onCancel() {
        view.hide();
    }

    void enableRelationDependentFields(RelationType relationType) {
        if (relationType != null) {
            switch (relationType) {
                case ONE_TO_ONE:
                    view.setOptionalVisible(true);
                    view.setMappedByVisible(true);
                    view.setOrphanRemovalVisible(true);
                    break;
                case ONE_TO_MANY:
                    view.setOptionalVisible(false);
                    view.setMappedByVisible(true);
                    view.setOrphanRemovalVisible(true);
                    break;
                case MANY_TO_ONE:
                    view.setOptionalVisible(true);
                    view.setMappedByVisible(false);
                    view.setOrphanRemovalVisible(false);
                    break;
                case MANY_TO_MANY:
                    view.setOptionalVisible(false);
                    view.setMappedByVisible(true);
                    view.setOrphanRemovalVisible(false);
            }
        }
    }

    private void setCascadeTypes(List<CascadeType> cascadeTypes) {
        view.setCascadeAll(cascadeTypes != null && cascadeTypes.contains(CascadeType.ALL));
        view.setCascadePersist(cascadeTypes != null && cascadeTypes.contains(CascadeType.PERSIST));
        view.setCascadeMerge(cascadeTypes != null && cascadeTypes.contains(CascadeType.MERGE));
        view.setCascadeRemove(cascadeTypes != null && cascadeTypes.contains(CascadeType.REMOVE));
        view.setCascadeRefresh(cascadeTypes != null && cascadeTypes.contains(CascadeType.REFRESH));
        view.setCascadeDetach(cascadeTypes != null && cascadeTypes.contains(CascadeType.DETACH));
    }

    private List<CascadeType> getCascadeTypes() {
        List<CascadeType> cascadeTypes = new ArrayList<>();
        if (view.getCascadeAll()) {
            cascadeTypes.add(CascadeType.ALL);
            if (cascadeAllWasClicked) {
                //when cascade ALL was selected in the UI by intention, then it's the only option that we will
                //configure since it include the other available ones.
                return cascadeTypes;
            }
        }
        if (view.getCascadePersist()) {
            cascadeTypes.add(CascadeType.PERSIST);
        }
        if (view.getCascadeMerge()) {
            cascadeTypes.add(CascadeType.MERGE);
        }
        if (view.getCascadeRemove()) {
            cascadeTypes.add(CascadeType.REMOVE);
        }
        if (view.getCascadeRefresh()) {
            cascadeTypes.add(CascadeType.REFRESH);
        }
        if (view.getCascadeDetach()) {
            cascadeTypes.add(CascadeType.DETACH);
        }
        return cascadeTypes;
    }

    private void enableCascadeTypes(boolean enableCascadeAll,
                                    boolean enableTheRest) {
        view.enableCascadeAll(enableCascadeAll);
        view.enableCascadePersist(enableTheRest);
        view.enableCascadeMerge(enableTheRest);
        view.enableCascadeRemove(enableTheRest);
        view.enableCascadeRefresh(enableTheRest);
        view.enableCascadeDetach(enableTheRest);
    }
}