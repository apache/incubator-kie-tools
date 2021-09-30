/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client;

import java.util.List;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;

/**
 * Interface addressed to capture events coming from a Displayer instance.
 */
public interface DisplayerListener {

    /**
     * Invoked just before the data lookup operation has been started,
     *
     * @param displayer The Displayer instance.
     */
    void onDataLookup(Displayer displayer);

    /**
     * Invoked right after the data lookup finishes and the data set is available,
     *
     * @param displayer The Displayer instance.
     */
    void onDataLoaded(Displayer displayer);

    /**
     * Invoked just after the displayer has been drawn.
     *
     * @param displayer The Displayer instance.
     */
    void onDraw(Displayer displayer);

    /**
     * Invoked just after the displayer has been redrawn.
     *
     * @param displayer The Displayer instance.
     */
    void onRedraw(Displayer displayer);

    /**
     * Invoked just after the displayer has been closed.
     *
     * @param displayer The Displayer instance.
     */
    void onClose(Displayer displayer);

    /**
     * Invoked when a group interval selection filter request is executed on a given Displayer instance.
     *
     * @param displayer The Displayer instance where the interval selection event comes from.
     * @param groupOp The group interval selection operation.
     */
    void onFilterEnabled(Displayer displayer, DataSetGroup groupOp);

    /**
     * Invoked when a filter request is executed on a given Displayer instance.
     *
     * @param displayer The Displayer instance where the filter request event comes from.
     * @param filter The filter operation.
     */
    void onFilterEnabled(Displayer displayer, DataSetFilter filter);

    /**
     * Invoked when an update filter request is executed on an already filtered Displayer instance.
     *
     * @param displayer The Displayer instance where the filter request event comes from.
     * @param oldFilter The old filter operation.
     * @param newFilter The new filter operation.
     */
    void onFilterUpdate(Displayer displayer, DataSetFilter oldFilter, DataSetFilter newFilter);

    /**
     * Invoked when a group interval reset request is executed on a given Displayer instance.
     *
     * @param displayer The Displayer instance where the interval selection event comes from.
     * @param groupOps The set of group interval selection operations reset.
     */
    void onFilterReset(Displayer displayer, List<DataSetGroup> groupOps);

    /**
     * Invoked when a filter reset request is executed on a given Displayer instance.
     *
     * @param displayer The Displayer instance where the filter event comes from.
     * @param filter The filter operation to reset.
     */
    void onFilterReset(Displayer displayer, DataSetFilter filter);

    /**
     * Invoked when some error occurs.
     * @param displayer The Displayer instance event comes from.
     * @param error The error instance.
     */
    void onError(final Displayer displayer, ClientRuntimeError error);
}