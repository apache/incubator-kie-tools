package org.kie.workbench.common.screens.library.client.screens.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.mockito.Mock;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Anchor;

import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.never;

@RunWith(GwtMockitoTestRunner.class)
public class AddProjectPopUpViewTest {

    @Mock
    private AddProjectPopUpPresenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private Div advancedOptions;

    @Mock
    private Anchor advancedOptionsAnchor;

    private AddProjectPopUpView view;

    @Before
    public void setup() {
        view = spy(new AddProjectPopUpView(presenter,
                                           translationService,
                                           advancedOptions,
                                           advancedOptionsAnchor));
    }

    @Test
    public void testConfigureAdvancedOptions() {
        ClickEvent clickEvent = mock(ClickEvent.class);

        doReturn(true).when(advancedOptions).getHidden();
        doReturn("restore").when(translationService).format(LibraryConstants.RestoreAdvancedOptions);

        view.advancedOptionsAnchorOnClick(clickEvent);

        verify(advancedOptions).setHidden(false);
        verify(translationService).format(LibraryConstants.RestoreAdvancedOptions);
        verify(advancedOptionsAnchor).setTextContent("restore");
        verify(presenter, never()).restoreAdvancedOptions();
    }

    @Test
    public void testRestoreAdvancedOptions() {
        ClickEvent clickEvent = mock(ClickEvent.class);

        doReturn(false).when(advancedOptions).getHidden();
        doReturn("configure").when(translationService).format(LibraryConstants.ConfigureAdvancedOptions);

        view.advancedOptionsAnchorOnClick(clickEvent);

        verify(advancedOptions).setHidden(true);
        verify(translationService).format(LibraryConstants.ConfigureAdvancedOptions);
        verify(advancedOptionsAnchor).setTextContent("configure");
        verify(presenter).restoreAdvancedOptions();
    }
}
