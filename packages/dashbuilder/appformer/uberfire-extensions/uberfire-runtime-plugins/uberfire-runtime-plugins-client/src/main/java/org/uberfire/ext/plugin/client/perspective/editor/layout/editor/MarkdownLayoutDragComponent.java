/*
 * Copyright 2023 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.plugin.client.resources.ResourcesInjector;

@Dependent
public class MarkdownLayoutDragComponent implements LayoutDragComponent {

    public static final String MARKDOWN_CODE_PARAMETER = "MARKDOWN_CODE";
    
    @PostConstruct
    void setup() {
        ResourcesInjector.ensureMarkedJsInjected();
    }

    @Override
    public IsWidget getShowWidget(RenderingContext context) {
        var properties = context.getComponent().getProperties();
        var markdown = properties.get(MarkdownLayoutDragComponent.MARKDOWN_CODE_PARAMETER);
        if (markdown == null) {
            return null;
        }
        var html = Marked.Builder.get().parse(markdown);
        return new HTMLPanel(html);
    }

}
