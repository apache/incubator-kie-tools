/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.booleans;

import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.HasErrorMessage;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorView;
import org.uberfire.commons.data.Pair;

public interface BooleanValuePairEditorView
        extends ValuePairEditorView<BooleanValuePairEditorView.Presenter>,
            HasErrorMessage {

    interface Presenter {

        void onValueChange();
    }

    void setSelectedValue( String value );

    String getSelectedValue();

    void initOptions( List<Pair<String, String>> options );

}
