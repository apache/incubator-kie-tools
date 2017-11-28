package org.dashbuilder.client.widgets.dataset.editor.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.validation.client.impl.ConstraintViolationImpl;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetEditorWorkflowTest extends AbstractDataSetWorkflowTest {

    public static final String UUID = "uuid1";
    public static final String NAME = "name1";

    @Mock SyncBeanManager beanManager;
    @Mock EventSourceMock<SaveRequestEvent> saveRequestEvent;
    @Mock EventSourceMock<TestDataSetRequestEvent> testDataSetEvent;
    @Mock EventSourceMock<CancelRequestEvent> cancelRequestEvent;
    @Mock DataSetClientServices clientServices;
    @Mock DataSetDef dataSetDef;
    @Mock DataSet dataSet;
    @Mock SimpleBeanEditorDriver driver;

    @Mock DataSetEditorWorkflow.View view;

    private DataSetEditorWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();

        presenter = new DataSetEditorWorkflow(clientServices, validatorProvider, beanManager, saveRequestEvent,
                testDataSetEvent, cancelRequestEvent, view) {

        };
        presenter.dataSetDef = this.dataSetDef;

        when(dataSetDef.getUUID()).thenReturn(UUID);
        when(dataSetDef.getName()).thenReturn(NAME);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        when(dataSet.getUUID()).thenReturn(UUID);
        when(dataSet.getRowCount()).thenReturn(0);
        when(dataSetDef.clone()).thenReturn(dataSetDef);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                DataSetReadyCallback callback = (DataSetReadyCallback) invocationOnMock.getArguments()[2];
                callback.callback(dataSet);
                return null;
            }
        }).when(clientServices).lookupDataSet(any(dataSetDef.getClass()), any(DataSetLookup.class), any(DataSetReadyCallback.class));
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testClear() {
        presenter.dataSetDef = this.dataSetDef;
        final Command c = mock(Command.class);
        presenter.flushCommand = c;
        presenter.stepValidator = c;
        presenter.clear();
        assertNull(presenter.getDataSetDef());
        assertNull(presenter.flushCommand);
        assertNull(presenter.stepValidator);
        assertTrue(presenter.violations.isEmpty());
        verify(view, times(1)).clearView();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).clearButtons();
    }

    // Expect RuntimeException!!!
    @Test(expected = RuntimeException.class)
    public void testDoTestDataSetNotEdited() {
        presenter.dataSetDef = null;
        final DataSetEditorWorkflow.TestDataSetCallback testDataSetCallback = mock(DataSetEditorWorkflow.TestDataSetCallback.class);
        presenter.testDataSet(testDataSetCallback);
    }

    @Test
    public void testDoTestDataSet() throws Exception {
        final DataSetEditorWorkflow.TestDataSetCallback testDataSetCallback = mock(DataSetEditorWorkflow.TestDataSetCallback.class);
        presenter.testDataSet(testDataSetCallback);
        verify(dataSetDef, times(1)).setAllColumnsEnabled(true);
        verify(dataSetDef, times(1)).setColumns(null);
        verify(dataSetDef, times(1)).setDataSetFilter(null);
        verify(dataSetDef, times(1)).clone();
        verify(dataSetDef, times(1)).setCacheEnabled(false);
        verify(testDataSetCallback, times(1)).onSuccess(dataSet);
        verify(testDataSetCallback, times(0)).onError(any(ClientRuntimeError.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testFlush() {
        final Command c = mock(Command.class);
        presenter.flushCommand = c;
        presenter.flush();
        verify(c, times(1)).execute();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testShowNextButton() {
        presenter.showNextButton();
        verify(view, times(1)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testShowTestButton() {
        presenter.showTestButton();
        verify(view, times(1)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testShowBackButton() {
        presenter.showBackButton();
        verify(view, times(1)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).clearView();
    }

    @Test
    public void testClearButtons() {
        presenter.clearButtons();
        verify(view, times(1)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testNoHasErrors() {
        final Collection violations = mock(Collection.class);
        when(violations.isEmpty()).thenReturn(true);
        presenter.violations = violations;
        assertFalse(presenter.hasErrors());
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testHasErrors() {
        final Collection violations = mock(Collection.class);
        when(violations.isEmpty()).thenReturn(false);
        presenter.violations = violations;
        assertTrue(presenter.hasErrors());
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testFlushDriver() {
        final Collection violations = mock(Collection.class);
        when(violations.isEmpty()).thenReturn(true);
        presenter.violations = violations;
        final Command c = mock(Command.class);
        presenter.stepValidator = c;
        presenter.flush(driver);
        verify(driver, times(1)).flush();
        verify(c, times(1)).execute();
        assertFalse(presenter.hasErrors());
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testAddViolations() {
        presenter.violations.clear();
        ConstraintViolationImpl v1 = mock(ConstraintViolationImpl.class);
        List<ConstraintViolation> _violations = new ArrayList<ConstraintViolation>();
        _violations.add(v1);
        presenter.addViolations(_violations);
        assertTrue(presenter.hasErrors());
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();

    }

    @Test
    public void testButtonCommand() {
        final Command c = mock(Command.class);
        presenter.flushCommand = c;
        presenter.testButtonCommand.execute();
        verify(c, times(1)).execute();
        verify(testDataSetEvent, times(1)).fire(any(TestDataSetRequestEvent.class));
        verify(saveRequestEvent, times(0)).fire(any(SaveRequestEvent.class));
        verify(cancelRequestEvent, times(0)).fire(any(CancelRequestEvent.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testSaveButtonCommand() {
        final Command c = mock(Command.class);
        presenter.flushCommand = c;
        presenter.saveButtonCommand.execute();
        verify(c, times(1)).execute();
        verify(saveRequestEvent, times(1)).fire(any(SaveRequestEvent.class));
        verify(testDataSetEvent, times(0)).fire(any(TestDataSetRequestEvent.class));
        verify(cancelRequestEvent, times(0)).fire(any(CancelRequestEvent.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

    @Test
    public void testCancelButtonCommand() {
        presenter.cancelButtonCommand.execute();
        verify(cancelRequestEvent, times(1)).fire(any(CancelRequestEvent.class));
        verify(saveRequestEvent, times(0)).fire(any(SaveRequestEvent.class));
        verify(testDataSetEvent, times(0)).fire(any(TestDataSetRequestEvent.class));
        verify(view, times(0)).clearButtons();
        verify(view, times(0)).addButton(anyString(), anyString(), anyBoolean(), any(Command.class));
        verify(view, times(0)).add(any(IsWidget.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).clearView();
    }

}
