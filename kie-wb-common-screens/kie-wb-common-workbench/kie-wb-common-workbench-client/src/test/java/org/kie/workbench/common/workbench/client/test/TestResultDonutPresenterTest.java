package org.kie.workbench.common.workbench.client.test;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TestResultDonutPresenterTest {

    @Mock
    DisplayerLocator displayerLocator;

    @Mock
    Elemental2DomUtil elemental2DomUtil;

    @Mock
    DisplayerCoordinator displayerCoordinator;

    @Mock
    TranslationService translationService;

    @InjectMocks
    TestResultDonutPresenter donutPresenter;

    @Mock
    Displayer displayer;

    @Before
    public void setUp() throws Exception {
        when(displayerLocator.lookupDisplayer(any())).thenReturn(displayer);
    }

    @Test
    public void showSuccessFailureDiagram() {
        HTMLDivElement container = mock(HTMLDivElement.class);
        donutPresenter.init(container);

        donutPresenter.showSuccessFailureDiagram(1, 1);

        verify(displayerCoordinator).addDisplayer(displayer);
        verify(displayerCoordinator).drawAll();

        elemental2DomUtil.appendWidgetToElement(eq(container),
                                                any());
    }

    @Test
    public void showSuccessFailureDiagramSecondRun() {
        HTMLDivElement container = mock(HTMLDivElement.class);
        donutPresenter.init(container);

        donutPresenter.showSuccessFailureDiagram(1, 1);

        verify(elemental2DomUtil, never()).removeAllElementChildren(any());
        verify(displayerCoordinator, never()).removeDisplayer(any());

        donutPresenter.showSuccessFailureDiagram(2, 2);

        verify(elemental2DomUtil).removeAllElementChildren(any());
        verify(displayerCoordinator).removeDisplayer(any());
    }
}