/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class InfoEditor
        implements IsWidget {

    protected DivWidget infoEditorContainer = new DivWidget( );
    protected HelpInline infoEditorContent = new HelpInline( );

    public InfoEditor() {
        infoEditorContainer.add( infoEditorContent );
    }

    public void setInfo( String info ) {
        infoEditorContent.setText( info );
    }

    public void clear() {
        infoEditorContent.setText( null );
    }

    @Override
    public Widget asWidget() {
        return infoEditorContainer.asWidget();
    }
}
