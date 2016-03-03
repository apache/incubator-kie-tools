/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.popup;

import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>A confirmation box presenter.</p>
 *
 * @since 0.8.0
 */
@Dependent
public class ConfirmBox {

    public interface View {

        void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand,
                         final org.uberfire.mvp.Command noCommand, final org.uberfire.mvp.Command cancelCommand);

        void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand,
                  final org.uberfire.mvp.Command noCommand);

        void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand);

    }

    View view;

    @Inject
    public ConfirmBox(final View view) {
        this.view = view;
    }

    public void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand) {
        view.show(title, message, yesCommand);
    }

    public void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand, Command noCancelCommand) {
        view.show(title, message, yesCommand, noCancelCommand);
    }
    
    public void show(final String title, final String message, final org.uberfire.mvp.Command yesCommand,
              final org.uberfire.mvp.Command noCommand, final org.uberfire.mvp.Command cancelCommand) {
        view.show(title, message, yesCommand, noCommand, cancelCommand);
        
    }
}
