/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
props = [:]

void add(String key, def value) {
    if (value) {
        props.put(key, value)
    }
}

void readFromUrl(String url) {
    if (url) {
        tempFile = sh(returnStdout: true, script: 'mktemp').trim()
        sh "wget ${url} -O ${tempFile}"
        readFromFile(tempFile)
    }
}

void readFromFile(String file) {
    props = readProperties file: file
    echo props.collect { entry ->  "${entry.key}=${entry.value}" }.join('\n')
}

void writeToFile(String file) {
    def propertiesStr = props.collect { entry ->  "${entry.key}=${entry.value}" }.join('\n')
    writeFile(text: propertiesStr, file: file)
}

boolean contains(String key) {
    return props[key]
}

String retrieve(String key) {
    return props[key]
}

return this
