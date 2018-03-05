/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.api;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;

/**
 * <p>
 * A result to a {@link ProjectAssetsQuery} that can either be a (possibly size 0)
 * list of assets, or an empty result if the project is not indexed or does not exist.
 */
public class AssetQueryResult {

    private final List<AssetInfo> assets;
    private final ResultType resultType;

    public static AssetQueryResult nonexistent() {
        return new AssetQueryResult(null, ResultType.DoesNotExist);
    }

    public static AssetQueryResult unindexed() {
        return new AssetQueryResult(null, ResultType.Unindexed);
    }

    public static AssetQueryResult normal(List<AssetInfo> assets) {
        checkNotNull("assets", assets);
        return new AssetQueryResult(assets, ResultType.Normal);
    }

    /**
     * @param assets Should be null when {@code resultType} is not {@link ResultType#Normal}.
     * @param resultType Must not be null.
     * @see #nonexistent()
     * @see #unindexed()
     * @see #normal(List)
     */
    public AssetQueryResult(@MapsTo("assets") final List<AssetInfo> assets, @MapsTo("resultType") final ResultType resultType) {
        this.assets = assets;
        this.resultType = resultType;
    }

    /**
     * @return An empty {@link Optional} if the query was against a project not yet indexed or does not exist, or else a present list of assets.
     */
    public Optional<List<AssetInfo>> getAssetInfos() {
        return Optional.ofNullable(assets);
    }

    public ResultType getResultType() {
        return resultType;
    }

    public static enum ResultType {
        /**
         * Indicates a typical result to a query for an existing, indexed project.
         */
        Normal,
        /**
         * Indicates a result for an exisiting but unindexed project.
         */
        Unindexed,
        /**
         * Indicates a result for a project that does not exist.
         */
        DoesNotExist;
    }

}
