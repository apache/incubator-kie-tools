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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to show a <b>key/value</b> <b>item</b> of a <b>map</b>
 *
 */
@Templated
public class KeyValueElementViewImpl extends ElementViewImpl<KeyValueElementView.Presenter> implements KeyValueElementView {

    @DataField("keyValueContainer")
    protected LIElement keyValueContainer = Document.get().createLIElement();

    @DataField("keyContainer")
    protected UListElement keyContainer = Document.get().createULElement();

    @DataField("valueContainer")
    protected UListElement valueContainer = Document.get().createULElement();

    @DataField("keyLabel")
    protected LIElement keyLabel = Document.get().createLIElement();

    @DataField("valueLabel")
    protected LIElement valueLabel = Document.get().createLIElement();

    @Override
    public UListElement getKeyContainer() {
        return keyContainer;
    }

    @Override
    public UListElement getValueContainer() {
        return valueContainer;
    }

    @Override
    public LIElement getKeyLabel() {
        return keyLabel;
    }

    @Override
    public LIElement getValueLabel() {
        return valueLabel;
    }

    @Override
    public LIElement getSaveChange() {
        return saveChange;
    }

    @Override
    public SpanElement getFaAngleRight() {
        return faAngleRight;
    }

}
