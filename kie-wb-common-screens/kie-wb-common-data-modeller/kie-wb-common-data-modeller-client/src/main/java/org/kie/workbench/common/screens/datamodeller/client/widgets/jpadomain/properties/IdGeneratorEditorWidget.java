/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.BasePopupPropertyEditorWidget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public class IdGeneratorEditorWidget extends BasePopupPropertyEditorWidget {

    @Override protected PropertyEditionPopup createEditionPopup( PropertyEditorFieldInfo property ) {
        final PropertyEditionPopup popup = GWT.create( IdGeneratorEditionDialog.class );
        popup.setProperty( property );
        return popup;
    }
}
