/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories.git.hooks.impl;

import org.assertj.core.api.Assertions;
import org.guvnor.structure.backend.repositories.git.hooks.PostCommitNotificationService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.repositories.impl.git.event.NotificationType;
import org.guvnor.structure.repositories.impl.git.event.PostCommitNotificationEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import javax.enterprise.event.Event;
import java.util.Locale;

import static org.guvnor.structure.repositories.impl.git.event.NotificationType.ERROR;
import static org.guvnor.structure.repositories.impl.git.event.NotificationType.SUCCESS;
import static org.guvnor.structure.repositories.impl.git.event.NotificationType.WARNING;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostCommitNotificationServiceImplTest {

    private static final String BUNDLE_PATH = "src/test/resources/bundles/Messages.properties";

    private static final String REPO = "repository.git";
    private static final String VALID_SPACE = "mySpace";
    private static final String SYSTEM_SPACE = "system";

    private static final String EN_SUCCESS = "Success: nothing wrong happens";
    private static final String EN_WARNING = "Warning: check the logs";
    private static final String EN_WARNING_2 = "Warning: check the logs again";
    private static final String EN_ERROR = "Error: run in circles and scream";

    private static final String CA_SUCCESS = "Exit: no passa res";
    private static final String CA_WARNING = "Alerta: mira els logs del servidor";
    private static final String CA_WARNING_2 = "Alerta: mira els logs del servidor un altre cop";
    private static final String CA_ERROR = "Error: corre en cercles i crida";

    private Locale defaultLocale;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private Event<PostCommitNotificationEvent> notificationEvent;

    private Locale locale = Locale.ENGLISH;

    private MessageReader reader;

    private PostCommitNotificationServiceImpl service;

    @Before
    public void init() {
        defaultLocale = Locale.getDefault();

        Locale.setDefault(Locale.ENGLISH);

        reader = new MessageReader(() -> locale);
    }

    private void init(String path) {
        System.setProperty(PostCommitNotificationService.BUNDLE_PARAM, path);

        service = new PostCommitNotificationServiceImpl(sessionInfo, notificationEvent, reader);

        service.init();
    }

    @Test
    public void testLoadWrongBundle() {
        init("wrong bundle");

        service.notifyUser(new GitRepository(REPO, new Space(VALID_SPACE)), 0);

        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testNoNotification() {
        init(BUNDLE_PATH);

        service.notifyUser(new GitRepository(REPO, new Space(SYSTEM_SPACE)), 0);

        verify(notificationEvent, never()).fire(any());

        service.notifyUser(new GitRepository(REPO, new Space(VALID_SPACE)), 3);

        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void testNotifyEn() {
        testNotify(Locale.ENGLISH, EN_SUCCESS, EN_WARNING, EN_WARNING_2, EN_ERROR);
    }

    @Test
    public void testNotifyCa() {
        testNotify(new Locale("ca"), CA_SUCCESS, CA_WARNING, CA_WARNING_2, CA_ERROR);
    }

    @Test
    public void testNotifyNonExistingLanguage() {
        testNotify(Locale.FRENCH, EN_SUCCESS, EN_WARNING, EN_WARNING_2, EN_ERROR);
    }

    private void testNotify(Locale locale, String successMsg, String warningMsg, String warning2Msg, String errorMsg) {
        init(BUNDLE_PATH);

        this.locale = locale;

        testNotify(0, successMsg, SUCCESS,1);
        testNotify(1, warningMsg, WARNING, 2);
        testNotify(30, warning2Msg, WARNING, 3);
        testNotify(31, errorMsg, ERROR, 4);
    }

    private void testNotify(int code, String message, NotificationType notificationType, int times) {
        service.notifyUser(new GitRepository(REPO, new Space(VALID_SPACE)), code);

        ArgumentCaptor<PostCommitNotificationEvent> eventCaptor = ArgumentCaptor.forClass(PostCommitNotificationEvent.class);

        verify(notificationEvent, times(times)).fire(eventCaptor.capture());

        Assertions.assertThat(eventCaptor.getValue())
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", message)
                .hasFieldOrPropertyWithValue("type", notificationType);
    }

    @After
    public void finish(){
        Locale.setDefault(defaultLocale);
    }

}
