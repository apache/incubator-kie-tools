package org.dashbuilder.common.client.editor.file;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.AbstractEditorTest;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FileUploadEditorTest extends AbstractEditorTest {
    
    @Mock FileUploadEditor.View view;
    @Mock EventSourceMock<ValueChangeEvent<String>> valueChangeEvent;
    @Mock EventSourceMock<NotificationEvent> workbenchNotificationEvent;
    
    private FileUploadEditor presenter;
    
    @Before
    public void setup() {
        // The presenter instance to test.
        presenter = new FileUploadEditor(valueChangeEvent, workbenchNotificationEvent, view);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testInit() throws Exception {
        presenter.init();
        assertEquals(view.asWidget(), presenter.asWidget());
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(1)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(1)).setLoadingImageVisible(false);
        verify(view, times(0)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testConfigure() throws Exception {
        FileUploadEditor.FileUploadEditorCallback callback = mock(FileUploadEditor.FileUploadEditorCallback.class);
        presenter.configure("f1", callback);
        assertEquals(view.asWidget(), presenter.asWidget());
        assertEquals(callback, presenter.callback);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(1)).setFileUploadName("f1");
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(0)).setLoadingImageVisible(false);
        verify(view, times(0)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testClearErrors() throws Exception {
        List<EditorError> errors = new ArrayList<EditorError>();
        presenter.showErrors(errors);
        verify(view, times(1)).clearError();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(0)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
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
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(0)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).clearError();
    }

    @Test
    public void testAddHelpContent() throws Exception {
        final String title = "title";
        final String content = "content";
        final Placement p = Placement.BOTTOM;
        presenter.addHelpContent(title, content, p);
        verify(view, times(1)).addHelpContent(title, content, p);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(0)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }
    
    @Test
    public void testValueSet() throws Exception {
        final String newValue = "newValue";  
        when(view.getFileName()).thenReturn("fff");
        presenter.setValue(newValue);
        assertEquals(newValue, presenter.getValue());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(1)).setFileLabelVisible(false);
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(1)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testValueUnset() throws Exception {
        final String newValue = "newValue";
        when(view.getFileName()).thenReturn(null);
        presenter.setValue(newValue);
        assertEquals(newValue, presenter.getValue());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(1)).setFileLabelText(newValue);
        verify(view, times(1)).setFileLabelVisible(true);
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(1)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testGetUploadFileName() throws Exception {
        FileUploadEditor.FileUploadEditorCallback callback = mock(FileUploadEditor.FileUploadEditorCallback.class);
        presenter.callback = callback;
        presenter.getUploadFileName();
        verify(callback, times(1)).getUploadFileName();
        verify(callback, times(0)).getUploadFileUrl();
    }

    @Test
    public void testGetUploadFileUrl() throws Exception {
        FileUploadEditor.FileUploadEditorCallback callback = mock(FileUploadEditor.FileUploadEditorCallback.class);
        presenter.callback = callback;
        presenter.getUploadFileUrl();
        verify(callback, times(1)).getUploadFileUrl();
        verify(callback, times(0)).getUploadFileName();
    }

    @Test
    public void testOnSubmitNull() throws Exception {
        when(view.getFileName()).thenReturn(null);
        final boolean result = presenter.onSubmit();
        assertEquals(false, result);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(0)).setFileUploadVisible(anyBoolean());
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(0)).setLoadingImageVisible(anyBoolean());
        verify(view, times(1)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testOnSubmit() throws Exception {
        when(view.getFileName()).thenReturn("ff");
        final boolean result = presenter.onSubmit();
        assertEquals(true, result);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(1)).setFileUploadVisible(false);
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(1)).setLoadingImageVisible(true);
        verify(view, times(1)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).clearError();
    }

    @Test
    public void testOnSubmitComplete() throws Exception {
        when(view.getFileName()).thenReturn("ff");
        presenter.onSubmitComplete("OK");
        assertEquals("ff", presenter.value);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).setFileUploadName(anyString());
        verify(view, times(1)).setFileUploadVisible(true);
        verify(view, times(0)).setFileLabelText(anyString());
        verify(view, times(0)).setFileLabelVisible(anyBoolean());
        verify(view, times(1)).setLoadingImageVisible(false);
        verify(view, times(1)).getFileName();
        verify(view, times(0)).setFormAction(anyString());
        verify(view, times(0)).submit();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(2)).clearError();
        verify( workbenchNotificationEvent, times( 1 ) ).fire(any(NotificationEvent.class));
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
    }
    
}
