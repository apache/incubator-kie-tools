package org.uberfire.ext.widgets.common.client.dropdown;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ModalEntryCreationLiveSearchServiceTest extends AbstractEntryCreationLiveSearchServiceTest<ModalCreationEditor> {

    @Test
    public void testInlineEditorCancelAction() {

        startTest();

        onCancelCommand.execute();

        verify(dropDown, never()).addNewItem(any());
        verify(view, never()).restoreFooter();
    }

    @Override
    protected Class<ModalCreationEditor> getEditorType() {
        return ModalCreationEditor.class;
    }
}
