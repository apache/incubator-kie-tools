/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.client.shape;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.dmn.client.resources.DMNImageResources;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

@ApplicationScoped
public class DMNPictureProvider implements PictureProvider<DMNPictures> {

    private static final Map<DMNPictures, SafeUri> PICTURE_URIS =
            Stream.of(new AbstractMap.SimpleEntry<>(DMNPictures.DIAGRAM, DMNImageResources.INSTANCE.diagramToolbox().getSafeUri()),
                      new AbstractMap.SimpleEntry<>(DMNPictures.INPUT_DATA, DMNImageResources.INSTANCE.inputDataToolbox().getSafeUri()),
                      new AbstractMap.SimpleEntry<>(DMNPictures.KNOWLEDGE_SOURCE, DMNImageResources.INSTANCE.knowledgeSourceToolbox().getSafeUri()),
                      new AbstractMap.SimpleEntry<>(DMNPictures.BUSINESS_KNOWLEDGE_MODEL, DMNImageResources.INSTANCE.businessKnowledgeModelToolbox().getSafeUri()),
                      new AbstractMap.SimpleEntry<>(DMNPictures.DECISION, DMNImageResources.INSTANCE.decisionToolbox().getSafeUri()),
                      new AbstractMap.SimpleEntry<>(DMNPictures.TEXT_ANNOTATION, DMNImageResources.INSTANCE.textAnnotationToolbox().getSafeUri()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public Class<DMNPictures> getSourceType() {
        return DMNPictures.class;
    }

    @Override
    public boolean thumbFor(final DMNPictures source) {
        return get(source) != null;
    }

    @Override
    public SafeUri getThumbnailUri(final DMNPictures source) {
        return get(source);
    }

    private SafeUri get(final DMNPictures source) {
        Objects.requireNonNull(source, "Parameter named 'source' should be not null!");
        return PICTURE_URIS.get(source);
    }
}
