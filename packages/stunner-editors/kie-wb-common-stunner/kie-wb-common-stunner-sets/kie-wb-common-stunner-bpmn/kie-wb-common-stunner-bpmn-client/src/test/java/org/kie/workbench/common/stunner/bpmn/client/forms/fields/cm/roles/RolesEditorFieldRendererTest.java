/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.kie.workbench.common.stunner.bpmn.forms.model.cm.RolesEditorFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.serializer.cm.CaseRoleSerializer;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RolesEditorFieldRendererTest {

    private RolesEditorFieldRenderer tested;

    @Mock
    private RolesEditorWidgetView view;

    private ManagedInstanceStub<FormGroup> formGroupsInstance;

    @Mock
    private DefaultFormGroup formGroup;

    private CaseRoleSerializer caseRoleSerializer;

    public static final String SERIALIZED_ROLE = "role:1";

    public static final KeyValueRow ROLE = new KeyValueRow("role", "1");

    private List<KeyValueRow> rows;

    @Before
    public void setUp() throws Exception {
        rows = new ArrayList<>();
        rows.add(ROLE);
        caseRoleSerializer = spy(new CaseRoleSerializer());
        tested = spy(new RolesEditorFieldRenderer(view, caseRoleSerializer));
        formGroupsInstance = new ManagedInstanceStub<>(formGroup);

        final Field formGroupsInstanceField = FieldRenderer.class.getDeclaredField("formGroupsInstance");
        formGroupsInstanceField.setAccessible(true);
        formGroupsInstanceField.set(tested, formGroupsInstance);
    }

    @Test
    public void getName() {
        String name = tested.getName();
        assertThat(name).isEqualTo(RolesEditorFieldDefinition.FIELD_TYPE.getTypeName());
    }

    @Test
    public void getFormGroup() {
        FormGroup formGroup = tested.getFormGroup(RenderMode.EDIT_MODE);
        verify(view).init(tested);
        assertThat(formGroup).isInstanceOf(DefaultFormGroup.class);
    }

    @Test
    public void setReadOnly() {
        tested.setReadOnly(true);
        verify(view).setReadOnly(true);
    }

    @Test
    public void deserialize() {
        final List<KeyValueRow> deserialized = tested.deserialize(SERIALIZED_ROLE);
        verify(caseRoleSerializer).deserialize(eq(SERIALIZED_ROLE), any());
        assertThat(deserialized).usingElementComparator(Comparator.comparing(KeyValueRow::getKey)).isEqualTo(rows);
        assertThat(deserialized).usingElementComparator(Comparator.comparing(KeyValueRow::getValue)).isEqualTo(rows);
    }

    @Test
    public void serialize() {
        final String serialized = tested.serialize(rows);
        verify(caseRoleSerializer).serialize(eq(Optional.ofNullable(rows)), any(), any());
        assertThat(serialized).isEqualTo(SERIALIZED_ROLE);
    }
}