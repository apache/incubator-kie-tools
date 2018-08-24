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

package org.kie.workbench.common.test;

import org.kie.workbench.common.widgets.client.callbacks.AssetValidatedCallback;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class MockProvider {

    public static ValidationPopup getMockValidationPopup() {
        final ValidationPopup validationPopup = mock(ValidationPopup.class);
        doAnswer(new Answer<AssetValidatedCallback>() {
            @Override
            public AssetValidatedCallback answer(InvocationOnMock invocationOnMock) throws Throwable {
                final AssetValidatedCallback callback = mock(AssetValidatedCallback.class);
                final Command command = (Command) invocationOnMock.getArguments()[0];
                doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        command.execute();
                        return null;
                    }
                }).when(callback).callback(anyList());
                return callback;
            }
        }).when(validationPopup).getValidationCallback(any(Command.class));
        return validationPopup;
    }
}
