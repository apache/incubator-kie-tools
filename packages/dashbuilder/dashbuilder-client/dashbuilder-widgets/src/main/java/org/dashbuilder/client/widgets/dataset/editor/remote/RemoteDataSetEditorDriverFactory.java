/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.widgets.dataset.editor.remote;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.dashbuilder.client.widgets.dataset.editor.driver.RemoteDataSetDefAttributesDriver;
import org.dashbuilder.client.widgets.dataset.editor.driver.RemoteDataSetDefDriver;

import com.google.gwt.core.client.GWT;


@ApplicationScoped
public class RemoteDataSetEditorDriverFactory {

    final RemoteDataSetDefAttributesDriver remoteDataSetDefAttributesDriver = GWT.create(RemoteDataSetDefAttributesDriver.class);

    final RemoteDataSetDefDriver remoteDataSetDefDriver = GWT.create(RemoteDataSetDefDriver.class);

    @Produces
    public RemoteDataSetDefDriver remoteDataSetDefDriver() {
        return remoteDataSetDefDriver;
    }

    @Produces
    public RemoteDataSetDefAttributesDriver remoteDataSetDefAttributesDriver() {
        return remoteDataSetDefAttributesDriver;
    }

}