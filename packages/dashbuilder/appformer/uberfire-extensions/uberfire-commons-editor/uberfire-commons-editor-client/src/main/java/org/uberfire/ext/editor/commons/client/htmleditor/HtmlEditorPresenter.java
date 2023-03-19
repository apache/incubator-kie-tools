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

package org.uberfire.ext.editor.commons.client.htmleditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.BaseEditorView;

@Dependent
public class HtmlEditorPresenter {

    private final View view;

    @Inject
    public HtmlEditorPresenter(final View view) {
        this.view = view;
    }

    public void load() {
        view.load();
    }

    public View getView() {
        return view;
    }

    public String getContent() {
        return view.getContent();
    }

    public void setContent(final String content) {
        view.setContent(content);
    }

    public void destroy() {
        view.destroy();
    }

    public interface View extends UberElement<HtmlEditorPresenter>,
                                  BaseEditorView {

        String getContent();

        void setContent(String content);

        void load();

        void destroy();
    }
}
