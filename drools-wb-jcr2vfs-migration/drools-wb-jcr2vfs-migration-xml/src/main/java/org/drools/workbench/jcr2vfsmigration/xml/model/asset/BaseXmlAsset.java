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

import java.util.Date;

public abstract class BaseXmlAsset implements XmlAsset {

    protected String name;
    protected AssetType assetType;
    protected String lastContributor;
    protected String checkinComment;
    protected Date lastModified;
    protected XmlAssets assetHistory;

    protected BaseXmlAsset() {}

    protected BaseXmlAsset( String name, String format, String lastContributor, String checkinComment, Date lastModified ) {
        this.name = name;
        this.assetType = AssetType.getByType( format );
        this.lastContributor = lastContributor;
        this.checkinComment = checkinComment;
        this.lastModified = lastModified;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AssetType getAssetType() {
        return assetType;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public XmlAssets getAssetHistory() {
        return assetHistory;
    }

    @Override
    public void setAssetHistory( XmlAssets assetHistory ) {
        this.assetHistory = assetHistory != null ? assetHistory : new XmlAssets();
    }
}
