/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.generator;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.Optional;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;

public interface LayoutGeneratorDriver {

    HTMLElement createContainer();

    HTMLElement createRow(LayoutRow layoutRow);

    HTMLElement createColumn(LayoutColumn layoutColumn);

    IsWidget createComponent(HTMLElement column, LayoutComponent layoutComponent);

    default Optional<IsWidget> getComponentPart(HTMLElement column, LayoutComponent layoutComponent, String partId) {
        return Optional.empty();
    }
}
