package org.kie.workbench.common.screens.server.management.client.artifact;

import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DependencyListWidgetPresenterTest {

    private DependencyListWidgetPresenter presenter;

    @Mock
    private DependencyListWidgetPresenter.View view;

    @Mock
    private ArtifactListPresenter artifactListPresenter;

    @Mock
    private EventSourceMock<DependencyPathSelectedEvent> event;

    @Before
    public void setup() {
        presenter = new DependencyListWidgetPresenter( view, artifactListPresenter, event );

        assertEquals( view, presenter.getView() );
        assertEquals( artifactListPresenter, presenter.getArtifactListPresenter() );
    }

    @Test
    public void testSelect() {
        presenter.onSelect( "my_selected_artifact" );

        verify( event, times( 1 ) ).fire( any( DependencyPathSelectedEvent.class ) );
    }

    @Test
    public void testSearch() {
        presenter.search( "artifact" );

        verify( artifactListPresenter, times( 1 ) ).search( "artifact" );
    }

    @Test
    public void testRefresh() {
        presenter.refresh();

        verify( artifactListPresenter, times( 1 ) ).refresh();
    }
}
