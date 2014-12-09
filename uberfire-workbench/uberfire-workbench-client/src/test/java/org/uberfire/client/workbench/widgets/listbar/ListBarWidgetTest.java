package org.uberfire.client.workbench.widgets.listbar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
    public class ListBarWidgetTest {

    @InjectMocks
    private ListBarWidget widget;

    // mockito was having classloader issues when mocking this, so we use a spy instead
    @Spy
    private Instance<ListbarPreferences> optionalListBarPrefs = new Instance<ListbarPreferences>() {

        @Override
        public Iterator<ListbarPreferences> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListbarPreferences get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Instance<ListbarPreferences> select( Annotation... qualifiers ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U extends ListbarPreferences> Instance<U> select( Class<U> subtype, Annotation... qualifiers ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isUnsatisfied() {
            return true;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

    };

    @Mock
    private Pair<PartDefinition, FlowPanel> currentPart;

    @Mock
    private WorkbenchPanelPresenter presenter;

    @Mock
    private PanelManager panelManager;

    @Mock
    private Map<PartDefinition, FlowPanel> partContentView;

    @Mock
    private LinkedHashSet<PartDefinition> parts;

    @Before
    public void setup() {
        widget.postConstruct();
    }

    @Test
    public void verifyNewInstanceCreationSequenceHappyCase() {
        assertTrue( widget.isDndEnabled() );
        assertTrue( widget.isMultiPart() );
        verify( widget.closeButton ).addClickHandler( any( ClickHandler.class ) );
        verify( widget.container ).addFocusHandler( any( FocusHandler.class ) );
        verify( widget.contextDisplay ).removeFromParent();
    }

    @Test
    public void clearCallSequence() {
        // this gets a setVisisble call earlier in the setup process
        reset( widget.menuArea );

        widget.clear();
        verify( widget.contextMenu ).clear();
        verify( widget.menuArea ).setVisible( false );
        verify( widget.title ).clear();
        verify( widget.content ).clear();
        verify( parts ).clear();
        assertTrue( widget.partChooserList == null );
    }

}
