/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.enterprise.client.cdi.events;

import org.jboss.errai.ioc.client.api.AfterInitialization;

/**
 * An event that is fired locally on the client after the ErraiBus has completed
 * federation with the server. All remote (server-side) CDI services are known
 * before this event is fired.
 * <p>
 * <i>Usage note:</i> methods annotated with {@link AfterInitialization} are
 * also called after remote service discovery, and these methods are often an
 * easier way of arranging for client-side startup code to execute.
 *
 * @author Mike Brock .
 */
public class BusReadyEvent {}
