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

///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//REPOS droolsAndKogito=file://${droolsAndKogitoLocalM2Repo},mavencentral
//DEPS org.kie:drools-build-parent:${kogito-runtime.version}@pom
//DEPS ch.qos.logback:logback-classic:1.2.13
//DEPS info.picocli:picocli:4.7.5
//DEPS org.slf4j:slf4j-simple:2.0.12
//DEPS org.kie:kie-api:${kogito-runtime.version}
//DEPS org.kie:kie-internal:${kogito-runtime.version}
//DEPS org.kie:kie-dmn-api:${kogito-runtime.version}
//DEPS org.kie:kie-dmn-core:${kogito-runtime.version}
//DEPS org.kie:kie-dmn-model:${kogito-runtime.version}
//DEPS org.kie:kie-dmn-validation:${kogito-runtime.version}

package jbang;

import java.util.concurrent.Callable;

/* Parent Script class for all JBang script used in this package
 * This class serves to pre-fetch all dependencies declared in this class (in the above DEPS scripts).
 * To pre-fetch the dependency, just call this jbang script with no args and the required dependency's versions 
 * (eg. jbang -Dkogito-runtime.version=x.y.z ./src/DmnMarshallerBackendCompatibilityTesterScript.java)
 * To make it work, all dependencies used by all JBang scripts (aka this class implementations), MUST be declared here.
 */
abstract class DmnMarshallerBackendCompatibilityTesterScript implements Callable<Integer> {

    public static void main(String... args) {
        System.exit(0);
    }

}
