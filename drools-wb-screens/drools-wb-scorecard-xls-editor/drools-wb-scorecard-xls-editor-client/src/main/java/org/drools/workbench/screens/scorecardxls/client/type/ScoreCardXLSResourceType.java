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

package org.drools.workbench.screens.scorecardxls.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.resources.ScoreCardXLSEditorResources;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.type.ScoreCardXLSResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ScoreCardXLSResourceType
        extends ScoreCardXLSResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( ScoreCardXLSEditorResources.INSTANCE.images().typeXLSScoreCard() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = ScoreCardXLSEditorConstants.INSTANCE.scoreCardXLSResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
