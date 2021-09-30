/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.def;

import org.dashbuilder.dataset.impl.BeanDataSetDefBuilderImpl;
import org.dashbuilder.dataset.impl.CSVDataSetDefBuilderImpl;
import org.dashbuilder.dataset.impl.KafkaDataSetDefBuilderImpl;
import org.dashbuilder.dataset.impl.PrometheusDataSetDefBuilderImpl;
import org.dashbuilder.dataset.impl.SQLDataSetDefBuilderImpl;
import org.dashbuilder.dataset.impl.StaticDataSetDefBuilderImpl;

/**
 * Factory class for building DataSetDef instances.
 */
public final class DataSetDefFactory {

    public static DataSetDefBuilder<StaticDataSetDefBuilderImpl> newStaticDataSetDef() {
        return new StaticDataSetDefBuilderImpl();
    }

    public static CSVDataSetDefBuilderImpl newCSVDataSetDef() {
        return new CSVDataSetDefBuilderImpl();
    }

    public static SQLDataSetDefBuilderImpl newSQLDataSetDef() {
        return new SQLDataSetDefBuilderImpl();
    }

    public static BeanDataSetDefBuilderImpl newBeanDataSetDef() {
        return new BeanDataSetDefBuilderImpl();
    }

    public static PrometheusDataSetDefBuilderImpl newPrometheusDataSetDef() {
        return new PrometheusDataSetDefBuilderImpl();
    }

    public static KafkaDataSetDefBuilderImpl newKafkaDataSetDef() {
        return new KafkaDataSetDefBuilderImpl();
    }
}