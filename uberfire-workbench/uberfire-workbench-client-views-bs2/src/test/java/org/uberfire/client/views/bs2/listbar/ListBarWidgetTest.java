/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.bs2.listbar;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListbarPreferences;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ListBarWidgetTest {

    @InjectMocks
    private ListBarWidgetImpl widget;

    // mockito was having classloader issues when mocking this, so we use a spy instead
    @Spy
    private final Instance<ListbarPreferences> optionalListBarPrefs = new Instance<ListbarPreferences>() {

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
        public <U extends ListbarPreferences> Instance<U> select( Class<U> subtype,
                                                                  Annotation... qualifiers ) {
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

        public void destroy( final ListbarPreferences listbarPreferences ) {

        }
    };

    @Mock
    private Pair<PartDefinition, FlowPanel> currentPart;

    @Mock
    private WorkbenchPanelPresenter presenter;

    @Mock
    private PanelManager panelManager;

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
        // this gets a setVisible call earlier in the setup process
        reset( widget.menuArea );

        widget.clear();
        verify( widget.contextMenu ).clear();
        verify( widget.menuArea ).setVisible( false );
        verify( widget.title ).clear();
        verify( widget.content ).clear();
        verify( parts ).clear();
        assertTrue( widget.partChooserList == null );
    }

    @Test
    public void onSelectPartOnPartHiddenEventIsFired() {
        ListBarWidgetImpl listBar = getStubListBarWidget();


        final PartDefinition selectedPart = mock( PartDefinition.class );
        final PartDefinition currentPart = mock( PartDefinition.class );

        listBar.panelManager = panelManager;
        listBar.partContentView.put( selectedPart, new FlowPanel() );
        listBar.parts.add( selectedPart );
        listBar.currentPart = Pair.newPair( currentPart, new FlowPanel() );
        listBar.partContentView.put( currentPart, new FlowPanel() );


        listBar.selectPart( selectedPart );

        verify( panelManager ).onPartHidden( currentPart );

    }

    private ListBarWidgetImpl getStubListBarWidget() {
        return new ListBarWidgetImpl() {
            @Override
            void setupContextMenu() {
            }

            @Override
            void setupDropdown() {
            }

            @Override
            void updateBreadcrumb( PartDefinition partDefinition ) {
            }
        };
    }

}