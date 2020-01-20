/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import java.util.Optional;

import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.client.callbacks.Callback;

public class EditableCollectionBox extends EditableTextBox {

    private static final String COLLECTION_PREFIX = "=";

    public EditableCollectionBox(final Callback<String> changed,
                                 final TextBox view,
                                 final String fieldName,
                                 final String initialValue) {
        super(changed, view, fieldName, withoutCollectionPrefix(initialValue));
    }

    @Override
    public void onValueChange(final String value) {
        super.onValueChange(withCollectionPrefix(value));
    }

    static String withCollectionPrefix(final String value) {
        return Optional
                .ofNullable(value)
                .map(val -> hasCollectionPrefix(val) ? val : COLLECTION_PREFIX + val)
                .orElse("");
    }

    static String withoutCollectionPrefix(final String value) {
        return Optional
                .ofNullable(value)
                .map(val -> hasCollectionPrefix(val) ? val.substring(1) : val)
                .orElse("");
    }

    private static boolean hasCollectionPrefix(final String value) {
        return Optional
                .ofNullable(value)
                .filter(val -> val.length() > 0)
                .map(val -> val.substring(0, 1).equals(COLLECTION_PREFIX))
                .orElse(false);
    }
}
