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

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.wizards.WizardContext;

/**
 * A container for the details required to create a new Asset on the repository
 */
public abstract class NewAssetWizardContext implements WizardContext {

    private final String baseFileName;
    private final Path contextPath;

    public NewAssetWizardContext( final String baseFileName,
                                  final Path contextPath ) {
        this.baseFileName = baseFileName;
        this.contextPath = contextPath;
    }

    public String getBaseFileName() {
        return baseFileName;
    }

    public Path getContextPath() {
        return contextPath;
    }

}
