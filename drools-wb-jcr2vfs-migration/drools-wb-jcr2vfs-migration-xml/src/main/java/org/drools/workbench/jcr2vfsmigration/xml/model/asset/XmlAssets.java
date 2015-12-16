/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.jcr2vfsmigration.xml.model.asset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class XmlAssets {
    private Collection<XmlAsset> cAssets;

    public XmlAssets() {
        this.cAssets = new ArrayList<XmlAsset>();
    }

    public XmlAssets( Collection<XmlAsset> assets ) {
        this.cAssets = assets != null ? assets : new ArrayList<XmlAsset>();
    }

    public void addAsset( XmlAsset xmlAsset ) {
        this.cAssets.add( xmlAsset );
    }

    public Collection<XmlAsset> getAssets() {
        return Collections.unmodifiableCollection( cAssets );
    }
}
