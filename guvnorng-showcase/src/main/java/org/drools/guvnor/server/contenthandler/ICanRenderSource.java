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
package org.drools.guvnor.server.contenthandler;


import org.drools.guvnor.shared.api.PortableObject;


/**
 * Implementations are able to render their source as text
 */
public interface ICanRenderSource {

    /**
     * Render source of the PortableObject into the StringBuilder
     * 
     * @param assetContent
     * @param stringBuilder
     */
    public void assembleSource(PortableObject assetContent,
                               StringBuilder stringBuilder);

}
