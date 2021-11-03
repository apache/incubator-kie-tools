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
package org.dashbuilder.dataset.client;

import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.AbstractDataSetCore;
import org.dashbuilder.dataset.client.engine.ClientChronometer;
import org.dashbuilder.dataset.client.engine.ClientDateFormatter;
import org.dashbuilder.dataset.client.engine.ClientDateFormatterImpl;
import org.dashbuilder.dataset.client.engine.ClientIntervalBuilderDynamicDate;
import org.dashbuilder.dataset.client.engine.ClientIntervalBuilderLocator;
import org.dashbuilder.dataset.client.uuid.ClientUUIDGenerator;
import org.dashbuilder.dataset.engine.Chronometer;
import org.dashbuilder.dataset.engine.group.IntervalBuilderLocator;
import org.dashbuilder.dataset.uuid.UUIDGenerator;

public class ClientDataSetCore extends AbstractDataSetCore {

    private static ClientDataSetCore _instance = null;

    public static ClientDataSetCore get() {
        if (_instance == null) {
            _instance = new ClientDataSetCore();
        }
        return _instance;
    }

    private ClientDataSetManager clientDataSetManager;
    private ClientIntervalBuilderLocator clientIntervalBuilderLocator;
    private ClientChronometer clientChronometer;
    private ClientUUIDGenerator clientUUIDGenerator;
    private ClientIntervalBuilderDynamicDate clientIntervalBuilderDynamicDate;
    private ClientDateFormatter clientDateFormatter;

    // Getters

    public ClientDataSetManager getClientDataSetManager() {
        if (clientDataSetManager == null) {
            clientDataSetManager = new ClientDataSetManager();
        }
        return clientDataSetManager;
    }

    public ClientIntervalBuilderLocator getClientIntervalBuilderLocator() {
        if (clientIntervalBuilderLocator == null) {
            clientIntervalBuilderLocator = new ClientIntervalBuilderLocator(
                    checkNotNull(getIntervalBuilderDynamicLabel(), "IntervalBuilderDynamicLabel"),
                    checkNotNull(getClientIntervalBuilderDynamicDate(), "ClientIntervalBuilderDynamicDate"),
                    checkNotNull(getIntervalBuilderFixedDate(), "IntervalBuilderFixedDate"));
        }
        return clientIntervalBuilderLocator;
    }

    public ClientIntervalBuilderDynamicDate getClientIntervalBuilderDynamicDate() {
        if (clientIntervalBuilderDynamicDate == null) {
            clientIntervalBuilderDynamicDate = new ClientIntervalBuilderDynamicDate(
                    checkNotNull(getClientDateFormatter(), "ClientDateFormatter")
            );
        }
        return clientIntervalBuilderDynamicDate;
    }

    public ClientDateFormatter getClientDateFormatter() {
        if (clientDateFormatter == null) {
            clientDateFormatter = new ClientDateFormatterImpl();
        }
        return clientDateFormatter;
    }

    public ClientChronometer getClientChronometer() {
        if (clientChronometer == null) {
            clientChronometer = new ClientChronometer();
        }
        return clientChronometer;
    }

    public ClientUUIDGenerator getClientUUIDGenerator() {
        if (clientUUIDGenerator == null) {
            clientUUIDGenerator = new ClientUUIDGenerator();
        }
        return clientUUIDGenerator;
    }

    // Setters

    public void setClientDataSetManager(ClientDataSetManager clientDataSetManager) {
        this.clientDataSetManager = clientDataSetManager;
    }

    public void setClientIntervalBuilderLocator(ClientIntervalBuilderLocator clientIntervalBuilderLocator) {
        this.clientIntervalBuilderLocator = clientIntervalBuilderLocator;
    }

    public void setClientIntervalBuilderDynamicDate(ClientIntervalBuilderDynamicDate clientIntervalBuilderDynamicDate) {
        this.clientIntervalBuilderDynamicDate = clientIntervalBuilderDynamicDate;
    }

    public void setClientDateFormatter(ClientDateFormatter clientDateFormatter) {
        this.clientDateFormatter = clientDateFormatter;
    }

    public void setClientChronometer(ClientChronometer clientChronometer) {
        this.clientChronometer = clientChronometer;
    }

    public void setClientUUIDGenerator(ClientUUIDGenerator clientUUIDGenerator) {
        this.clientUUIDGenerator = clientUUIDGenerator;
    }

    // Factory methods

    @Override
    public DataSetManager newDataSetManager() {
        return getClientDataSetManager();
    }

    @Override
    public IntervalBuilderLocator newIntervalBuilderLocator() {
        return getClientIntervalBuilderLocator();
    }

    @Override
    public Chronometer newChronometer() {
        return getClientChronometer();
    }

    @Override
    public UUIDGenerator newUuidGenerator() {
        return getClientUUIDGenerator();
    }
}

