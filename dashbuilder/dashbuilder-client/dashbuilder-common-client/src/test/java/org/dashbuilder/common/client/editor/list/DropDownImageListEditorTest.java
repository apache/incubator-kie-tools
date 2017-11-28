package org.dashbuilder.common.client.editor.list;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DropDownImageListEditorTest extends ImageListEditorTest {

    @Before
    public void setup() {
        super.initExpectedValues();
        // The presenter instance to test.
        view = mock(DropDownImageListEditor.View.class);
        presenter = new DropDownImageListEditor<DataSetProviderType>(view, valueChangeEvent);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClear() throws Exception {
        super.testClear();
    }


    @Test
    public void testInit() throws Exception {
        super.testInit();
    }

    @Test
    public void testNewEntry() throws Exception {
        super.testNewEntry();
    }

    @Test
    public void testSetEntries() throws Exception {
        super.testSetEntries();
    }

    @Test
    public void testClearErrors() throws Exception {
        super.testClearErrors();
    }
    
    @Test
    public void testShowErrors() throws Exception {
        super.testShowErrors();
    }

    @Test
    public void testAddHelpContent() throws Exception {
        super.testAddHelpContent();
    }
    
    @Test
    public void testSetValueWithoutEvents() throws Exception {
        super.testSetValueWithoutEvents();
    }

    @Test
    public void testSetValueWithEvents() throws Exception {
        super.testSetValueWithEvents();
    }

    @Test
    public void testEditModeEnabled() throws Exception {
        presenter.setEntries(expectedEntries);
        ((DropDownImageListEditor)presenter).isEditMode(true);
        assertEquals(true, ((DropDownImageListEditor) presenter).isEditMode);
        verify( ((DropDownImageListEditor.View)view) , times(2)).setDropDown(true);
        
    }

    @Test
    public void testEditModeDisabled() throws Exception {
        presenter.setEntries(expectedEntries);
        ((DropDownImageListEditor)presenter).isEditMode(false);
        assertEquals(false, ((DropDownImageListEditor) presenter).isEditMode);
        verify( ((DropDownImageListEditor.View)view) , times(1)).setDropDown(false);

    }
    
}
