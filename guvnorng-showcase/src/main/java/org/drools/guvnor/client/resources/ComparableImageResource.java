/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ImageResource;
//import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.SafeUri;

public class ComparableImageResource
    implements
    ImageResource,
    Comparable<ComparableImageResource> {

    private final String compareString;
    private final ImageResource imageResource;

    public ComparableImageResource(String compareString,
                                   ImageResource imageResource) {
        this.compareString = compareString;
        this.imageResource = imageResource;
    }

    public String getName() {
        return this.imageResource.getName();
    }

    public int compareTo(ComparableImageResource o) {
        return compareString.compareTo( o.compareString);
    }

    public int getHeight() {
        return this.imageResource.getHeight();
    }

    public int getLeft() {
        return this.imageResource.getLeft();
    }

    public int getTop() {
        return this.imageResource.getTop();
    }

    public String getURL() {
        return this.imageResource.getURL();
    }

    public int getWidth() {
        return this.imageResource.getWidth();
    }

    public boolean isAnimated() {
        return this.imageResource.isAnimated();
    }

    @Override
    public SafeUri getSafeUri() {
        return this.imageResource.getSafeUri();
    }

}
