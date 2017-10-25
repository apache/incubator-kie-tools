/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.wizard.pipeline.select.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.util.AbstractHasContentChangeHandlers;
import org.guvnor.ala.ui.model.PipelineKey;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class PipelineItemPresenter
        extends AbstractHasContentChangeHandlers {

    public interface View
            extends UberElement<PipelineItemPresenter> {

        boolean isSelected();

        void setSelected(boolean selected);

        void setPipelineName(String name);
    }

    private final View view;

    private final Collection<PipelineItemPresenter> others = new ArrayList<>();
    private PipelineKey pipeline;

    @Inject
    public PipelineItemPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final PipelineKey pipeline) {
        this.pipeline = pipeline;
        view.setPipelineName(pipeline.getId());
    }

    public PipelineKey getPipeline() {
        return pipeline;
    }

    public void addOthers(final Collection<PipelineItemPresenter> items) {
        others.addAll(items.stream()
                              .filter(other -> !other.equals(this))
                              .collect(Collectors.toList())
        );
    }

    public boolean isSelected() {
        return view.isSelected();
    }

    public IsElement getView() {
        return view;
    }

    protected void onItemClick() {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
            unSelectOthers();
        }
        fireChangeHandlers();
    }

    private void unSelectOthers() {
        others.forEach(PipelineItemPresenter::unSelect);
    }

    private void unSelect() {
        view.setSelected(false);
    }
}
