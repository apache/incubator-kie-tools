/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.gwtmockito.fakes.FakeProvider;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class EditScreenTest {

    private EditScreenFake editScreen;

    private ModalConfigurationContext ctx;

    private SimpleEventBus simpleEventBus = new SimpleEventBus();

    private ModalHiddenHandler modalHiddenHandler;

    private Command cleanupPlaceRequest;

    @Before
    public void setup() {
        GwtMockito.useProviderForType(SimpleEventBus.class,
                                      new FakeProvider() {
                                          @Override
                                          public Object getFake(Class aClass) {
                                              return simpleEventBus;
                                          }
                                      });

        ctx = mock(ModalConfigurationContext.class);
        cleanupPlaceRequest = mock(Command.class);
        editScreen = spy(new EditScreenFake(ctx,
                                            cleanupPlaceRequest));

        when(editScreen.addHiddenHandler(Mockito.any(ModalHiddenHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                modalHiddenHandler = (ModalHiddenHandler) aInvocation.getArguments()[0];
                return null;
            }
        });

        editScreen.realAddHiddenHandler();
    }

    @Test
    public void okButtonClickHandlerTest() {
        editScreen.okButton();
        verify(editScreen).hide();

        modalHiddenHandler.onHidden(new ModalHiddenEvent(editScreen,
                                                         new Event() {
                                                         }));
        verify(editScreen.getConfigContext(),
               never()).configurationCancelled();
        verify(editScreen.getConfigContext()).configurationFinished();
        verify(cleanupPlaceRequest).execute();
    }

    @Test
    public void cancelButtonClickHandlerTest() {
        editScreen.cancelButton();
        verify(editScreen).hide();

        modalHiddenHandler.onHidden(new ModalHiddenEvent(editScreen,
                                                         new Event() {
                                                         }));
        verify(editScreen.getConfigContext()).configurationCancelled();
        verify(editScreen.getConfigContext(),
               never()).configurationFinished();
    }

    @Test
    public void closeButtonClickHandlerTest() {
        modalHiddenHandler.onHidden(new ModalHiddenEvent(editScreen,
                                                         new Event() {
                                                         }));
        verify(editScreen.getConfigContext()).configurationCancelled();
        verify(editScreen.getConfigContext(),
               never()).configurationFinished();
    }

    @Test
    public void shouldICleanupPlaceRequestTest() {
        EditScreen edit = spy(new EditScreen(mock(ModalConfigurationContext.class),
                                             new ArrayList<>(),
                                             () -> {
                                             }));

        assertFalse(edit.shouldICleanupPlaceRequest());

        when(edit.oldPlaceName()).thenReturn("some");
        when(edit.currentPlaceName()).thenReturn("some");
        assertFalse(edit.shouldICleanupPlaceRequest());

        when(edit.oldPlaceName()).thenReturn("some");
        when(edit.currentPlaceName()).thenReturn("another");
        assertTrue(edit.shouldICleanupPlaceRequest());
    }

    private List<String> getScreensId() {
        List<String> availableWorkbenchScreensIds = new ArrayList<String>();
        availableWorkbenchScreensIds.add("screen");

        return availableWorkbenchScreensIds;
    }

    private class EditScreenFake extends EditScreen {

        public EditScreenFake(ModalConfigurationContext ctx,
                              Command cleanupPlaceRequest) {
            super(ctx,
                  getScreensId(),
                  cleanupPlaceRequest);
        }

        public void realAddHiddenHandler() {
            super.addHiddenHandler();
        }

        @Override
        boolean shouldICleanupPlaceRequest() {
            return true;
        }

        @Override
        public void addHiddenHandler() {
        }
    }
}
