/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.widgets.client.widget;

import java.util.ArrayList;
import java.util.List;

public class AttachmentFileWidgetTestWrapper extends AttachmentFileWidget {

    boolean initialized;
    boolean isValid;

    List<String> shownMessages;

    public AttachmentFileWidgetTestWrapper() {
        super(false);
        shownMessages = new ArrayList<>();
    }

    @Override
    void setup(final boolean addFileUpload) {
        if (initialized) {
            super.setup(addFileUpload);
        }
    }

    void forceInitForm(final boolean addFileUpload) {
        this.initialized = true;
        setup(addFileUpload);
    }

    void setValid(final boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    boolean isValid() {
        return isValid;
    }

    @Override
    public void showMessage(String message) {
        shownMessages.add(message);
    }

    public List<String> getShownMessages() {
        return shownMessages;
    }
}
