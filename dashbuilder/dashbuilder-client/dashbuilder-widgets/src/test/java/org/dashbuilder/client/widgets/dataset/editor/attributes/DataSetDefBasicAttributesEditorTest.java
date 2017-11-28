package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefBasicAttributesEditorTest {

    @Mock ValueBoxEditor<String> uuidEditor;
    @Mock ValueBoxEditor<String> nameEditor;
    @Mock DataSetDefBasicAttributesEditor.View view;
    private DataSetDefBasicAttributesEditor presenter;
    
    @Before
    public void setup() {
        presenter = new DataSetDefBasicAttributesEditor(uuidEditor, nameEditor, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(uuidEditor, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(nameEditor, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
    }

    @Test
    public void testUUID() {
        assertEquals(uuidEditor, presenter.UUID());
    }

    @Test
    public void testName() {
        assertEquals(nameEditor, presenter.name());
    }
    
}
