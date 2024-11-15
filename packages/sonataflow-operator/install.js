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

const fs = require("fs");
const path = require("path");
const { env } = require("./env");

function getAllYamlFiles(dir) {
    let results = [];
    const list = fs.readdirSync(dir);

    list.forEach((file) => {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);

        if (stat && stat.isDirectory()) {
            // Recurse into subdirectory
            results = results.concat(getAllYamlFiles(fullPath));
        } else if (file.endsWith(".yaml")) {
            // Add .yaml file to results
            results.push(fullPath);
        }
    });

    return results;
}

const baseDir = path.resolve(__dirname, ".");

const yamlFiles = getAllYamlFiles(baseDir);

yamlFiles.forEach((filePath) => {
    const updatedContent = fs
        .readFileSync(filePath, "utf-8")
        .replace(
            /org\.kie:kie-addons-quarkus-persistence-jdbc:\S*/,
            `org.kie:kie-addons-quarkus-persistence-jdbc:${env.versions.kogito}`
        )
        .replace(
            /org\.kie\.kogito:kogito-addons-quarkus-jobs-knative-eventing:\S*/,
            `org.kie.kogito:kogito-addons-quarkus-jobs-knative-eventing:${env.versions.kogito}`
        )
        .replace(
            /- groupId: io\.quarkus\s+artifactId: quarkus-jdbc-postgresql\s+version: \S+/g,
            `- groupId: io.quarkus\n    artifactId: quarkus-jdbc-postgresql\n    version: ${env.versions.quarkus}`
        )
        .replace(
            /- groupId: io\.quarkus\s+artifactId: quarkus-agroal\s+version: \S+/g,
            `- groupId: io.quarkus\n    artifactId: quarkus-agroal\n    version: ${env.versions.quarkus}`
        )
        .replace(
            /- groupId: org\.kie\s+artifactId: kie-addons-quarkus-persistence-jdbc\s+version: \S+/g,
            `- groupId: org.kie\n    artifactId: kie-addons-quarkus-persistence-jdbc\n    version: ${env.versions.kogito}`
        );

    fs.writeFileSync(filePath, updatedContent);
    console.log(`Updated: ${filePath}`);
});

