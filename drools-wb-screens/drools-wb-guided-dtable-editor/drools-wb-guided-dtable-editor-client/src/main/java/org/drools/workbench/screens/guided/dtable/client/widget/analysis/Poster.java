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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import org.drools.workbench.services.verifier.plugin.client.api.DeleteColumns;
import org.drools.workbench.services.verifier.plugin.client.api.DrlInitialize;
import org.drools.workbench.services.verifier.plugin.client.api.MakeRule;
import org.drools.workbench.services.verifier.plugin.client.api.NewColumn;
import org.drools.workbench.services.verifier.plugin.client.api.RemoveRule;
import org.drools.workbench.services.verifier.plugin.client.api.Update;
import org.kie.workbench.common.services.verifier.api.client.api.RequestStatus;

public class Poster
        extends org.kie.workbench.common.services.verifier.reporting.client.analysis.Poster {


    public void post(final MakeRule makeRule) {
        postObject(makeRule);
    }

    public void post(final DeleteColumns deleteColumns) {
        postObject(deleteColumns);
    }

    public void post(final NewColumn newColumn) {
        postObject(newColumn);
    }

    public void post(final Update update) {
        postObject(update);
    }

    public void post(final DrlInitialize initialize) {
        postObject(initialize);
    }

    public void post(final RequestStatus requestStatus) {
        postObject(requestStatus);
    }

    public void post(final RemoveRule removeRule) {
        postObject(removeRule);
    }
}
