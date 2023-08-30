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

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

/**
 * A glyph for image data-uri's.
 * Default renderers display it as a picture or image.
 */
public final class ImageDataUriGlyph implements Glyph {

    private final SafeUri uri;

    public static ImageDataUriGlyph create(final SafeUri uri) {
        return new ImageDataUriGlyph(uri);
    }

    private ImageDataUriGlyph(final SafeUri uri) {
        this.uri = uri;
    }

    public SafeUri getUri() {
        return uri;
    }
}
