/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.lang.reflect.Field;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BackendBindableAdapterFunctions implements BindableAdapterFunctions {

    private static final Logger LOG = LoggerFactory.getLogger(BackendBindableAdapterFunctions.class);

    @Override
    public Object getValue(Object property, String fieldName) {
        return getFieldValue(property, fieldName);
    }

    public static Object getFieldValue(Object property, String fieldName) {
        try {
            return ReflectionAdapterUtils.getValue(property, fieldName);
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining value for field " + fieldName);
        }
        return null;
    }

    @Override
    public void setValue(Object property, String fieldName, Object value) {
        setFieldValue(property, fieldName, value);
    }

    public static void setFieldValue(Object property, String fieldName, Object value) {
        Field field = ReflectionAdapterUtils.getField(property, fieldName);
        if (null != field) {
            try {
                field.setAccessible(true);
                field.set(property, value);
            } catch (Exception e) {
                LOG.error("Error setting value for field " + fieldName);
            }
        }
    }
}
