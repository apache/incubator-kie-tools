/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.client.editor;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;

@Templated
public class ExperimentalFeaturesEditorScreenViewImpl implements ExperimentalFeaturesEditorScreenView,
                                                                 IsElement {

    private Presenter presenter;

    @Inject
    private Elemental2DomUtil util;

    @Inject
    @DataField
    private HTMLDivElement container;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        util.removeAllElementChildren(container);
    }

    @Override
    public void add(ExperimentalFeaturesGroup experimentalFeaturesGroup) {
        container.appendChild(experimentalFeaturesGroup.getElement());
    }
}
