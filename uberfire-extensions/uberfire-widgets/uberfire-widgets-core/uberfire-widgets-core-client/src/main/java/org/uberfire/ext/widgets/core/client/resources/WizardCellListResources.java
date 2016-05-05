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
package org.uberfire.ext.widgets.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;

/**
 * Overriding the default styling for CellLists
 */
public interface WizardCellListResources
        extends
        CellList.Resources {

    WizardCellListResources INSTANCE = GWT.create( WizardCellListResources.class );

    @Source("css/WizardsCellList.css")
    WizardCellListStyle cellListStyle();

    public interface WizardCellListStyle
            extends
            CellList.Style {

        String cellListEvenItem();

        String cellListKeyboardSelectedItem();

        String cellListOddItem();

        String cellListSelectedItem();

        String cellListWidget();

        String cellListEmptyItem();

    }

}