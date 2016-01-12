package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;

import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.widgets.DataGrid;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class SimpleTableTest {

    private SimpleTable simpleTable;

    @GwtMock
    DataGrid dataGridMock;

    @Test
    public void testRedrawFlush() throws Exception {
        this.simpleTable = new SimpleTable();

        simpleTable.dataGrid = dataGridMock;
        simpleTable.redraw();
        verify(dataGridMock).redraw();
        verify(dataGridMock).flush();
    }


}