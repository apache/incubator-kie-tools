/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.util;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDef;

/**
 * Helper class for the serialization/deserialization of data source definitions into json format.
 */
public class DataSourceDefSerializer {

    public static DataSourceDef deserialize( final String dataSourceDefString ) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( dataSourceDefString, DataSourceDef.class );
    }

    public static DataSourceDef deserialize( final Reader reader ) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson( reader, DataSourceDef.class );
    }

    public static String serialize( final DataSourceDef dataSourceDef ) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson( dataSourceDef );
    }
}
