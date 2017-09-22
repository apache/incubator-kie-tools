/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Before;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class DataBinderEditorTest<EDITOR extends DataBindingEditor> {

    public static final String FIELD_BINDING = "field";
    public static final String NAME = "name";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String CP = "cp";

    protected Set<String> fields = new TreeSet<>();

    @Mock
    protected FieldDefinition fieldDefinition;

    protected Consumer<String> bindingChangeConsumer;

    protected Supplier<Collection<String>> bindingsSupplier;

    protected EDITOR editor;

    @Before
    public void initTest() {

        fields.add(FIELD_BINDING);
        fields.add(NAME);
        fields.add(LAST_NAME);
        fields.add(ADDRESS);
        fields.add(CITY);
        fields.add(CP);

        bindingsSupplier = spy(new Supplier<Collection<String>>() {
            @Override
            public Collection<String> get() {
                return fields;
            }
        });

        bindingChangeConsumer = spy(new Consumer<String>() {
            @Override
            public void accept(String s) {

            }
        });

        when(fieldDefinition.getBinding()).thenReturn(FIELD_BINDING);
    }
}
