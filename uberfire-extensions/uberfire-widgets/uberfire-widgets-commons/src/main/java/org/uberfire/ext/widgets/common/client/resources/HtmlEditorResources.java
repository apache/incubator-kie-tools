/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface HtmlEditorResources extends ClientBundle {

    HtmlEditorResources INSTANCE = GWT.create(HtmlEditorResources.class);

    @Source({"js/wysihtml/wysihtml.min.js"})
    TextResource wysihtml();

    @Source({"js/wysihtml/wysihtml.all-commands.min.js"})
    TextResource wysihtmlAllCommands();

    @Source({"js/wysihtml/wysihtml.table_editing.min.js"})
    TextResource wysihtmlTableEditing();

    @Source({"js/wysihtml/wysihtml.toolbar.min.js"})
    TextResource wysihtmlToolbar();

    @Source({"js/wysihtml/parser_rules/advanced_and_extended.js"})
    TextResource parserRules();
}
