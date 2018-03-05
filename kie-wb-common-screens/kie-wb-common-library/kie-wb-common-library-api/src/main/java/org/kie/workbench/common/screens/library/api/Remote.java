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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Qualifier;


/**
 * <p>
 * A qualifier indicating an event originates from the server and not the client.
 *
 * <p>
 * Why would you want this? If you want to observe a server-side event direclty form a client-side view, you can run into timing issues.
 * Particularly, because the Errai Bus must subscribe to the event channel when the view is created, it's possible to miss events for a short time
 * after the view is instantiated.
 *
 * <p>A useful pattern to avoid this is to observe an event in an {@link ApplicationScoped} bean with the {@link Remote} qualifier,
 * and refire the event from the client with the {@link Routed} qualifier.
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
public @interface Remote {

}
