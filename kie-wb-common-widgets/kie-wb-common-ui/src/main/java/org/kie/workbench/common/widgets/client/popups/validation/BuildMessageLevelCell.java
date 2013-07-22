/*
 * Copyright 2011 JBoss Inc
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
package org.kie.workbench.common.widgets.client.popups.validation;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

/**
 * A cell to render the BuildMessage.Level
 */
public class BuildMessageLevelCell extends AbstractCell<BuildMessage.Level> {

    private static String htmlErrorImageHtml = AbstractImagePrototype.create( CommonImages.INSTANCE.error() ).getHTML();
    private static String htmlInformationImageHtml = AbstractImagePrototype.create( CommonImages.INSTANCE.information() ).getHTML();
    private static String htmlWarningImageHtml = AbstractImagePrototype.create( CommonImages.INSTANCE.warning() ).getHTML();

    @Override
    public void render( final Context context,
                        final BuildMessage.Level value,
                        final SafeHtmlBuilder sb ) {
        if ( value == null ) {
            return;
        }
        switch ( value ) {
            case ERROR:
                sb.appendHtmlConstant( htmlErrorImageHtml );
                break;
            case INFO:
                sb.appendHtmlConstant( htmlInformationImageHtml );
                break;
            case WARNING:
                sb.appendHtmlConstant( htmlWarningImageHtml );
        }
    }

}
