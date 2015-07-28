/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.services.backend.builder;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;

class BuildMessageBuilder {

    static BuildMessage makeErrorMessage(final String prefix) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel(Level.ERROR);
        buildMessage.setText(prefix);
        return buildMessage;
    }

    static BuildMessage makeWarningMessage(final String prefix) {
        final BuildMessage buildMessage = new BuildMessage();
        buildMessage.setLevel(Level.WARNING);
        buildMessage.setText(prefix);
        return buildMessage;
    }

}
