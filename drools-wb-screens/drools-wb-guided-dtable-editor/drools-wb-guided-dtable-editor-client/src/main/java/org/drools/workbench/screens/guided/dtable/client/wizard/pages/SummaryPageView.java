/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;

/**
 * View and Presenter definitions for the Summary page
 */
public interface SummaryPageView
        extends
        UberView<SummaryPageView.Presenter> {

    interface Presenter {

        void stateChanged();

        String getBaseFileName();

    }

    String getBaseFileName();

    void setBaseFileName( String baseFileName );

    void setValidBaseFileName( final boolean isValid );

    void setContextPath( Path contextPath );

    void setTableFormat( GuidedDecisionTable52.TableFormat tableFormat );

    void setHitPolicy( GuidedDecisionTable52.HitPolicy hitPolicy );

}
