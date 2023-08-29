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


package org.kie.workbench.common.stunner.core.client.shape;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A glyph that represents an SVG image.
 * Additional SVG declarations can be added for inserting
 * into the <code>SVG def</code> generated.
 * <p/>
 * Default renderers display this image by parsing or attaching the svg declarations
 * into the client response, this way it provides full DOM support for the SVG, as it's not
 * just being rendered as an image (as when using an ImageDataUriGlyph).
 */
public final class SvgDataUriGlyph implements Glyph {

    private final SafeUri uri;
    private final Collection<SafeUri> defUris;
    private final Collection<String> validUseRefIds;

    public static class Builder {

        private SafeUri mainUri;
        private final Map<String, SafeUri> useRefIds;

        public static Builder create() {
            return new Builder(new HashMap<>());
        }

        private Builder(final Map<String, SafeUri> useRefIds) {
            this.useRefIds = useRefIds;
        }

        public Builder setUri(final SafeUri uri) {
            this.mainUri = uri;
            return this;
        }

        public Builder addUri(final String useRefId,
                              final SafeUri uri) {
            if (null != uri) {
                useRefIds.put(useRefId,
                              uri);
            }
            return this;
        }

        public SvgDataUriGlyph build(final String refId) {
            assert null != mainUri;
            final SafeUri safeUri = useRefIds.get(refId);
            return null != safeUri ?
                    new SvgDataUriGlyph(mainUri,
                                        Optional.of(Arrays.asList(safeUri)),
                                        Optional.of(Arrays.asList(refId))) :
                    new SvgDataUriGlyph(mainUri,
                                        Optional.empty(),
                                        Optional.empty());
        }

        public SvgDataUriGlyph build() {
            assert null != mainUri;
            return new SvgDataUriGlyph(mainUri,
                                       useRefIds.isEmpty() ?
                                               Optional.empty() :
                                               Optional.of(useRefIds.values()),
                                       useRefIds.isEmpty() ?
                                               Optional.empty() :
                                               Optional.of(useRefIds.keySet()));
        }

        public static SvgDataUriGlyph build(final SafeUri uri) {
            return Builder.create()
                    .setUri(uri)
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private SvgDataUriGlyph(final SafeUri uri,
                            final Optional<Collection<SafeUri>> defUris,
                            final Optional<Collection<String>> validUseRefIds) {
        this.uri = uri;
        this.defUris = defUris.orElse(Collections.EMPTY_LIST);
        this.validUseRefIds = validUseRefIds.orElse(Collections.EMPTY_LIST);
    }

    public SafeUri getSvg() {
        return uri;
    }

    public Collection<SafeUri> getDefs() {
        return defUris;
    }

    public Collection<String> getValidUseRefIds() {
        return validUseRefIds;
    }
}
