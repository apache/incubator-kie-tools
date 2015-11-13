/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.sandbox.client.markdown.viewer;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class MarkdownType implements ClientResourceType {

    @Override
    public String getShortName() {
        return "markdown";
    }

    @Override
    public String getDescription() {
        return "Markdown file";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "md";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*.md";
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "" + getSuffix() );
    }
}
