/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.mvp.UberView;

public interface ImportsWidgetView
        extends UberView<ImportsWidgetView.Presenter> {

    interface Presenter {

        void setContent( final AsyncPackageDataModelOracle dmo,
                         final Imports resourceImports,
                         final boolean isReadOnly );

        void onAddImport( final Import importType );

        void onRemoveImport( final Import importType );

        Widget asWidget();

    }

    void setContent( final List<Import> internalFactTypes,
                     final List<Import> externalFactTypes,
                     final List<Import> importTypes,
                     final boolean isReadOnly );

}
