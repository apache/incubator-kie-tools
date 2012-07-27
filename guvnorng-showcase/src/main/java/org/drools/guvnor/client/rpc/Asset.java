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

package org.drools.guvnor.client.rpc;




import org.drools.guvnor.shared.api.PortableObject;

import java.io.Serializable;

/**
 * This is the "payload" of an asset.
 * Includes the meta data.
 */
public class Asset extends Artifact {

    public MetaData       metaData;
    public PortableObject content;

    public Asset setMetaData(MetaData metaData) {
        this.metaData = metaData;
        return this;
    }
    public MetaData getMetaData() {
        return metaData;
    }

    public Asset setContent(PortableObject content) {
        this.content = content;
        return this;
    }
    public PortableObject getContent() {
        return content;
    }

}
