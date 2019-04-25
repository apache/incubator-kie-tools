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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.CASCADE;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.FETCH;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.MAPPED_BY;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.OPTIONAL;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.ORPHAN_REMOVAL;
import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.RELATION_TYPE;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RelationshipEditionDialogTest {

    @Mock
    private RelationshipEditionDialog.View view;

    @Mock
    private DataModelerPropertyEditorFieldInfo fieldInfo;

    @Captor
    private ArgumentCaptor<List<CascadeType>> cascadeTypesCaptor;

    private RelationshipEditionDialog dialog;

    @Before
    public void setUp() {
        dialog = new RelationshipEditionDialog(view);
        dialog.init();
        verify(view).init(dialog);
    }

    @Test
    public void testAsWidget() {
        Widget widget = mock(Widget.class);
        when(view.asWidget()).thenReturn(widget);
        assertEquals(widget, dialog.asWidget());
    }

    @Test
    public void testShow() {
        RelationType relationTypeValue = RelationType.ONE_TO_MANY;
        List<CascadeType> cascadeTypeValue = Arrays.asList(CascadeType.DETACH, CascadeType.MERGE);
        FetchMode fetchModeValue = FetchMode.EAGER;
        Boolean optionalValue = Boolean.TRUE;
        String mappedByValue = "mappedByValue";
        Boolean orphanRemovalValue = Boolean.FALSE;
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.RELATION_TYPE)).thenReturn(relationTypeValue);
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.CASCADE)).thenReturn(cascadeTypeValue);
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.FETCH)).thenReturn(fetchModeValue);
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.OPTIONAL)).thenReturn(optionalValue);
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.MAPPED_BY)).thenReturn(mappedByValue);
        when(fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.ORPHAN_REMOVAL)).thenReturn(orphanRemovalValue);
        when(fieldInfo.isDisabled()).thenReturn(false);

        dialog.setProperty(fieldInfo);
        dialog.show();

        verify(view).setRelationType(relationTypeValue.name());
        verifyRelationDependentFields(relationTypeValue);
        verifyCascadeTypesWhereSet(cascadeTypeValue);
        verify(view).setFetchMode(fetchModeValue.name());
        verify(view).setOptional(optionalValue);
        verify(view).setMappedBy(mappedByValue);
        verify(view).setOrphanRemoval(orphanRemovalValue);
        verify(view).setEnabled(!fieldInfo.isDisabled());
        verify(view).show();
    }

    @Test
    public void testRelationDependentFieldsOneToOne() {
        dialog.enableRelationDependentFields(RelationType.ONE_TO_ONE);
        verifyRelationDependentFields(RelationType.ONE_TO_ONE);
    }

    @Test
    public void testRelationDependentFieldsOneToMany() {
        dialog.enableRelationDependentFields(RelationType.ONE_TO_MANY);
        verifyRelationDependentFields(RelationType.ONE_TO_MANY);
    }

    @Test
    public void testRelationDependentFieldsManyToOne() {
        dialog.enableRelationDependentFields(RelationType.MANY_TO_ONE);
        verifyRelationDependentFields(RelationType.MANY_TO_ONE);
    }

    @Test
    public void testRelationDependentFieldsManyToMany() {
        dialog.enableRelationDependentFields(RelationType.MANY_TO_MANY);
        verifyRelationDependentFields(RelationType.MANY_TO_MANY);
    }

    @Test
    public void testGetStringValue() {
        testGetStringValue("someValue", "someValue");
    }

    @Test
    public void testGetStringValueWhenNotSet() {
        testGetStringValue(RelationshipField.NOT_CONFIGURED_LABEL, UIUtil.NOT_SELECTED);
    }

    private void testGetStringValue(String expectedValue, String currentValue) {
        when(view.getRelationType()).thenReturn(currentValue);
        assertEquals(expectedValue, dialog.getStringValue());
    }

    @Test
    public void testOnOkRelationNotSelected() {
        prepareTestOnOK(null, Collections.singletonList(CascadeType.ALL), FetchMode.EAGER, Boolean.TRUE, "someMappedByValue", Boolean.TRUE);
        doTestOnOk();
        verify(fieldInfo, never()).setCurrentValue(anyString(), anyObject());
    }

    @Test
    public void testOnOKOneToOneSelected() {
        List<CascadeType> cascadeTypes = Arrays.asList(CascadeType.DETACH, CascadeType.MERGE);
        prepareTestOnOK(RelationType.ONE_TO_ONE, cascadeTypes, FetchMode.EAGER, Boolean.TRUE, "someMappedByValue", Boolean.TRUE);
        doTestOnOk();
        verify(fieldInfo).setCurrentValue(RELATION_TYPE, RelationType.ONE_TO_ONE);
        verify(fieldInfo).setCurrentValue(eq(CASCADE), cascadeTypesCaptor.capture());
        assertContainsSameValues(cascadeTypes, cascadeTypesCaptor.getValue());
        verify(fieldInfo).setCurrentValue(FETCH, FetchMode.EAGER);
        verify(fieldInfo).setCurrentValue(OPTIONAL, Boolean.TRUE);
        verify(fieldInfo).setCurrentValue(MAPPED_BY, "someMappedByValue");
        verify(fieldInfo).setCurrentValue(ORPHAN_REMOVAL, Boolean.TRUE);
    }

    @Test
    public void testOnOKOneToManySelected() {
        List<CascadeType> cascadeTypes = Arrays.asList(CascadeType.DETACH, CascadeType.MERGE);
        prepareTestOnOK(RelationType.ONE_TO_MANY, cascadeTypes, FetchMode.EAGER, Boolean.TRUE, "someMappedByValue", Boolean.TRUE);
        doTestOnOk();
        verify(fieldInfo).setCurrentValue(RELATION_TYPE, RelationType.ONE_TO_MANY);
        verify(fieldInfo).setCurrentValue(eq(CASCADE), cascadeTypesCaptor.capture());
        assertContainsSameValues(cascadeTypes, cascadeTypesCaptor.getValue());
        verify(fieldInfo).setCurrentValue(FETCH, FetchMode.EAGER);
        verify(fieldInfo, never()).setCurrentValue(eq(OPTIONAL), anyBoolean());
        verify(fieldInfo).setCurrentValue(MAPPED_BY, "someMappedByValue");
        verify(fieldInfo).setCurrentValue(ORPHAN_REMOVAL, Boolean.TRUE);
    }

    @Test
    public void testOnOKManyToOneSelected() {
        List<CascadeType> cascadeTypes = Arrays.asList(CascadeType.DETACH, CascadeType.MERGE);
        prepareTestOnOK(RelationType.MANY_TO_ONE, cascadeTypes, FetchMode.EAGER, Boolean.TRUE, "someMappedByValue", Boolean.TRUE);
        doTestOnOk();
        verify(fieldInfo).setCurrentValue(RELATION_TYPE, RelationType.MANY_TO_ONE);
        verify(fieldInfo).setCurrentValue(eq(CASCADE), cascadeTypesCaptor.capture());
        assertContainsSameValues(cascadeTypes, cascadeTypesCaptor.getValue());
        verify(fieldInfo).setCurrentValue(FETCH, FetchMode.EAGER);
        verify(fieldInfo).setCurrentValue(OPTIONAL, Boolean.TRUE);
        verify(fieldInfo, never()).setCurrentValue(eq(MAPPED_BY), anyString());
        verify(fieldInfo, never()).setCurrentValue(eq(ORPHAN_REMOVAL), anyString());
    }

    @Test
    public void testOnOKManyToManySelected() {
        List<CascadeType> cascadeTypes = Arrays.asList(CascadeType.DETACH, CascadeType.MERGE);
        prepareTestOnOK(RelationType.MANY_TO_MANY, cascadeTypes, FetchMode.EAGER, Boolean.TRUE, "someMappedByValue", Boolean.TRUE);
        doTestOnOk();
        verify(fieldInfo).setCurrentValue(RELATION_TYPE, RelationType.MANY_TO_MANY);
        verify(fieldInfo).setCurrentValue(eq(CASCADE), cascadeTypesCaptor.capture());
        assertContainsSameValues(cascadeTypes, cascadeTypesCaptor.getValue());
        verify(fieldInfo).setCurrentValue(FETCH, FetchMode.EAGER);
        verify(fieldInfo, never()).setCurrentValue(eq(OPTIONAL), anyString());
        verify(fieldInfo).setCurrentValue(MAPPED_BY, "someMappedByValue");
        verify(fieldInfo, never()).setCurrentValue(eq(ORPHAN_REMOVAL), anyString());
    }

    private void doTestOnOk() {
        Command command = mock(Command.class);
        dialog.setProperty(fieldInfo);
        dialog.setOkCommand(command);
        dialog.onOK();

        verify(view).hide();
        verify(command).execute();
    }

    private void prepareTestOnOK(RelationType relationType, List<CascadeType> cascadeTypes, FetchMode fetchMode,
                                 Boolean optional, String mappedBy, Boolean orphanRemoval) {
        when(view.getRelationType()).thenReturn(relationType != null ? relationType.name() : UIUtil.NOT_SELECTED);
        prepareCascadeTypes(cascadeTypes);
        when(view.getFetchMode()).thenReturn(fetchMode.name());
        when(view.getOptional()).thenReturn(optional);
        when(view.getMappedBy()).thenReturn(mappedBy);
        when(view.getOrphanRemoval()).thenReturn(orphanRemoval);
    }

    @Test
    public void testOnCancel() {
        dialog.onCancel();
        verify(view).hide();
    }

    @Test
    public void testOnRelationTypeChangedOneToOne() {
        testOnRelationTypeChanged(RelationType.ONE_TO_ONE);
    }

    @Test
    public void testOnRelationTypeChangedOneToMany() {
        testOnRelationTypeChanged(RelationType.ONE_TO_MANY);
    }

    @Test
    public void testOnRelationTypeChangedManyToOne() {
        testOnRelationTypeChanged(RelationType.MANY_TO_ONE);
    }

    @Test
    public void testOnRelationTypeChangedManyToMany() {
        testOnRelationTypeChanged(RelationType.MANY_TO_MANY);
    }

    private void testOnRelationTypeChanged(RelationType relationType) {
        when(view.getRelationType()).thenReturn(relationType.name());
        dialog.onRelationTypeChanged();
        verifyRelationDependentFields(relationType);
    }

    @Test
    public void testOnCascadeAllChangedTrue() {
        when(view.getCascadeAll()).thenReturn(true);
        dialog.onCascadeAllChanged();
        verifyCascadeTypesEnabled(1, true, false, false, false, false, false);
        verifyCascadeTypesValues(true, true, true, true, true);
    }

    @Test
    public void testOnCascadeAllChangedFalse() {
        when(view.getCascadeAll()).thenReturn(false);
        dialog.onCascadeAllChanged();
        verifyCascadeTypesEnabled(1, true, true, true, true, true, true);
        verify(view, never()).setCascadePersist(anyBoolean());
        verify(view, never()).setCascadeMerge(anyBoolean());
        verify(view, never()).setCascadeRemove(anyBoolean());
        verify(view, never()).setCascadeRefresh(anyBoolean());
        verify(view, never()).setCascadeDetach(anyBoolean());
    }

    @Test
    public void testOnCascadeAllChangedFalse2() {
        when(view.getCascadeAll()).thenReturn(false);
        dialog.onCascadeAllChanged();
        dialog.onCascadeAllChanged();
        verifyCascadeTypesEnabled(2, true, true, true, true, true, true);
        verifyCascadeTypesValues(false, false, false, false, false);
    }

    private void verifyRelationDependentFields(RelationType relationType) {
        switch (relationType) {
            case ONE_TO_ONE:
                verify(view).setOptionalVisible(true);
                verify(view).setMappedByVisible(true);
                verify(view).setOrphanRemovalVisible(true);
                break;
            case ONE_TO_MANY:
                verify(view).setOptionalVisible(false);
                verify(view).setMappedByVisible(true);
                verify(view).setOrphanRemovalVisible(true);
                break;
            case MANY_TO_ONE:
                verify(view).setOptionalVisible(true);
                verify(view).setMappedByVisible(false);
                verify(view).setOrphanRemovalVisible(false);
                break;
            case MANY_TO_MANY:
                verify(view).setOptionalVisible(false);
                verify(view).setMappedByVisible(true);
                verify(view).setOrphanRemovalVisible(false);
        }
    }

    private void verifyCascadeTypesWhereSet(List<CascadeType> cascadeTypes) {
        verify(view).setCascadeAll(cascadeTypes != null && cascadeTypes.contains(CascadeType.ALL));
        verify(view).setCascadePersist(cascadeTypes != null && cascadeTypes.contains(CascadeType.PERSIST));
        verify(view).setCascadeMerge(cascadeTypes != null && cascadeTypes.contains(CascadeType.MERGE));
        verify(view).setCascadeRemove(cascadeTypes != null && cascadeTypes.contains(CascadeType.REMOVE));
        verify(view).setCascadeRefresh(cascadeTypes != null && cascadeTypes.contains(CascadeType.REFRESH));
        verify(view).setCascadeDetach(cascadeTypes != null && cascadeTypes.contains(CascadeType.DETACH));
    }

    private void prepareCascadeTypes(List<CascadeType> cascadeTypes) {
        when(view.getCascadeAll()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.ALL));
        when(view.getCascadePersist()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.PERSIST));
        when(view.getCascadeMerge()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.MERGE));
        when(view.getCascadeRemove()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.REMOVE));
        when(view.getCascadeRefresh()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.REFRESH));
        when(view.getCascadeDetach()).thenReturn(cascadeTypes != null && cascadeTypes.contains(CascadeType.DETACH));
    }

    private void verifyCascadeTypesEnabled(int times, boolean cascadeAllEnabled, boolean cascadePersistEnabled, boolean cascadeMergeEnabled,
                                           boolean cascadeRemoveEnabled, boolean cascadeRefreshEnabled, boolean cascadeDetachEnabled) {
        verify(view, times(times)).enableCascadeAll(cascadeAllEnabled);
        verify(view, times(times)).enableCascadePersist(cascadePersistEnabled);
        verify(view, times(times)).enableCascadeMerge(cascadeMergeEnabled);
        verify(view, times(times)).enableCascadeRemove(cascadeRemoveEnabled);
        verify(view, times(times)).enableCascadeRefresh(cascadeRefreshEnabled);
        verify(view, times(times)).enableCascadeDetach(cascadeDetachEnabled);
    }

    private void verifyCascadeTypesValues(boolean cascadePersist, boolean cascadeMerge, boolean cascadeRemove,
                                          boolean cascadeRefresh, boolean cascadeDetach) {
        verify(view).setCascadePersist(cascadePersist);
        verify(view).setCascadeMerge(cascadeMerge);
        verify(view).setCascadeRemove(cascadeRemove);
        verify(view).setCascadeRefresh(cascadeRefresh);
        verify(view).setCascadeDetach(cascadeDetach);
    }

    private void assertContainsSameValues(List<CascadeType> expectedCascadeTypes, List<CascadeType> cascadeTypes) {
        assertEquals(expectedCascadeTypes.size(), cascadeTypes.size());
        expectedCascadeTypes.forEach(cascadeTypes::contains);
    }
}
