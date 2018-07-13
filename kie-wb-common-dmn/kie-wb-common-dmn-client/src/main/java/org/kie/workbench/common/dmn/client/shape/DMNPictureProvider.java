/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.shape;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.dmn.client.resources.DMNImageResources;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class DMNPictureProvider implements PictureProvider<DMNPictures> {

    private static final Map<DMNPictures, SafeUri> PICTURE_URIS =
            new HashMap<DMNPictures, SafeUri>() {{
                put(DMNPictures.DIAGRAM,
                    DMNImageResources.INSTANCE.diagramToolbox().getSafeUri());
                put(DMNPictures.INPUT_DATA,
                    DMNImageResources.INSTANCE.inputDataToolbox().getSafeUri());
                put(DMNPictures.KNOWLEDGE_SOURCE,
                    DMNImageResources.INSTANCE.knowledgeSourceToolbox().getSafeUri());
                put(DMNPictures.BUSINESS_KNOWLEDGE_MODEL,
                    DMNImageResources.INSTANCE.businessKnowledgeModelToolbox().getSafeUri());
                put(DMNPictures.DECISION,
                    DMNImageResources.INSTANCE.decisionToolbox().getSafeUri());
                put(DMNPictures.TEXT_ANNOTATION,
                    DMNImageResources.INSTANCE.textAnnotationToolbox().getSafeUri());
            }};

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
        checkNotNull("source",
                     source);
        return PICTURE_URIS.get(source);
    }
}
