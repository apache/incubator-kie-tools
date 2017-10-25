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

package org.guvnor.ala.ui.client.widget.item;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

public abstract class SelectableCardItemBase
        implements IsElement {

    private static final String CARD_ACCENTED_CLASS = "card-pf-accented";

    private static final String REMOVE_OPTION = "remove-option";

    private static final String OPACITY = "opacity";

    private static final String OPACITY_VALUE = "0.3";

    public void disable() {
        getAccentedArea().getClassList().add(REMOVE_OPTION);
    }

    public boolean isDisabled() {
        return getAccentedArea().getClassList().contains(REMOVE_OPTION);
    }

    public boolean isSelected() {
        return hasCSSClass(getAccentedArea(),
                           CARD_ACCENTED_CLASS);
    }

    public void setSelected(boolean selected) {
        removeCSSClass(getAccentedArea(),
                       CARD_ACCENTED_CLASS);
        if (selected) {
            addCSSClass(getAccentedArea(),
                        CARD_ACCENTED_CLASS);
            removeOpacity();
        } else {
            addOpacity();
        }
    }

    protected abstract Div getAccentedArea();

    protected abstract Div getBody();

    private void addOpacity() {
        getBody().getStyle().setProperty(OPACITY,
                                         OPACITY_VALUE);
    }

    private void removeOpacity() {
        getBody().getStyle().removeProperty(OPACITY);
    }
}
