package org.uberfire.client.workbench;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.Collections;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith( GwtMockitoTestRunner.class )
public class WorkbenchStartupTest {

    /** The thing we are unit testing */
    @InjectMocks Workbench workbench;

    @Mock SyncBeanManager bm;
    @Mock WorkbenchPickupDragController dragController;
    @Mock WorkbenchDragAndDropManager dndManager;
    @Mock PanelManager panelManager;
    @Mock StubAppReadyEventSource appReadyEvent;
    @Mock User identity;
    @Mock(extraInterfaces=ClientMessageBus.class) ClientMessageBusImpl bus;
    @Mock WorkbenchLayout layout;
    @Mock LayoutSelection layoutSelection;

    @Before
    public void setup() {
        when( bm.lookupBeans( any(Class.class) ) ).thenReturn( Collections.emptyList() );
        when( dragController.getBoundaryPanel() ).thenReturn( new AbsolutePanel() );
    }

    @Test
    public void shouldNotStartWhenBlocked() throws Exception {
        verify( appReadyEvent, never() ).fire( any(ApplicationReadyEvent.class) );
        workbench.addStartupBlocker( WorkbenchStartupTest.class );
        workbench.startIfNotBlocked();
        verify( appReadyEvent, never() ).fire( any(ApplicationReadyEvent.class) );
    }

    @Test
    public void shouldStartWhenUnblocked() throws Exception {
        workbench.addStartupBlocker( WorkbenchStartupTest.class );
        workbench.removeStartupBlocker( WorkbenchStartupTest.class );
        verify( appReadyEvent, times( 1 ) ).fire( any(ApplicationReadyEvent.class) );
    }

    @Test
    public void shouldStartOnAfterInitIfNeverBlocked() throws Exception {
        workbench.startIfNotBlocked();
        verify( appReadyEvent, times( 1 ) ).fire( any(ApplicationReadyEvent.class) );
    }

    /**
     * Mockito failed to produce a valid mock for a raw {@code Event<ApplicationReadyEvent>} due to classloader issues.
     * This class provides it something that it can mock properly.
     */
    public static class StubAppReadyEventSource implements Event<ApplicationReadyEvent> {

        @Override
        public void fire( ApplicationReadyEvent event ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public Event<ApplicationReadyEvent> select( Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public <U extends ApplicationReadyEvent> Event<U> select( Class<U> subtype, Annotation... qualifiers ) {
            throw new UnsupportedOperationException("Not implemented.");
        }

    }
}
