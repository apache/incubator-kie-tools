package org.dashbuilder.common.client.editor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public abstract class AbstractEditorTest {

    
    protected EditorError mockEditorError(final Editor<?> editor, final String message) {
        EditorError error = mock(EditorError.class);
        doReturn(editor).when(error).getEditor();
        doReturn(message).when(error).getMessage();
        return error;
    }
    
    protected void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
}
