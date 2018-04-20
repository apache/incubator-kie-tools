package org.kie.workbench.common.screens.library.client.settings.generalsettings;

import java.util.Arrays;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GitUrlsPresenter.View;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieSelectElement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import org.kie.workbench.common.screens.projecteditor.model.GitUrl;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GitUrlsPresenterTest {

    @Mock
    private View view;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private TranslationService translationService;

    @Mock
    private KieSelectElement kieSelectElement;

    private GitUrlsPresenter presenter;

    @Before
    public void before() {
        presenter = spy(new GitUrlsPresenter(view,
                                             notificationEvent,
                                             kieSelectElement,
                                             translationService));
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view).init(eq(presenter));
    }

    @Test
    public void testSetup() {

        final GitUrl gitUrl = new GitUrl("git", "git-url");
        final GitUrl sshUrl = new GitUrl("ssh", "ssh-url");

        presenter.setup(Arrays.asList(gitUrl, sshUrl));

        verify(presenter, times(1)).update();

        assertEquals("ssh",
                     presenter.selectedProtocol);
        assertEquals(2,
                     presenter.gitUrlsByProtocol.size());
        assertEquals(gitUrl,
                     presenter.gitUrlsByProtocol.get("git"));
        assertEquals(sshUrl,
                     presenter.gitUrlsByProtocol.get("ssh"));
    }

    @Test
    public void testSetSelectedProtocol() {

        final GitUrl gitUrl = new GitUrl("git", "git-url");
        final GitUrl sshUrl = new GitUrl("ssh", "ssh-url");

        presenter.setup(Arrays.asList(gitUrl, sshUrl));
        presenter.setSelectedProtocol("git");

        verify(presenter, times(2)).update();

        assertEquals("git",
                     presenter.selectedProtocol);
        assertEquals(2,
                     presenter.gitUrlsByProtocol.size());
        assertEquals(gitUrl,
                     presenter.gitUrlsByProtocol.get("git"));
        assertEquals(sshUrl,
                     presenter.gitUrlsByProtocol.get("ssh"));
    }

    @Test
    public void testUpdate() {
        final GitUrl gitUrl = new GitUrl("git", "url");
        presenter.setup(singletonList(gitUrl));

        presenter.update();
        verify(view, times(2)).setUrl(eq(gitUrl.getUrl()));
    }

    @Test
    public void testCopyToClipboardSuccess() {
        final GitUrl gitUrl = new GitUrl("git", "url");
        presenter.setup(singletonList(gitUrl));

        doReturn(true).when(presenter).copy();

        presenter.copyToClipboard();
        verify(notificationEvent).fire(any());
    }

    @Test
    public void testCopyToClipboardFail() {
        final GitUrl gitUrl = new GitUrl("git", "url");
        presenter.setup(singletonList(gitUrl));

        doReturn(false).when(presenter).copy();

        presenter.copyToClipboard();
        verify(notificationEvent).fire(any());
    }
}