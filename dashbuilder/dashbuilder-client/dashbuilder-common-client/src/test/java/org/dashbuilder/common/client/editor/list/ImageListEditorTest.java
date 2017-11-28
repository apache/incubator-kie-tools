package org.dashbuilder.common.client.editor.list;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import org.dashbuilder.common.client.editor.AbstractEditorTest;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public abstract class ImageListEditorTest extends AbstractEditorTest {
    
    @Mock EventSourceMock<ValueChangeEvent<DataSetProviderType>> valueChangeEvent;
    
    protected final Collection<DataSetProviderType> expectedTypes = new ArrayList<DataSetProviderType>(4);
    protected final List<ImageListEditor<DataSetProviderType>.Entry> expectedEntries = new ArrayList<ImageListEditor<org.dashbuilder.dataprovider.DataSetProviderType>.Entry>(4);
    protected ImageListEditor<DataSetProviderType> presenter;
    protected ImageListEditorView<DataSetProviderType> view;
    
    public void initExpectedValues() {
        
        // Currently expected provider types supported.
        expectedTypes.add(DataSetProviderType.BEAN);
        expectedTypes.add(DataSetProviderType.CSV);
        expectedTypes.add(DataSetProviderType.SQL);
        expectedTypes.add(DataSetProviderType.ELASTICSEARCH);
        expectedEntries.addAll(mockEntries());
    }

    public void testClear() throws Exception {
        presenter.entries.addAll(expectedEntries);
        presenter.value = DataSetProviderType.BEAN;
        presenter.clear();
        assertTrue(presenter.entries.isEmpty());
        assertNull(presenter.value);
        verify(view, times(1)).clear();
        verify(view, times(0)).init(any(ImageListEditor.class));
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).add(any(SafeUri.class), anyString(), anyString(), any(SafeHtml.class), 
                any(SafeHtml.class), anyBoolean(), any(Command.class));
    }


    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).clear();
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).add(any(SafeUri.class), anyString(), anyString(), any(SafeHtml.class),
                any(SafeHtml.class), anyBoolean(), any(Command.class));
        
    }

    public void testNewEntry() throws Exception {
        ImageListEditor<DataSetProviderType>.Entry expectedEntry = mockEntry(DataSetProviderType.BEAN);
        ImageListEditor<DataSetProviderType>.Entry actualEntry = presenter.newEntry(expectedEntry.getValue(), expectedEntry.getUri(), expectedEntry.getHeading(), expectedEntry.getText());
        Assert.assertEquals(expectedEntry.getHeading(), actualEntry.getHeading());
        Assert.assertEquals(expectedEntry.getText(), actualEntry.getText());
        Assert.assertEquals(expectedEntry.getUri(), actualEntry.getUri());
        Assert.assertEquals(expectedEntry.getValue(), actualEntry.getValue());
    }

    public void testSetEntries() throws Exception {
        presenter.setEntries(expectedEntries);
        assertShowElements();
    }

    public void testClearErrors() throws Exception {
        List<EditorError> errors = new ArrayList<EditorError>();
        presenter.showErrors(errors);
        verify(view, times(0)).clear();
        verify(view, times(0)).init(any(ImageListEditor.class));
        verify(view, times(1)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).add(any(SafeUri.class), anyString(), anyString(), any(SafeHtml.class),
                any(SafeHtml.class), anyBoolean(), any(Command.class));
    }
    
    public void testShowErrors() throws Exception {
        EditorError e1 = mockEditorError(presenter, "m1");
        EditorError e2 = mockEditorError(presenter, "m2");
        List<EditorError> errors = new ArrayList<EditorError>(2);
        errors.add(e1);
        errors.add(e2);
        
        presenter.showErrors(errors);
        final ArgumentCaptor<SafeHtml> errorSafeHtmlCaptor =  ArgumentCaptor.forClass(SafeHtml.class);
        verify(view, times(0)).clear();
        verify(view, times(0)).init(any(ImageListEditor.class));
        verify(view, times(0)).clearError();
        verify(view, times(0)).add(any(SafeUri.class), anyString(), anyString(), any(SafeHtml.class),
                any(SafeHtml.class), anyBoolean(), any(Command.class));
        verify(view, times(1)).showError(errorSafeHtmlCaptor.capture());
        final SafeHtml value = errorSafeHtmlCaptor.getValue();
        Assert.assertEquals("m1\nm2", value.asString());
    }

    public void testAddHelpContent() throws Exception {
        final String title = "title";
        final String content = "content";
        final Placement p = Placement.BOTTOM;
        presenter.setHelpContent(title, content, p);
        verify(view, times(1)).setHelpContent(title, content, p);
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(0)).init(presenter);
    }
    
    public void testSetValueWithoutEvents() throws Exception {
        final DataSetProviderType value = DataSetProviderType.BEAN;
        presenter.entries.addAll(expectedEntries);
        presenter.setValue(value, false);
        Assert.assertEquals(value, presenter.value);
        assertShowElements();
    }

    public void testSetValueWithEvents() throws Exception {
        final DataSetProviderType oldValue = DataSetProviderType.SQL;
        final DataSetProviderType newValue = DataSetProviderType.BEAN;
        presenter.value = oldValue;
        presenter.entries.addAll(expectedEntries);
        presenter.setValue(newValue, true);
        Assert.assertEquals(newValue, presenter.value);
        assertShowElements();
        verify( valueChangeEvent, times( 1 ) ).fire(any(ValueChangeEvent.class));
    }
    
    protected void assertShowElements() {
        verify(view, times(0)).init(any(ImageListEditor.class));
        verify(view, times(0)).clearError();
        verify(view, times(0)).showError(any(SafeHtml.class));
        verify(view, times(1)).clear();
        verify(view, times(4)).add(any(SafeUri.class), anyString(), anyString(), any(SafeHtml.class),
                any(SafeHtml.class), anyBoolean(), any(Command.class));
        Assert.assertEquals(expectedEntries, presenter.entries);
    }

    protected List<ImageListEditor<DataSetProviderType>.Entry> mockEntries() {
        List<ImageListEditor<DataSetProviderType>.Entry> result = new ArrayList<ImageListEditor<org.dashbuilder.dataprovider.DataSetProviderType>.Entry>(4);
        result.add(mockEntry(DataSetProviderType.BEAN));
        result.add(mockEntry(DataSetProviderType.CSV));
        result.add(mockEntry(DataSetProviderType.SQL));
        result.add(mockEntry(DataSetProviderType.ELASTICSEARCH));
        return result;
    }

    protected static ImageListEditor<DataSetProviderType>.Entry mockEntry(DataSetProviderType type) {
        final String name = type.getName();
        final SafeUri uri = mock(SafeUri.class);
        final SafeHtml safeHtml = mock(SafeHtml.class);
        doReturn(name).when(safeHtml).asString();
        ImageListEditor<DataSetProviderType>.Entry entry = mock(ImageListEditor.Entry.class);
        doReturn(type).when(entry).getValue();
        doReturn(safeHtml).when(entry).getHeading();
        doReturn(safeHtml).when(entry).getText();
        doReturn(uri).when(entry).getUri();
        return entry;
    }
}
