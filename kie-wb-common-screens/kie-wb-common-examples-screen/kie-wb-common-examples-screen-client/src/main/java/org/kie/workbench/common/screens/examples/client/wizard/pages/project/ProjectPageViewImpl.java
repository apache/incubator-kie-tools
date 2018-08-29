/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.project;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.examples.model.ImportProject;

@Dependent
@Templated
public class ProjectPageViewImpl extends Composite implements ProjectPageView {

    @DataField
    Element projects = DOM.createDiv();

    @DataField("project-description")
    Element projectDescription = DOM.createDiv();

    @Inject
    @DataField("tagInput")
    TextInput tagInput;

    @Inject
    @DataField("searchButton")
    Button searchButton;

    @DataField("tagList")
    Element tagList = DOM.createElement("ul");

    @Inject
    @DataField("clear")
    Anchor clear;

    @Inject
    private ManagedInstance<ProjectItemView> projectItemViewInstance;

    @Inject
    private ManagedInstance<TagItemView> tagItemViewInstance;

    private ProjectPage presenter;

    @Override
    public void init(final ProjectPage presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initialise() {
        destroy();
    }

    @Override
    public void setProjectsInRepository(final List<ImportProject> projects) {
        this.projects.removeAllChildren();
        for (ImportProject project : projects) {
            final ProjectItemView w = makeProjectWidget(project);
            this.projects.appendChild(w.asWidget().getElement());
        }
    }

    @Override
    public void destroy() {
        projects.removeAllChildren();
        tagInput.setValue("");
        tagList.removeAllChildren();
    }

    private ProjectItemView makeProjectWidget(final ImportProject project) {
        final ProjectItemView projectItemView = projectItemViewInstance.get();
        projectItemView.setProject(project,
                                   presenter.isProjectSelected(project));
        projectItemView.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
                final boolean selected = event.getValue();
                if (selected) {
                    presenter.addProject(project);
                } else {
                    presenter.removeProject(project);
                }
            }
        });

        projectItemView.addMouseOverHandler(h -> {
            final SafeHtmlBuilder shb = new SafeHtmlBuilder();
            shb.appendEscaped(project.getDescription());
            projectDescription.setInnerSafeHtml(shb.toSafeHtml());
        });

        projectItemView.addMouseOutHandler(h -> {
            projectDescription.setInnerSafeHtml(new SafeHtmlBuilder().toSafeHtml());
        });
        return projectItemView;
    }

    @EventHandler("searchButton")
    private void onSearchButtonClicked(ClickEvent event) {
        createAndAddTag(false);
    }

    @EventHandler("tagInput")
    private void onTagInputEnterPressed(KeyUpEvent event) {
        int keyCode = event.getNativeKeyCode();
        if (keyCode == KeyCodes.KEY_ENTER) {
            createAndAddTag(true);
        } else {
            presenter.addPartialTag(tagInput.getValue());
            tagInput.focus();
        }
    }

    private void createAndAddTag(boolean setTagInputFocus) {
        if (tagInput.getValue() != null && !tagInput.getValue().isEmpty()) {
            TagItemView tag = tagItemViewInstance.get();
            tag.setName(tagInput.getValue());
            Node tagNode = tag.asWidget().getElement();
            tag.addClickHandler(c -> {
                tagList.removeChild(tagNode);
                presenter.removeTag(tag.getName());
            });
            tagList.appendChild(tagNode);
            presenter.addTag(tagInput.getValue());
            tagInput.setValue("");
            if (setTagInputFocus) {
                tagInput.focus();
            }
        }
    }

    @EventHandler("clear")
    private void onClearClicked(ClickEvent event) {
        tagList.removeAllChildren();
        presenter.removeAllTags();
    }
}
