package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class PagedTableTest {

    private PagedTable pagedTable;

    @GwtMock
    AsyncDataProvider dataProvider;

    @Test
    public void testSetDataProvider() throws Exception {
        this.pagedTable = new PagedTable( 5 );

        pagedTable.setDataProvider(dataProvider);
        verify( dataProvider ).addDataDisplay(pagedTable);

    }
}