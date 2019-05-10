/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.select;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOption;

import static java.util.stream.Collectors.toList;

@Dependent
public class KieEnumSelectElement<T extends Enum<T>> {

    private final KieSelectElement kieSelectElement;
    private final TranslationService translationService;

    Class<T> componentType;

    @Inject
    public KieEnumSelectElement(final KieSelectElement kieSelectElement,
                                final TranslationService translationService) {

        this.kieSelectElement = kieSelectElement;
        this.translationService = translationService;
    }

    @SuppressWarnings("unchecked")
    public void setup(final Element element,
                      final T[] values,
                      final T initialValue,
                      final Consumer<T> onChange) {

        componentType = (Class<T>) values.getClass().getComponentType();

        kieSelectElement.setup(
                element,
                buildOptions(values),
                initialValue.name(),
                name -> onChange.accept(toEnum(name)));
    }

    List<KieSelectOption> buildOptions(final T[] values) {
        return Arrays.stream(values).map(this::newOption).collect(toList());
    }

    KieSelectOption newOption(final T e) {
        return new KieSelectOption(getLabel(e), e.name());
    }

    String getLabel(final T e) {
        return translationService.format(e.name());
    }

    public T getValue() {
        return toEnum(kieSelectElement.getValue());
    }

    T toEnum(final String value) {
        return Enum.valueOf(componentType, value);
    }
}
