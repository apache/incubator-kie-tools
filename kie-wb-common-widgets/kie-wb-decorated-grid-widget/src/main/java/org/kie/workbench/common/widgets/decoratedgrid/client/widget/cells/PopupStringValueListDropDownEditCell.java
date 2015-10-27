/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Arrays;
import java.util.List;

/**
 * A Popup drop-down Editor ;-)
 */
public class PopupStringValueListDropDownEditCell extends
                                                  PopupValueListDropDownEditCell<String> {

    public PopupStringValueListDropDownEditCell( final String[] items,
                                                 final boolean isReadOnly ) {
        super( items,
               isReadOnly );
    }

    public PopupStringValueListDropDownEditCell( final String[] items,
                                                 final boolean isMultipleSelect,
                                                 final boolean isReadOnly ) {
        super( items,
               isMultipleSelect,
               isReadOnly );
    }



}
