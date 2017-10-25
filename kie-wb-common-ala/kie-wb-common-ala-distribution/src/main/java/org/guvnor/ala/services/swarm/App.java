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

package org.guvnor.ala.services.swarm;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/*
 * This class represent the entry point for the WildflySwarm Guvnor ALA Application.
 * You can start this self contained (fat) JAR by running: 
 * java -jar guvnor-ala-distribution-swarm.jar
 * After that you can interact with the services at: http://<host>:<port>/api
 */
@ApplicationPath("/api")
public class App extends Application {

}
