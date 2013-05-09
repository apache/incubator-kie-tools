/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.wizard;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.uberfire.backend.vfs.Path;

/**
 * A container for the details required to create a new Guided Decision Table
 * Asset on the repository
 */
public class NewGuidedDecisionTableAssetWizardContext extends NewAssetWizardContext {

    private final GuidedDecisionTable52.TableFormat tableFormat;

    public NewGuidedDecisionTableAssetWizardContext( final String baseFileName,
                                                     final Path contextPath,
                                                     final GuidedDecisionTable52.TableFormat tableFormat ) {
        super( baseFileName,
               contextPath );
        this.tableFormat = tableFormat;
    }

    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return this.tableFormat;
    }

}
