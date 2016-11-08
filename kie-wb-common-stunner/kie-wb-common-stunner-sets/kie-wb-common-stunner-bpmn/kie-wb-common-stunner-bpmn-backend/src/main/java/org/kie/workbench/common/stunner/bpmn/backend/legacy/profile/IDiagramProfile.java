/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.backend.legacy.profile;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.ecore.resource.Resource;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.repository.Repository;

import javax.servlet.ServletContext;
import java.util.Collection;

/**
 * A profile for the editor to choose which stencilset
 * and which plugins should be loaded.
 *
 * @author Antoine Toulme
 */
public interface IDiagramProfile {

    /**
     * @return the name of the profile
     * it will be passed by the user when opening the editor.
     */
    public String getName();

    /**
     * @return the title of the profile.
     */
    public String getTitle();

    /**
     * @return the stencil set used by the profile.
     */
    public String getStencilSet();

    /**
     * @return the stencil set extensions used by the profile
     */
    public Collection<String> getStencilSetExtensions();

    public String getSerializedModelExtension();

    /**
     * @return the stencil url used by the profile.
     */
    public String getStencilSetURL();

    /**
     * @return stencil namespace url.
     */
    public String getStencilSetNamespaceURL();

    /**
     * @return stencil set extension url used by the profile.
     */
    public String getStencilSetExtensionURL();

    /**
     * @return the plugins to load for the profile.
     */
    public Collection<String> getPlugins();

    /**
     * @return a classLoader to transform the json into the final model.
     */
    public IDiagramMarshaller createMarshaller();

    /**
     * @return an unmarshaller to transform the model into the json.
     */
    public IDiagramUnmarshaller createUnmarshaller();

    public String getRepositoryGlobalDir();

    public String getRepositoryGlobalDir( String uuid );

    /**
     * @return the local history enabled.
     */
    public String getLocalHistoryEnabled();

    /**
     * @return the local history timeout.
     */
    public String getLocalHistoryTimeout();

    /**
     * @return the store svg on save option.
     */
    public String getStoreSVGonSaveOption();

    /**
     * @return the repository.
     */
    public Repository getRepository();

    /**
     * Parser to produce the final model to be saved.
     *
     * @author Antoine Toulme
     */
    public interface IDiagramMarshaller {

        /**
         * @param jsonModel the model
         * @return the string representation of the serialized model.
         */
        public String parseModel( String jsonModel, String preProcessingData ) throws Exception;

        public Definitions getDefinitions( String jsonModel, String preProcessingData ) throws Exception;

        public Resource getResource( String jsonModel, String preProcessingData ) throws Exception;
    }

    /**
     * Parser to produce the final model to be saved.
     *
     * @author Tihomir Surdilovic
     */
    public interface IDiagramUnmarshaller {

        /**
         * @param xmlModel xml model
         * @param profile  process profile.
         * @return the json model
         */
        public String parseModel( String xmlModel, IDiagramProfile profile, String preProcessingData ) throws Exception;
    }

    public void init( ServletContext context );

}
