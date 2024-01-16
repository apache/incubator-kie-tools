/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.enterprise.event;

/**
 * A CDI event with payload of type {@link Startup} and qualifier {@link
 * jakarta.enterprise.inject.Any} is <i>synchronously</i> fired by CDI container during application
 * initialization. Applications must never manually fire any events with {@link Startup} as payload.
 *
 * <p>Implementations have to fire this event after the event with qualifier
 * {@code @Initialized(ApplicationScope.class)} but before processing requests.
 *
 * <p>This event can be observed by integrators and libraries to perform any kind of early
 * initialization as well as by users as a reliable entry point for when the CDI container is ready.
 *
 * <p>Observers are encouraged to specify {@code @Priority} to determine ordering with lower
 * priority numbers being recommended for platform/framework/library integration and higher numbers
 * for user applications. See also {@link jakarta.interceptor.Interceptor.Priority}
 */
public class Startup {}
