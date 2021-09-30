/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import java.util.List;
import java.util.function.Supplier;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class InputAutocomplete implements IsElement {

    @Inject
    @DataField("input")
    HTMLInputElement input;

    @Inject
    private JQueryElementalProducer.JQuery<InputAutocompleteElement> jQuery;

    public void setup(final Supplier<List<String>> optionsSupplier) {
        final InputAutocompleteOptions inputAutocompleteOptions = new InputAutocompleteOptions();
        List<String> options = optionsSupplier.get();
        if (options != null) {
            String[] optionsArray = options.stream()
                    .toArray(String[]::new);
            inputAutocompleteOptions.setSource(optionsArray);
        }

        jQuery.wrap(getElement()).autocomplete(inputAutocompleteOptions);
    }

    public void setValue(final String value) {
        this.input.value = value;
    }

    public String getValue() {
        return this.input.value;
    }

    @JsType(isNative = true)
    public interface InputAutocompleteElement extends JQueryElementalProducer.JQueryElement {

        void autocomplete(InputAutocompleteOptions options);
    }

    @Override
    public HTMLElement getElement() {
        return input;
    }
}
