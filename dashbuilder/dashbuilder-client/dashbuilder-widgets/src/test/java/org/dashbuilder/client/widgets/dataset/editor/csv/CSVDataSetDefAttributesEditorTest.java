package org.dashbuilder.client.widgets.dataset.editor.csv;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetDefAttributesEditor;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.file.FileUploadEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CSVDataSetDefAttributesEditorTest {

    @Mock DataSetClientServices dataSetClientServices;
    @Mock ValueBoxEditor<String> fileURL;
    @Mock FileUploadEditor filePath;
    @Mock ValueBoxEditor<Character> separatorChar;
    @Mock ValueBoxEditor<Character> quoteChar;
    @Mock ValueBoxEditor<Character> escapeChar;
    @Mock ValueBoxEditor<String> datePattern;
    @Mock ValueBoxEditor<String> numberPattern;
    @Mock CSVDataSetDefAttributesEditor.View view;
    
    private CSVDataSetDefAttributesEditor presenter;
    
    
    @Before
    public void setup() {
        doAnswer(args -> args.getArguments()[0])
                .when(dataSetClientServices)
                .getUploadFileUrl(anyString());

        presenter = new CSVDataSetDefAttributesEditor(dataSetClientServices,
                fileURL, filePath, separatorChar, quoteChar,
                escapeChar, datePattern, numberPattern, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class),any(ValueBoxEditor.View.class), 
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(fileURL, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(filePath, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(separatorChar, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(quoteChar, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(escapeChar, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(datePattern, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(numberPattern, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(filePath, times(1)).configure(anyString(), any(FileUploadEditor.FileUploadEditorCallback.class));
        verify(view, times(1)).showFilePathInput();
        verify(view, times(0)).showFileURLInput();
        ArgumentCaptor<FileUploadEditor.FileUploadEditorCallback> callbackCaptor = ArgumentCaptor.forClass(FileUploadEditor.FileUploadEditorCallback.class);
        verify(filePath).configure(anyString(), callbackCaptor.capture());


        CSVDataSetDef dataSetDef = (CSVDataSetDef) DataSetDefFactory.newCSVDataSetDef().uuid("test").buildDef();
        presenter.setValue(dataSetDef);
        FileUploadEditor.FileUploadEditorCallback fileCallback = callbackCaptor.getValue();
        String fileUrl = fileCallback.getUploadFileUrl();
        assertEquals(fileUrl, "default://master@dashbuilder/datasets/tmp/test.csv");
    }
    
    @Test
    public void testFileUrl() {
        assertEquals(fileURL, presenter.fileURL());
    }

    @Test
    public void testFilePath() {
        assertEquals(filePath, presenter.filePath());
    }

    @Test
    public void testSepChar() {
        assertEquals(separatorChar, presenter.separatorChar());
    }

    @Test
    public void testQuoteChar() {
        assertEquals(quoteChar, presenter.quoteChar());
    }

    @Test
    public void testEscapeChar() {
        assertEquals(escapeChar, presenter.escapeChar());
    }

    @Test
    public void testDatePattern() {
        assertEquals(datePattern, presenter.datePattern());
    }

    @Test
    public void testNumberPattern() {
        assertEquals(numberPattern, presenter.numberPattern());
    }

    @Test
    public void testSetValueUsingFileUrl() {
        final CSVDataSetDef dataSetDef = mock(CSVDataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.CSV);
        when(dataSetDef.getFileURL()).thenReturn("fileUrl");
        when(dataSetDef.getFilePath()).thenReturn(null);
        presenter.setValue(dataSetDef);
        assertEquals(false, presenter.isUsingFilePath());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class),any(ValueBoxEditor.View.class),
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(view, times(1)).showFileURLInput();
        verify(view, times(0)).showFilePathInput();
    }

    @Test
    public void testSetValueUsingFilePath() {
        final CSVDataSetDef dataSetDef = mock(CSVDataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.CSV);
        when(dataSetDef.getFileURL()).thenReturn(null);
        when(dataSetDef.getFilePath()).thenReturn("filePath");
        presenter.setValue(dataSetDef);
        assertEquals(true, presenter.isUsingFilePath());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class),any(ValueBoxEditor.View.class),
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(view, times(1)).showFilePathInput();
        verify(view, times(0)).showFileURLInput();
    }
    
    public void testCallbackUseFileUrl() {
        presenter.onUseFileURLButtonClick();
        assertEquals(false, presenter.isUsingFilePath());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class),any(ValueBoxEditor.View.class),
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(view, times(1)).showFileURLInput();
        verify(view, times(0)).showFilePathInput();
    }

    public void testCallbackUseFilePath() {
        presenter.onUseFilePathButtonClick();
        assertEquals(true, presenter.isUsingFilePath());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class),any(ValueBoxEditor.View.class),
                any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class), any(ValueBoxEditor.View.class));
        verify(view, times(1)).showFilePathInput();
        verify(view, times(0)).showFileURLInput();
    }
    
}
