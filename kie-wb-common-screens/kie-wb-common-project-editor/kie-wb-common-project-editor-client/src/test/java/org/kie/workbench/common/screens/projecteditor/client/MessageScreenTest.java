/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client;

public class MessageScreenTest {

//    private PlaceManager placeManager;
//    private MessageScreenView view;
//    private MessageScreenView.Presenter presenter;
//    private MessageScreen screen;
//    private ProblemsService messageService;
//    private Messages messages;
//
//    @Before
//    public void setUp() throws Exception {
//        placeManager = mock(PlaceManager.class);
//        view = mock(MessageScreenView.class);
//        messageService = mock(ProblemsService.class);
//        screen = new MessageScreen(view, placeManager, messageService);
//        presenter = screen;
//        messages = new Messages();
//        messages.getMessages().add(createMessage(1010101, Message.Level.INFO, "Just saying hi", 10, 123));
//        messages.getMessages().add(createMessage(123321, Message.Level.WARNING, "RRRRRED ALEEERT!", 1, 321));
//        messages.getMessages().add(createMessage(666666, Message.Level.ERROR, "ERROR!", 51, 121));
//
//        when(
//                messageService.getMessageLog()
//        ).thenReturn(
//                messages
//        );
//    }
//
//    @Test
//    public void testPresenterSet() throws Exception {
//        verify(view).setPresenter(presenter);
//    }
//
//
//    @Test
//    public void testMessageSent() throws Exception {
//
//        screen.init();
//
//        verify(view).addInfoLine(1010101, "Just saying hi", 10, 123);
//        verify(view).addWarningLine(123321, "RRRRRED ALEEERT!", 1, 321);
//        verify(view).addErrorLine(666666, "ERROR!", 51, 121);
//    }
//
//    @Test
//    public void testFocus() throws Exception {
//
//        screen.init();
//
//        verify(view).addInfoLine(1010101, "Just saying hi", 10, 123);
//        verify(view).addWarningLine(123321, "RRRRRED ALEEERT!", 1, 321);
//        verify(view).addErrorLine(666666, "ERROR!", 51, 121);
//        verify(view,never()).addInfoLine(111111, "New One", 11, 111);
//
//        messages.getMessages().add(createMessage(111111, Message.Level.INFO, "New One", 11, 111));
//
//        screen.onFocus();
//
//        verify(view).clean();
//        verify(view).addInfoLine(111111, "New One", 11, 111);
//    }
//
//    private Message createMessage(int id, Message.Level info, String text, int column, int line) {
//        Message message = new Message();
//        message.setId(id);
//        message.setLevel(info);
//        message.setText(text);
//        message.setColumn(column);
//        message.setLine(line);
//        return message;
//    }

}
