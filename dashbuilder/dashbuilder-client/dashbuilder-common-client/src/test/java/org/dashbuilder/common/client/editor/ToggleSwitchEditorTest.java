package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ToggleSwitchEditorTest extends AbstractEditorTest {
    
    @Mock ToggleSwitchEditor.View view;
    @Mock EventSourceMock<ValueChangeEvent<Boolean>> valueChangeEvent;
    
    private ToggleSwitchEditor presenter;
    
    @Before
    public void setup() {
        // The presenter instance to test.
        presenter = new ToggleSwitchEditor(view, valueChangeEvent);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).setValue(anyBoolean());
    }

    @Test
    public void testClearErrors() throws Exception {
        List<EditorError> errors = new ArrayList<EditorError>();
        presenter.showErrors(errors);
        verify(view, times(1)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setValue(anyBoolean());
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
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearError();
        verify(view, times(0)).setValue(anyBoolean());
        final SafeHtml value = errorSafeHtmlCaptor.getValue();
        Assert.assertEquals("m1\nm2", value.asString());
    }

    @Test
    public void testEnabled() throws Exception {
        presenter.setEnabled(true);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setValue(anyBoolean());
    }

    @Test
    public void testValue() throws Exception {
        final Boolean newValue = true;
        presenter.setValue(newValue);
        assertEquals(newValue, presenter.getValue());
        verify(view, times(1)).setValue(newValue);
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).init(presenter);
    }

    @Test
    public void testOnValueChanged() throws Exception {
        final Boolean newValue = true;
        presenter.onValueChanged(newValue);
        assertEquals(newValue, presenter.getValue());
        verify(view, times(1)).clearError();
        verify(view, times(0)).setValue(newValue);
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).init(presenter);
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
    }
}
