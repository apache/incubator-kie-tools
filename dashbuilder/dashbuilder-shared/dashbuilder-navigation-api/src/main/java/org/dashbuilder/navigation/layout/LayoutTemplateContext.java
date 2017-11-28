/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.layout;

import org.dashbuilder.navigation.NavGroup;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * A {@link LayoutTemplate}'s context contains for instance the identifier of the {@link NavGroup} to be used by
 * any of the layout's navigation components.
 */
@Portable
public class LayoutTemplateContext {

    String navGroupId;

    public LayoutTemplateContext() {
    }

    public LayoutTemplateContext(String navGroupId) {
        this.navGroupId = navGroupId;
    }

    public String getNavGroupId() {
        return navGroupId;
    }
}
