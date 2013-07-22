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

import com.google.gwt.user.cellview.client.Column;
import org.guvnor.common.services.shared.builder.BuildMessage;

/**
 * A column for the BuildMessage.Level
 */
public class BuildMessageLevelColumn extends Column<BuildMessage, BuildMessage.Level> {

    public BuildMessageLevelColumn() {
        super( new BuildMessageLevelCell() );
    }

    @Override
    public BuildMessage.Level getValue( final BuildMessage msg ) {
        return msg.getLevel();
    }

}
