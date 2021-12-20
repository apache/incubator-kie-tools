/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.lifecycle.api;

/**
 * An event representing the end of an IOC bean instances lifecycle.
 * 
 * This event is special in that, if it is successful (i.e. no listeners
 * {@linkplain LifecycleEvent#veto() veto} it) then all references to
 * {@link LifecycleListener LifecycleListeners} for this instance will released.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface Destruction<T> extends LifecycleEvent<T> {

}
