/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.uberfire.client.mvp;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.uberfire.workbench.model.bridge.Notification;

public interface EditorActivity extends Activity {

    Promise<Void> setContent(String path, String value);

    Promise<String> getContent();

    Promise<String> getPreview();

    Promise<List<Notification>> validate();

    default Element getWidgetElement() {
        IsWidget widget = getWidget();
        return (widget == null) ? null : widget.asWidget().getElement();
    }

    default Promise<Void> undo() {
        throw new UnsupportedOperationException("The editor does not support undo.");
    }

    default Promise<Void> redo() {
        throw new UnsupportedOperationException("The editor does not support redo.");
    }

    default Promise<Void> searchDomainObject(final String uuid) {
        throw new UnsupportedOperationException("The editor does not support search domain object.");
    }
}
