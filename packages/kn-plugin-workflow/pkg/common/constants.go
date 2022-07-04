/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

// can be overriten by env
var QUARKUS_VERSION = "2.10.0.Final"

const (
	QUARKUS_DEFAULT_EXTENSIONS = "kogito-quarkus-serverless-workflow,kogito-addons-quarkus-knative-eventing,resteasy-reactive-jackson,quarkus-kubernetes"
	JAVA_VERSION               = 11
	MAVEN_MAJOR_VERSION        = 3
	MAVEN_MINOR_VERSION        = 8
	DEFAULT_REGISTRY           = "quay.io"
	DEFAULT_TAG                = "latest"
)
