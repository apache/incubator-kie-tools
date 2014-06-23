/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.socialscreen.client.discussion;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;

public class CommentLine
        implements IsWidget {

    private DiscussionRecord record;

    public CommentLine(DiscussionRecord record) {
        this.record = record;
    }

    public DiscussionRecord getRecord() {
        return record;
    }

    @Override
    public Widget asWidget() {
        return new Label(record.getTimestamp() + " - " + record.getAuthor() + " - " + record.getNote());
    }
}
