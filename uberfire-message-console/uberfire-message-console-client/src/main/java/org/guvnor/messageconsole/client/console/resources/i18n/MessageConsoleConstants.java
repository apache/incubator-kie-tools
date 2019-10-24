/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.client.console.resources.i18n;

import com.google.gwt.i18n.client.Messages;

public interface MessageConsoleConstants extends Messages {

    String Line();

    String Column();

    String Text();

    String Level();

    String FileName();

    String RefreshMessageConsole();

    String ClearMessageConsole();

    String CopyMessageConsole();

    String Refreshing();

    String ErrorLevelTitle();

    String WarningLevelTitle();

    String InfoLevelTitle();

    String MessagesCopiedToClipboard();

    String MessagesNotCopiedToClipboard();
}
