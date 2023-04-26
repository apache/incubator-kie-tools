/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows customization of field (or JavaBean property) name.This name is used either in
 * serialization or in deserialization.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @YamlProperty} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a JavaBean property
 *   <li>field
 * </ul>
 *
 * @since JSON Binding 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface YamlProperty {

  /**
   * Customized name of the field (or JavaBean property).
   *
   * @return Customized property name.
   */
  String value() default "";
}
