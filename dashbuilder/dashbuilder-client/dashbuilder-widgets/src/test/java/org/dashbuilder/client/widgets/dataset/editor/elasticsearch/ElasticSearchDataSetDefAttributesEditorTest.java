package org.dashbuilder.client.widgets.dataset.editor.elasticsearch;

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
public class ElasticSearchDataSetDefAttributesEditorTest {

    @Mock ValueBoxEditor<String> serverURL;
    @Mock ValueBoxEditor<String> clusterName;
    @Mock ValueBoxEditor<String> index;
    @Mock ValueBoxEditor<String> type;
    @Mock ElasticSearchDataSetDefAttributesEditor.View view;
    
    private ElasticSearchDataSetDefAttributesEditor presenter;
    
    
    @Before
    public void setup() {
        presenter = new ElasticSearchDataSetDefAttributesEditor(serverURL, clusterName, index, type, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class),
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(serverURL, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(clusterName, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(index, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(type, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
    }
    
    @Test
    public void testServerUrl() {
        assertEquals(serverURL, presenter.serverURL());
    }

    @Test
    public void testClusterName() {
        assertEquals(clusterName, presenter.clusterName());
    }

    @Test
    public void testIndex() {
        assertEquals(index, presenter.index());
    }

    @Test
    public void testType() {
        assertEquals(type, presenter.type());
    }

}
