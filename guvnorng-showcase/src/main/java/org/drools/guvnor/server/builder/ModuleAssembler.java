/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import java.util.List;

import org.drools.repository.ModuleItem;

/**
 * This assembles modules into module deployment bundles, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * module deployment bundle.
 */
public interface ModuleAssembler {
    public void init(ModuleItem moduleItem, ModuleAssemblerConfiguration moduleAssemblerConfiguration);

    public void compile();

    /**
     * This will return true if there is an error in the module configuration
     * or functions.
     *
     * @return
     */
    public boolean isModuleConfigurationInError();

    public byte[] getCompiledBinary();
    public String getBinaryExtension();
    public String getCompiledSource();

    public boolean hasErrors();
    
    public List<ContentAssemblyError> getErrors();
}
