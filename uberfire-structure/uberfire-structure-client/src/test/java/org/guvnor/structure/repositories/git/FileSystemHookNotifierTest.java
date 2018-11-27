package org.guvnor.structure.repositories.git;

import org.assertj.core.api.Assertions;
import org.guvnor.structure.client.repositories.git.FileSystemHookNotifier;
import org.guvnor.structure.repositories.impl.git.event.NotificationType;
import org.guvnor.structure.repositories.impl.git.event.PostCommitNotificationEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.event.Event;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FileSystemHookNotifierTest {

    private static final String MESSAGE = "notification message";

    @Mock
    private Event<NotificationEvent> notificationEvent;

    private FileSystemHookNotifier notifier;

    @Before
    public void init() {
        notifier = new FileSystemHookNotifier(notificationEvent);
    }

    @Test
    public void testNotificationSuccess() {
        testNotification(new PostCommitNotificationEvent(NotificationType.SUCCESS, MESSAGE), MESSAGE, NotificationEvent.NotificationType.SUCCESS);
    }

    @Test
    public void testNotificationWarning() {
        testNotification(new PostCommitNotificationEvent(NotificationType.WARNING, MESSAGE), MESSAGE, NotificationEvent.NotificationType.WARNING);
    }

    @Test
    public void testNotificationError() {
        testNotification(new PostCommitNotificationEvent(NotificationType.ERROR, MESSAGE), MESSAGE, NotificationEvent.NotificationType.ERROR);
    }

    private void testNotification(PostCommitNotificationEvent event, String expectedMessage, NotificationEvent.NotificationType expectedType) {
        notifier.notify(event);

        ArgumentCaptor<NotificationEvent> eventCaptor = ArgumentCaptor.forClass(NotificationEvent.class);

        verify(notificationEvent).fire(eventCaptor.capture());

        Assertions.assertThat(eventCaptor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("notification", expectedMessage)
                .hasFieldOrPropertyWithValue("type", expectedType);
    }
}
