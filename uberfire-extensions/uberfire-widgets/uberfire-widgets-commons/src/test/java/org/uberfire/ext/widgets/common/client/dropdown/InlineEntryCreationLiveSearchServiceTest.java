package org.uberfire.ext.widgets.common.client.dropdown;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class InlineEntryCreationLiveSearchServiceTest extends AbstractEntryCreationLiveSearchServiceTest<InlineCreationEditor> {

    @Test
    public void testInlineEditorCancelAction() {
        startTest();

        onCancelCommand.execute();

        verify(dropDown, never()).addNewItem(any());
        verify(view).restoreFooter();
    }

    @Override
    protected void startTest() {
        super.startTest();

        verify(view).showNewItemEditor(editor);
    }

    @Override
    protected Class<InlineCreationEditor> getEditorType() {
        return InlineCreationEditor.class;
    }
}
