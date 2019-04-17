/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.File;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js.Document;
import org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.preview.DocumentPreview;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;

public interface DocumentUploadView extends IsWidget,
                                            FormWidget<List<DocumentData>> {

    void setPresenter(Presenter presenter);

    void clear();

    void addDocument(DocumentPreview preview);

    void removeDocument(DocumentPreview preview);

    void setEnabled(boolean enabled);

    void setMaxDocuments(String text);

    interface Presenter extends HasValue<List<DocumentData>> {

        void doUpload(Document document, File file);

        Collection<DocumentPreview> getCurrentPreviews();
    }
}
