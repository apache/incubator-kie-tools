/*
 * Copyright 2014 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This code duplicates the classes in the 
 * org.kie.services.client.serialization.jaxb.json package
 * in the kie-services-jaxb project.
 * </p>
 * There is no easy way (at the moment) to share an artifact between
 * the drools-wb-rest project and the kie-remote/kie-services-* projects. 
 * </p>
 * What we basically need to split drools-wb-rest into 2 projects: <ul>
 * <li>1 project with the knowledge store services</li>
 * <li>1 project with classes that determine REST service behaviour, like the classes in this package</li>
 * </ul>
 * </p>
 * (guvnor/drools-wb) drools-wb-rest is built before the (droolsjbpm-integration) kie-remote projects: 
 * the shared classes thus need to be in the drools-wb or guvnor project.
 */
package org.drools.workbench.common.services.rest.json;