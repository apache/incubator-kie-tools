package org.uberfire.client.mvp;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractPerspectiveActivityTest extends AbstractActivityTest {

    PerspectiveDefinition perspectiveDef;

    @Mock PlaceManager placeManager;
    @Mock PanelManager panelManager;
    @Mock WorkbenchServicesProxy wbServices;

    @InjectMocks
    AbstractWorkbenchPerspectiveActivity activity = new AbstractWorkbenchPerspectiveActivity( placeManager ) {

        @Override
        public Collection<String> getTraits() {
            throw new UnsupportedOperationException( "Not implemented." );
        }

        @Override
        public String getSignatureId() {
            throw new UnsupportedOperationException( "Not implemented." );
        }

        @Override
        public Collection<String> getRoles() {
            throw new UnsupportedOperationException( "Not implemented." );
        }

        @Override
        public PerspectiveDefinition getPerspective() {
            return perspectiveDef;
        }

        @Override
        public String getIdentifier() {
            throw new UnsupportedOperationException( "Not implemented." );
        }
    };

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        perspectiveDef = new PerspectiveDefinitionImpl();

        when( panelManager.getRoot() ).thenReturn( mock( PanelDefinition.class ) );

        doAnswer( new Answer<Object>() {
            @SuppressWarnings("rawtypes")
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                ((ParameterizedCommand) invocation.getArguments()[1]).execute( perspectiveDef );
                return null;
            }
        } ).when( wbServices ).loadPerspective( any(String.class), any(ParameterizedCommand.class) );
    }

    @Override
    public Activity getActivityUnderTest() {
        return activity;
    }

}
