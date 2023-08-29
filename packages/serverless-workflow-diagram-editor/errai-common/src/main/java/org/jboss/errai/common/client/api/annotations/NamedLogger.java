/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.common.client.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.slf4j.Logger;

/**
 * When injecting a {@link Logger}, this annotation can be used to specify a
 * name. If this annotation is used but no value is given, the root logger will
 * be provided.
 * 
 * Example usage:
 * 
 * <pre>
 * // Gets root logger
 * {@literal @Inject @NamedLogger} Logger logger;
 * 
 * // Gets logger with name 'LoggerName'
 * {@literal @Inject @NamedLogger("LoggerName")} Logger logger;
 * </pre>
 * 
 * @author mbarkley <mbarkley@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NamedLogger {
  String value() default org.slf4j.Logger.ROOT_LOGGER_NAME;
}
