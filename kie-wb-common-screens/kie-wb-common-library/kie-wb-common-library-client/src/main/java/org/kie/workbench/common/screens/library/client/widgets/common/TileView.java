/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.common;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.mvp.Command;

@Templated
public class TileView implements TileWidget.View,
                                 IsElement {

    private TileWidget presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("card")
    private Div card;

    @Inject
    @Named("h2")
    @DataField("label")
    private Heading label;

    @Inject
    @Named("h5")
    @DataField("description")
    private Heading description;

    @Inject
    @DataField("circle")
    private Span circle;

    @Override
    public void init(final TileWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setup(String label, String description, Command selectCommand) {
        this.label.setTextContent(label);
        this.description.setTextContent(description);
        this.description.setTitle(description);
        this.card.setOnclick(event -> selectCommand.execute());
        circle.setHidden(true);
    }

    @Override
    public void setNumberOfAssets(Integer numberOfAssets) {
        circle.setHidden(false);
        this.circle.setTextContent(String.valueOf(numberOfAssets));
        this.circle.setTitle(ts.format(LibraryConstants.NumberOfAssets, numberOfAssets));
    }

    @Override
    public boolean isSelected() {
        return card.getClassList().contains("active");
    }

    @Override
    public void setSelected(final boolean selected) {
        if (selected) {
            card.getClassList().add("active");
        } else {
            card.getClassList().remove("active");
        }
    }
}
