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
//REPOS mavencentral,apache=https://repository.apache.org/content/groups/public/
//DEPS ch.qos.logback:logback-classic:1.2.13
//DEPS info.picocli:picocli:4.7.5
//DEPS org.slf4j:slf4j-simple:2.0.12

package jbang;

import java.util.concurrent.Callable;

abstract class DmnParserJBangScript implements Callable<Integer> {

}
