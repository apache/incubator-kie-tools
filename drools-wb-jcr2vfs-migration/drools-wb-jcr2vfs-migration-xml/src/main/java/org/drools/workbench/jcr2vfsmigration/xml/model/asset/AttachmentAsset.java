/*
 * Copyright 2014 JBoss Inc
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

public class AttachmentAsset extends AbstractXmlAsset {

    private String attachmentFileName;


    public AttachmentAsset( String name, String format, String attachmentFileName ) {
        this.name = name;
        this.assetType = AssetType.getByName( format );
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }
}
