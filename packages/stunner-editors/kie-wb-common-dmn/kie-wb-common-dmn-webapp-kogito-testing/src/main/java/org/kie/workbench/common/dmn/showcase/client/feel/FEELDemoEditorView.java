/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.feel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@ApplicationScoped
public class FEELDemoEditorView implements FEELDemoEditor.View {

    @DataField("text")
    private final HTMLTextAreaElement text;

    @DataField("nodes")
    private final HTMLTextAreaElement nodes;

    @DataField("evaluation")
    private final HTMLTextAreaElement evaluation;

    @DataField("functions")
    private final HTMLTextAreaElement functions;

    @DataField("suggestions")
    private final HTMLTextAreaElement suggestions;

    private FEELDemoEditor presenter;

    @Inject
    public FEELDemoEditorView(final HTMLTextAreaElement text,
                              final HTMLTextAreaElement nodes,
                              final HTMLTextAreaElement evaluation,
                              final HTMLTextAreaElement functions,
                              final HTMLTextAreaElement suggestions) {
        this.text = text;
        this.nodes = nodes;
        this.evaluation = evaluation;
        this.functions = functions;
        this.suggestions = suggestions;
    }

    @Override
    public void init(final FEELDemoEditor presenter) {
        this.presenter = presenter;
    }

    @EventHandler("text")
    public void onTextChange(final KeyUpEvent e) {
        presenter.onTextChange(text.value);
    }

    @Override
    public void setText(final String text) {
        this.text.value = text;
    }

    @Override
    public void setNodes(final String nodes) {
        this.nodes.value = nodes;
    }

    @Override
    public void setEvaluation(final String evaluation) {
        this.evaluation.value = evaluation;
    }

    @Override
    public void setFunctions(final String functions) {
        this.functions.value = functions;
    }

    @Override
    public void setSuggestions(final String suggestions) {
        this.suggestions.value = suggestions;
    }
}
