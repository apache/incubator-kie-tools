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

package org.drools.guvnor.shared.common.vo.assets.plaintext;

import org.drools.guvnor.shared.common.vo.asset.AbstractAsset;
import org.drools.guvnor.shared.common.vo.asset.AssetData;
import org.drools.guvnor.shared.common.vo.asset.AssetMetaData;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * This is the guts of a plain text (advanced) rule.
 */
@Portable
public class RuleContentText extends AbstractAsset {

    private String content;

    public RuleContentText() {
    }

    public RuleContentText(String content,
                           AssetData assetData,
                           AssetMetaData assetMetaData) {
        super( assetData,
               assetMetaData );
        this.content = content;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *            the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

}
