package org.dashbuilder.client.widgets.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ClientRuntimeErrorPopupPresenterTest {
    
    @Mock ErrorPopupPresenter.View view;
    
    private ClientRuntimeErrorPopupPresenter presenter;
    
    @Before
    public void setup() {
        // The presenter instance to test.
        presenter = new ClientRuntimeErrorPopupPresenter(view);
    }

    @Test
    public void testShowMessage() throws Exception {
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        final String message = "message";
        final Throwable cause = mock(Throwable.class);
        final String localizedMessage = "localizedMessage";
        when(cause.getLocalizedMessage()).thenReturn(localizedMessage);
        when(error.getMessage()).thenReturn(message);
        when(error.getRootCause()).thenReturn(cause);
        presenter.showMessage(error);
        verify(view, times(1)).showMessage(anyString(), any(Command.class), any(Command.class));
    }
    
}
