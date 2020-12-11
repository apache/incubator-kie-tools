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

package org.dashbuilder.transfer;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.external.model.ExternalComponent;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@ApplicationScoped
public class LayoutComponentHelper {

    @Inject
    private PerspectiveServices perspectiveServices;

    public List<String> findComponentsInTemplates(Predicate<String> pageFilter) {
        return perspectiveServices.listLayoutTemplates()
                                  .stream()
                                  .filter(lt -> pageFilter.test(lt.getName()))
                                  .map(LayoutTemplate::getRows)
                                  .flatMap(this::allComponentsStream)
                                  .map(lt -> lt.getProperties().get(ExternalComponent.COMPONENT_ID_KEY))
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.toList());
    }

    private Stream<LayoutComponent> allComponentsStream(List<LayoutRow> row) {
        return row.stream()
                  .flatMap(r -> r.getLayoutColumns().stream())
                  .flatMap(cl -> Stream.concat(cl.getLayoutComponents().stream(),
                                               allComponentsStream(cl.getRows())));
    }

}
