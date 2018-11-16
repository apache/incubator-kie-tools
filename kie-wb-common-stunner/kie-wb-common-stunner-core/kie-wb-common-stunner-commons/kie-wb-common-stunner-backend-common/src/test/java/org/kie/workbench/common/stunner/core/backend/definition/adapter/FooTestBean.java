/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.definition.adapter;

import java.util.HashSet;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Definition(graphFactory = NodeFactory.class, nameField = FooTestBean.FOO_PROPERTY_NAME)
public class FooTestBean {

    public static final String FOO_PROPERTY_NAME = "fooProperty";

    @Category
    public static final String CATEGORY = "cat1";

    @Title
    public static final String TITLE = "title1";

    @Description
    public static final String DESCRIPTION = "desc1";

    @Labels
    public static final Set<String> LABELS = new HashSet<String>() {{
        add("label1");
        add("label2");
    }};

    @PropertySet
    public FooPropertySetTestBean fooPropertySet;

    @Property
    public FooProperty2TestBean fooProperty;

    public FooTestBean(String value1,
                       String value2) {
        this.fooPropertySet = new FooPropertySetTestBean(value1);
        this.fooProperty = new FooProperty2TestBean(value2);
    }
}
