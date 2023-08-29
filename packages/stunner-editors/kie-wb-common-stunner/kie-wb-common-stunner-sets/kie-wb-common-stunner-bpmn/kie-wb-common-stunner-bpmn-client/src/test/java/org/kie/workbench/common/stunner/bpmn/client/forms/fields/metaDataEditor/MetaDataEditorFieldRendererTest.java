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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataAttribute;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MetaDataEditorFieldRendererTest {

    @Mock
    private MetaDataEditorWidgetView metaDataEditorWidgetView;

    @Mock
    private MetaDataListItemWidgetView metaDataListItemWidgetView;

    @Mock
    private SessionManager abstractClientSessionManager;

    @Mock
    private Graph graph;

    @Mock
    private MetaDataRow metaDataRow;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private EditorSession clientFullSession;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    @Mock
    private ManagedInstance<DefaultFormGroup> formGroupsInstanceMock;

    @Mock
    private DefaultFormGroup formGroup;

    private MetaDataEditorFieldRenderer metaDataEditor;

    @Before
    public void setup() {
        when(formGroupsInstanceMock.get()).thenReturn(formGroup);
        metaDataEditor = new MetaDataEditorFieldRenderer(metaDataEditorWidgetView,
                                                         abstractClientSessionManager,
                                                         notification) {
            {
                formGroupsInstance = formGroupsInstanceMock;
            }
        };
    }

    @Test
    public void testGetName() {
        assertEquals("MetaDataEditor", metaDataEditor.getName());
    }

    @Test
    public void testAddAttribute() {
        List<MetaDataRow> rows = new ArrayList<>();
        MetaDataRow metaDataRow = new MetaDataRow();
        rows.add(metaDataRow);

        when(metaDataEditorWidgetView.getMetaDataRows()).thenReturn(rows);

        when(metaDataEditorWidgetView.getMetaDataWidget(anyInt())).thenReturn(metaDataListItemWidgetView);
        when(metaDataEditorWidgetView.getMetaDataRowsCount()).thenReturn(1);
        metaDataEditor.addAttribute();
        verify(metaDataEditorWidgetView,
               times(0)).setTableDisplayStyle();
        verify(metaDataEditorWidgetView,
               times(1)).getMetaDataRowsCount();
        verify(metaDataEditorWidgetView,
               times(1)).getMetaDataWidget(0);
    }

    @Test
    public void testAddAttributeRowsEmpty() {
        when(metaDataEditorWidgetView.getMetaDataRows()).thenReturn(new ArrayList<>());
        when(metaDataEditorWidgetView.getMetaDataWidget(anyInt())).thenReturn(mock(MetaDataListItemWidgetView.class));
        metaDataEditor.addAttribute();
        verify(metaDataEditorWidgetView,
               times(1)).setTableDisplayStyle();
    }

    @Test
    public void testNotifyModelChanged() {
        metaDataEditor.notifyModelChanged();
        verify(metaDataEditorWidgetView,
               times(1)).doSave();
    }

    @Test
    public void testRemoveMetaData() {
        prepareRemoveVariableTest();
        metaDataEditor.removeMetaData(metaDataRow);
        verify(metaDataEditorWidgetView, times(2)).getMetaDataRows();
        verify(metaDataEditorWidgetView).doSave();
    }

    private void prepareRemoveVariableTest() {
        when(metaDataEditorWidgetView.getMetaDataWidget(anyInt())).thenReturn(metaDataListItemWidgetView);
        when(metaDataEditorWidgetView.getMetaDataRowsCount()).thenReturn(1);
        when(metaDataRow.getAttribute()).thenReturn("attributeName");
        when(abstractClientSessionManager.getCurrentSession()).thenReturn(clientFullSession);
        when(clientFullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);

        metaDataEditor.getFormGroup(RenderMode.EDIT_MODE);
        metaDataEditor.addAttribute();
    }

    @Test
    public void testDeserializeMetaData() {
        List<MetaDataRow> metaDataRows = metaDataEditor.deserializeMetaDataAttributes("att1ßval1Øatt2ßval2Øatt3ßval3,val4");
        assertEquals(3,
                     metaDataRows.size());
        MetaDataRow att = metaDataRows.get(0);
        assertEquals("att1",
                     att.getAttribute());
        assertEquals("val1",
                     att.getValue());
        att = metaDataRows.get(1);
        assertEquals("att2",
                     att.getAttribute());
        assertEquals("val2",
                     att.getValue());
        att = metaDataRows.get(2);
        assertEquals("att3",
                     att.getAttribute());
        assertEquals("val3,val4",
                     att.getValue());
    }

    @Test
    public void testDeserializeMetaDataPermutations() {
        List<MetaDataRow> metaDataRows = metaDataEditor.deserializeMetaDataAttributes(null);
        assertEquals(0,
                     metaDataRows.size());
        List<MetaDataRow> metaDataRows2 = metaDataEditor.deserializeMetaDataAttributes("");
        assertEquals(0,
                     metaDataRows2.size());
        List<MetaDataRow> metaDataRows3 = metaDataEditor.deserializeMetaDataAttributes("ßvalue");
        assertEquals(0,
                     metaDataRows3.size());
        List<MetaDataRow> metaDataRows4 = metaDataEditor.deserializeMetaDataAttributes("att1ßval1Øatt2");
        assertEquals(2,
                     metaDataRows4.size());
        List<MetaDataRow> metaDataRows5 = metaDataEditor.deserializeMetaDataAttributes("att1ßval1Øatt2Øatt3ßval3,val4");
        assertEquals(3,
                     metaDataRows5.size());
        List<MetaDataRow> metaDataRows6 = metaDataEditor.deserializeMetaDataAttributes("att1ßval1Øatt2Øßval3,val4");
        assertEquals(2,
                     metaDataRows6.size());

        List<MetaDataRow> metaDataRows7 = metaDataEditor.deserializeMetaDataAttributes("ßØØatt1ßvall");
        assertEquals(1,
                     metaDataRows7.size());

        List<MetaDataRow> metaDataRows8 = metaDataEditor.deserializeMetaDataAttributes("ßØØßvall");
        assertEquals(0,
                     metaDataRows8.size());
    }

    @Test
    public void testSerializeMetaData() {
        List<MetaDataRow> metaDataRows = new ArrayList<>();
        metaDataRows.add(new MetaDataRow("att1", "val1"));
        metaDataRows.add(new MetaDataRow("att2", "val2"));
        metaDataRows.add(new MetaDataRow("att3", "val3,val4"));
        String s = metaDataEditor.serializeMetaDataAttributes(metaDataRows);
        assertEquals("att1ßval1Øatt2ßval2Øatt3ßval3,val4", s);
    }

    @Test
    public void testSerializeMetaDataAttributeNull() {
        List<MetaDataRow> metaDataRows = new ArrayList<>();
        metaDataRows.add(new MetaDataRow(null, "val1"));
        String s = metaDataEditor.serializeMetaDataAttributes(metaDataRows);
        assertEquals("", s);
    }

    @Test
    public void testSerializeMetaDataAttributeEmpty() {
        List<MetaDataRow> metaDataRows = new ArrayList<>();
        metaDataRows.add(new MetaDataRow("", "val1"));
        String s = metaDataEditor.serializeMetaDataAttributes(metaDataRows);
        assertEquals("", s);
    }

    @Test
    public void testIsDuplicateAttribute() {
        List<MetaDataRow> metaDataRows = new ArrayList<>();
        metaDataRows.add(new MetaDataRow("att1", "val1"));
        metaDataRows.add(new MetaDataRow("att2", "val2"));
        metaDataRows.add(new MetaDataRow("att3", "val3,val4"));
        metaDataRows.add(new MetaDataRow("att2", "val2"));
        metaDataRows.add(new MetaDataRow(null, "Object"));
        metaDataRows.add(new MetaDataRow(null, null));
        when(metaDataEditorWidgetView.getMetaDataRows()).thenReturn(metaDataRows);
        assertTrue(metaDataEditor.isDuplicateAttribute("att2"));
        assertFalse(metaDataEditor.isDuplicateAttribute("att1"));
    }

    @Test
    public void testIsDuplicateAttributeNullOrEmpty() {
        assertFalse(metaDataEditor.isDuplicateAttribute(null));
        assertFalse(metaDataEditor.isDuplicateAttribute("     "));
    }

    @Test
    public void testIsDuplicateRowsNull() {
        when(metaDataEditorWidgetView.getMetaDataRows()).thenReturn(null);
        assertFalse(metaDataEditor.isDuplicateAttribute("test"));
    }

    @Test
    public void testIsDuplicateRowsEmpty() {
        when(metaDataEditorWidgetView.getMetaDataRows()).thenReturn(new ArrayList<>());
        assertFalse(metaDataEditor.isDuplicateAttribute("test"));
    }

    @Test
    public void testSetReadOnlyTrue() {
        metaDataEditor.setReadOnly(true);
        verify(metaDataEditorWidgetView,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        metaDataEditor.setReadOnly(false);
        verify(metaDataEditorWidgetView,
               times(1)).setReadOnly(false);
    }

    @Test
    public void testMetadataAttribute() {
        MetaDataAttribute metaDataAttribute = new MetaDataAttribute();
        MetaDataAttribute metaDataAttribute2 = new MetaDataAttribute("");
        MetaDataAttribute metaDataAttribute3 = new MetaDataAttribute("att1", null);
        MetaDataAttribute metaDataAttribute4 = new MetaDataAttribute("att1", "");

        assertEquals("", metaDataAttribute.toString());
        assertEquals("", metaDataAttribute2.toString());
        assertEquals("att1", metaDataAttribute3.toString());
        assertEquals("att1", metaDataAttribute4.toString());
    }

    @Test
    public void testMetadataAttributeEquals() {
        MetaDataAttribute metaDataAttribute = new MetaDataAttribute();
        MetaDataAttribute metaDataAttribute2 = new MetaDataAttribute("");
        MetaDataAttribute metaDataAttribute3 = new MetaDataAttribute("att1", null);
        MetaDataAttribute metaDataAttribute4 = new MetaDataAttribute("att1", "");
        MetaDataAttribute metaDataAttribute5 = new MetaDataAttribute("att2", "");

        assertEquals(new MetaDataAttribute(), metaDataAttribute);
        assertEquals(new MetaDataAttribute(""), metaDataAttribute2);
        assertEquals(new MetaDataAttribute("att1", null), metaDataAttribute3);
        assertEquals(new MetaDataAttribute("att1", ""), metaDataAttribute4);

        assertNotEquals(metaDataAttribute, new MetaDataRow("", ""));
        assertNotEquals(metaDataAttribute, metaDataAttribute2);
        assertNotEquals(metaDataAttribute3, metaDataAttribute4);
        assertNotEquals(metaDataAttribute4, metaDataAttribute5);
    }

    @Test
    public void testMetadataAttributeHashCode() {
        MetaDataAttribute a = new MetaDataAttribute();
        MetaDataAttribute b = new MetaDataAttribute();

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testDeserialize() {
        MetaDataAttribute metaDataAttribute = MetaDataAttribute.deserialize("");
        assertEquals(new MetaDataAttribute(), metaDataAttribute);

        MetaDataAttribute metaDataAttribute2 = MetaDataAttribute.deserialize("att1ßval1");
        assertEquals(new MetaDataAttribute("att1", "val1"), metaDataAttribute2);

        MetaDataAttribute metaDataAttribute3 = MetaDataAttribute.deserialize("att1ßval1ßatt2");
        assertEquals(new MetaDataAttribute("att1", "val1"), metaDataAttribute3);

        MetaDataAttribute metaDataAttribute4 = MetaDataAttribute.deserialize("att1ßßatt2");
        assertEquals(new MetaDataAttribute("att1", null), metaDataAttribute4);

        MetaDataAttribute metaDataAttribute5 = MetaDataAttribute.deserialize("ß");
        assertEquals(new MetaDataAttribute(), metaDataAttribute5);
    }

    @Test
    public void testGetDiagramPath() {
        assertEquals(null, metaDataEditor.getDiagramPath());
    }

    @Test
    public void testShowErrorMessage() {
        metaDataEditor.showErrorMessage("Error message!");
        verify(notification, times(1)).fire(anyObject());
    }
}
