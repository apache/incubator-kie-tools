package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.editor.client.CompositeEditor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.driver.DataColumnDefDriver;
import org.dashbuilder.client.widgets.dataset.event.ColumnsChangedEvent;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ColumnListEditorTest {

    @Mock SyncBeanManager beanManager;
    @Mock DataColumnDefDriver dataColumnDefDriver;
    @Mock EventSourceMock<ColumnsChangedEvent> columnsChangedEvent;
    @Mock ColumnListEditor.View view;
    @Mock SyncBeanDef<DataColumnDefEditor> columnDefEditorSyncBeanDef;
    @Mock DataColumnDefEditor dataColumnDefEditor;
    private ColumnListEditor presenter;
    final ListEditor<DataColumnDef, org.dashbuilder.dataset.client.editor.DataColumnDefEditor> listEditor = mock(ListEditor.class);
    @Mock DataColumnDef col1;

    @Before
    public void setup() {
        presenter = new ColumnListEditor(beanManager, dataColumnDefDriver, columnsChangedEvent, view);
        
        // Bean instantiation mocks.
        when(beanManager.lookupBean(DataColumnDefEditor.class)).thenReturn(columnDefEditorSyncBeanDef);
        when( columnDefEditorSyncBeanDef.newInstance() ).thenAnswer( new Answer<DataColumnDefEditor>() {
            @Override
            public DataColumnDefEditor answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return dataColumnDefEditor;
            }
        } );
        
        // Acceptable values.
        when(col1.getId()).thenReturn("col1");
        when(col1.getColumnType()).thenReturn(ColumnType.LABEL);
        final List<DataColumnDef> acceptableValues = buildAcceptableValues();
        presenter.acceptableColumns = acceptableValues;
        
        // Column Editors.
        final ValueBoxEditor<String> id = mock(ValueBoxEditor.class);
        when(id.getValue()).thenReturn("col1");
        when(dataColumnDefEditor.id()).thenReturn(id);
        final List<org.dashbuilder.dataset.client.editor.DataColumnDefEditor> editors = new ArrayList<org.dashbuilder.dataset.client.editor.DataColumnDefEditor>();
        final List<DataColumnDef> columns = new ArrayList<DataColumnDef>();
        columns.add(col1);
        editors.add(dataColumnDefEditor);
        when(listEditor.getEditors()).thenReturn(editors);
        when(listEditor.getList()).thenReturn(columns);
        presenter.listEditor = listEditor;

    }

    @Test
    public void testInit() {
        presenter.init();
        assertNotNull(presenter.listEditor);
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(0)).remove(anyInt());
        verify(view, times(0)).clear();
    }

    @Test
    public void testClear() {
        presenter.clear();
        assertNull(presenter.acceptableColumns);
        verify(view, times(1)).clear();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(0)).remove(anyInt());
    }

    @Test
    public void testSetAcceptableValues() {
        final List<DataColumnDef> acceptableValues = buildAcceptableValues();
        presenter.setAcceptableValues(acceptableValues);
        verify(dataColumnDefEditor, times(1)).isEditMode(false);
        verify(dataColumnDefDriver, times(1)).initialize(dataColumnDefEditor);
        verify(dataColumnDefDriver, times(1)).edit(col1);
        verify(view, times(1)).clear();
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).remove(anyInt());
    }
    
    @Test
    public void testOnValueRestricted() {
        final String value = "col1";
        presenter.restrictedColumns.clear();
        presenter.onValueRestricted(value);
        assertEquals(1, presenter.restrictedColumns.size());
        assertEquals("col1", presenter.restrictedColumns.get(0));
        verify(dataColumnDefEditor, times(1)).isEditMode(false);
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * Column editor editMode set to false, as only one column present in the dset definition, so it cannot be unselected. 
     */
    @Test
    public void testOnValueUnRestrictedSingleColumn() {
        final String value = "col1";
        presenter.restrictedColumns.add("col1");
        presenter.onValueUnRestricted(value);
        assertTrue(presenter.restrictedColumns.isEmpty());
        verify(dataColumnDefEditor, times(1)).isEditMode(false);
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * Column editor editMode set to true, as more than one column present in the dset definition, so it can be selected/unselected. 
     */
    @Test
    public void testOnValueUnRestricted() {
        final String value = "col1";
        presenter.restrictedColumns.add("col1");
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        presenter.listEditor.getList().add(col2);
        presenter.onValueUnRestricted(value);
        assertTrue(presenter.restrictedColumns.isEmpty());
        verify(dataColumnDefEditor, times(1)).isEditMode(true);
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }
    
    @Test
    public void testCreateEditorForTraversal() {
        presenter.createEditorForTraversal();
        verify(listEditor, times(1)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).onPropertyChange(anyString());
        verify(listEditor, times(0)).setValue(any(List.class));
    }


    @Test
    public void testGetPathElement() {
        presenter.getPathElement(dataColumnDefEditor);
        verify(listEditor, times(1)).getPathElement(dataColumnDefEditor);
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).onPropertyChange(anyString());
        verify(listEditor, times(0)).setValue(any(List.class));
    }

    @Test
    public void testSetEditorChain() {
        final CompositeEditor.EditorChain<DataColumnDef, org.dashbuilder.dataset.client.editor.DataColumnDefEditor> chain = mock(CompositeEditor.EditorChain.class);
        presenter.setEditorChain(chain);
        verify(listEditor, times(1)).setEditorChain(chain);
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).onPropertyChange(anyString());
        verify(listEditor, times(0)).setValue(any(List.class));
    }

    @Test
    public void testSetDelegate() {
        final EditorDelegate<List<DataColumnDef>> delegate = mock(EditorDelegate.class);
        presenter.setDelegate(delegate);
        verify(listEditor, times(1)).setDelegate(delegate);
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).onPropertyChange(anyString());
        verify(listEditor, times(0)).setValue(any(List.class));
    }

    @Test
    public void testFlush() {
        presenter.flush();
        verify(listEditor, times(1)).flush();
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).onPropertyChange(anyString());
        verify(listEditor, times(0)).setValue(any(List.class));
    }

    @Test
    public void testOnPropertyChange() {
        final String s = "prop1";
        presenter.onPropertyChange(s);
        verify(listEditor, times(1)).onPropertyChange(s);
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).setValue(any(List.class));
    }

    @Test
    public void testSetValue() {
        final List<DataColumnDef> value = mock(List.class);
        presenter.setValue(value);
        verify(listEditor, times(1)).setValue(value);
        verify(listEditor, times(0)).createEditorForTraversal();
        verify(listEditor, times(0)).getPathElement(any(DataColumnDefEditor.class));
        verify(listEditor, times(0)).setEditorChain(any(CompositeEditor.EditorChain.class));
        verify(listEditor, times(0)).setDelegate(any(EditorDelegate.class));
        verify(listEditor, times(0)).flush();
        verify(listEditor, times(0)).onPropertyChange(anyString());
    }

    @Test
    public void testSetProviderType() {
        DataSetProviderType type = mock(DataSetProviderType.class);
        presenter.setProviderType(type);
        assertEquals(type, presenter.providerType);
    }

    @Test
    public void testOnColumnSelected() {
        listEditor.getList().clear();
        when(col1.clone()).thenReturn(col1);
        presenter.onColumnSelect(0, true);
        assertEquals(1, listEditor.getList().size());
        assertEquals(col1, listEditor.getList().get(0));
        verify(columnsChangedEvent, times(1)).fire(any(ColumnsChangedEvent.class));
    }

    @Test
    public void testOnColumnUnSelected() {
        when(col1.clone()).thenReturn(col1);
        presenter.onColumnSelect(0, false);
        assertTrue(listEditor.getList().isEmpty());
        verify(columnsChangedEvent, times(1)).fire(any(ColumnsChangedEvent.class));
    }

    /**
     * Column editor created with editMode set to false, as only one column present in the dset definition, so it cannot be unselected. 
     */
    @Test
    public void testDataColumnDefEditorSource_Create() {
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        org.dashbuilder.dataset.client.editor.DataColumnDefEditor e = source.create(0);
        verify(dataColumnDefEditor, times(2)).isEditMode(false);
        verify(dataColumnDefEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(dataColumnDefEditor, times(1)).setOriginalColumnType(any(ColumnType.class));
        verify(view, times(2)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(2)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * Column editor created with editMode set to true, as more than one column present in the dset definition. First column enabled too, as it was disabled as was the unique one before this creation.
     */
    @Test
    public void testDataColumnDefEditorSource_CreateMultiple() {
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        presenter.acceptableColumns.add(col2);
        presenter.listEditor.getList().add(col2);
        presenter.restrictedColumns.clear();
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        org.dashbuilder.dataset.client.editor.DataColumnDefEditor e = source.create(0);
        verify(dataColumnDefEditor, times(2)).isEditMode(true);
        verify(dataColumnDefEditor, times(1)).setOriginalColumnType(any(ColumnType.class));
        verify(dataColumnDefEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(view, times(2)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(2)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * See https://issues.jboss.org/browse/DASHBUILDE-79
     */
    @Test
    public void testDataColumnDefEditorSource_Create_DASHBUILDE79_A() {
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        presenter.acceptableColumns.add(col2);
        presenter.listEditor.getList().add(col2);
        presenter.listEditor.getEditors().clear();
        presenter.restrictedColumns.clear();
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        org.dashbuilder.dataset.client.editor.DataColumnDefEditor e = source.create(0);
        verify(dataColumnDefEditor, times(1)).isEditMode(true);
        verify(dataColumnDefEditor, times(1)).setOriginalColumnType(any(ColumnType.class));
        verify(dataColumnDefEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * See https://issues.jboss.org/browse/DASHBUILDE-79 (re-opened)
     */
    @Test
    public void testDataColumnDefEditorSource_Create_DASHBUILDE79_B() {
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        presenter.acceptableColumns.add(col2);
        presenter.listEditor.getList().clear();
        presenter.listEditor.getList().add(col2);
        presenter.listEditor.getEditors().clear();
        presenter.restrictedColumns.clear();
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        org.dashbuilder.dataset.client.editor.DataColumnDefEditor e = source.create(0);
        verify(dataColumnDefEditor, times(1)).isEditMode(false);
        verify(dataColumnDefEditor, times(1)).setOriginalColumnType(any(ColumnType.class));
        verify(dataColumnDefEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    /**
     * Ensure create a dummy not editable column when disposing the only one present from the definition.
     */
    @Test
    public void testDataColumnDefEditorSource_Dispose() {
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        source.dispose(dataColumnDefEditor);
        verify(dataColumnDefEditor, times(1)).removeFromParent();
        verify(dataColumnDefEditor, times(3)).isEditMode(false);
        verify(dataColumnDefDriver, times(1)).initialize(dataColumnDefEditor);
        verify(dataColumnDefDriver, times(1)).edit(col1);
        verify(view, times(2)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(2)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }


    /**
     * Ensure create a dummy not editable column when disposing the only one present from the definition and ensure first column is set to editMode=false, as will be the only one column in the definition.
     */
    @Test
    public void testDataColumnDefEditorSource_Dispose_UniqueColumn() {
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        final DataColumnDefEditor col2Editor = mock(DataColumnDefEditor.class);
        final ValueBoxEditor<String> id2 = mock(ValueBoxEditor.class);
        when(id2.getValue()).thenReturn("col2");
        when(col2Editor.id()).thenReturn(id2);        
        presenter.acceptableColumns.add(col2);
        presenter.listEditor.getList().add(col2);
        presenter.listEditor.getEditors().add(col2Editor);

        when( columnDefEditorSyncBeanDef.newInstance() ).thenAnswer( new Answer<DataColumnDefEditor>() {
            @Override
            public DataColumnDefEditor answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return col2Editor;
            }
        } );
        
        ColumnListEditor.DataColumnDefEditorSource source = presenter.createDataColumnDefEditorSource();
        source.dispose(col2Editor);
        verify(dataColumnDefEditor, times(0)).removeFromParent();
        verify(dataColumnDefEditor, times(0)).isEditMode(false);
        verify(col2Editor, times(1)).removeFromParent();
        verify(col2Editor, times(2)).isEditMode(false);
        
        verify(dataColumnDefDriver, times(1)).initialize(col2Editor);
        verify(dataColumnDefDriver, times(1)).edit(col2);
        verify(view, times(1)).insert(anyInt(), any(DataColumnDefEditor.View.class), anyBoolean(), anyBoolean(), anyString());
        verify(view, times(1)).remove(anyInt());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clear();
    }

    protected List<DataColumnDef> buildAcceptableValues() {
        final List<DataColumnDef> acceptableValues = new ArrayList<DataColumnDef>();
        acceptableValues.add(col1);
        return acceptableValues;
    }
}
