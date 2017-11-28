package org.dashbuilder.common.client.editor.map;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.AbstractEditorTest;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MapEditorTest extends AbstractEditorTest {
    
    @Mock MapEditor.View view;
    @Mock EventSourceMock<ValueChangeEvent<Map<String, String>>> valueChangeEvent;
    
    private MapEditor presenter;
    
    @Before
    public void setup() {
        // The presenter instance to test.
        presenter = new MapEditor(view, valueChangeEvent);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).setAddText(anyString());
        verify(view, times(1)).setEmptyText(anyString());
        verify(view, times(1)).addButtonColumn(anyInt(), anyString(), anyInt());
        verify(view, times(2)).addTextColumn(anyInt(), anyString(), anyBoolean(), anyInt());
        verify(view, times(0)).clearError();
        verify(view, times(0)).removeColumn(anyInt());
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).setData(anyList());
        verify(view, times(0)).setRowCount(anyInt());
    }

    @Test
    public void testClearErrors() throws Exception {
        List<EditorError> errors = new ArrayList<EditorError>();
        presenter.showErrors(errors);
        verify(view, times(1)).clearError();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setAddText(anyString());
        verify(view, times(0)).setEmptyText(anyString());
        verify(view, times(0)).addButtonColumn(anyInt(), anyString(), anyInt());
        verify(view, times(0)).addTextColumn(anyInt(), anyString(), anyBoolean(), anyInt());
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).setData(anyList());
        verify(view, times(0)).setRowCount(anyInt());
        verify(view, times(0)).removeColumn(anyInt());
    }
    
    @Test
    public void testShowErrors() throws Exception {
        EditorError e1 = mockEditorError(presenter, "m1");
        EditorError e2 = mockEditorError(presenter, "m2");
        List<EditorError> errors = new ArrayList<EditorError>(2);
        errors.add(e1);
        errors.add(e2);
        presenter.showErrors(errors);
        final ArgumentCaptor<SafeHtml> errorSafeHtmlCaptor =  ArgumentCaptor.forClass(SafeHtml.class);
        verify(view, times(1)).showError(errorSafeHtmlCaptor.capture());
        verify(view, times(0)).clearError();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setAddText(anyString());
        verify(view, times(0)).setEmptyText(anyString());
        verify(view, times(0)).addButtonColumn(anyInt(), anyString(), anyInt());
        verify(view, times(0)).addTextColumn(anyInt(), anyString(), anyBoolean(), anyInt());
        verify(view, times(0)).setData(anyList());
        verify(view, times(0)).setRowCount(anyInt());
        verify(view, times(0)).removeColumn(anyInt());
        final SafeHtml value = errorSafeHtmlCaptor.getValue();
        Assert.assertEquals("m1\nm2", value.asString());
    }

    @Test
    public void testValue() throws Exception {
        final Map<String, String> value = new HashMap<String, String>(1);
        value.put("key1", "value1");
        presenter.setValue(value);
        assertEquals(value, presenter.getValue());
        assertSetViewValue(1);
    }

    @Test
    public void testUpdateKey() throws Exception {
        final Map<String, String> value = new HashMap<String, String>(1);
        value.put("key1", "value1");
        presenter.value = value;
        Map.Entry<String, String> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("key1");
        presenter.update(0, 0, entry, "key2");
        assertEquals(1, presenter.getValue().size());
        assertEquals("value1", presenter.getValue().get("key2"));
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
        assertSetViewValue(1);
    }

    @Test
    public void testUpdateValue() throws Exception {
        final Map<String, String> value = new HashMap<String, String>(1);
        value.put("key1", "value1");
        presenter.value = value;
        Map.Entry<String, String> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("key1");
        when(entry.getValue()).thenReturn("value1");
        presenter.update(1, 0, entry, "value2");
        assertEquals(1, presenter.getValue().size());
        assertEquals("value2", presenter.getValue().get("key1"));
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
        assertSetViewValue(1);
    }

    @Test
    public void testRemoveEntry() throws Exception {
        final Map<String, String> value = new HashMap<String, String>(1);
        value.put("key1", "value1");
        presenter.value = value;
        Map.Entry<String, String> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("key1");
        presenter.update(2, 0, entry, null);
        assertEquals(0, presenter.getValue().size());
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
        assertSetViewValue(0);
    }

    @Test
    public void testAddEntry() throws Exception {
        presenter.addEntry();
        assertEquals(1, presenter.getValue().size());
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
        assertSetViewValue(1);
    }
    
    protected void assertSetViewValue(final int size) {
        verify(view, times(3)).removeColumn(0);
        verify(view, times(1)).clearError();
        verify(view, times(1)).addButtonColumn(anyInt(), anyString(), anyInt());
        verify(view, times(2)).addTextColumn(anyInt(), anyString(), anyBoolean(), anyInt());
        verify(view, times(1)).setEmptyText(anyString());
        verify(view, times(1)).setRowCount(size);
        final ArgumentCaptor<List> dataCaptor =  ArgumentCaptor.forClass(List.class);
        verify(view, times(1)).setData(dataCaptor.capture());
        final List dataValue = dataCaptor.getValue();
        assertNotNull(dataValue);
        assertEquals(size, dataValue.size());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).setAddText(anyString());
    }

}
