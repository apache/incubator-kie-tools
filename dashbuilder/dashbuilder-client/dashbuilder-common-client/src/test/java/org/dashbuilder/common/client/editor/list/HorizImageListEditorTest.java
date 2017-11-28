package org.dashbuilder.common.client.editor.list;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class HorizImageListEditorTest extends ImageListEditorTest {
    
    @Before
    public void setup() {
        super.initExpectedValues();
        // The presenter instance to test.
        view = mock( ImageListEditorView.class );
        presenter = new HorizImageListEditor<DataSetProviderType>(view, valueChangeEvent);
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
    
}
