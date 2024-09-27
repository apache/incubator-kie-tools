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

package version

const (
	// Use the script /hack/ci/bump-version.sh to update these constants. DO NOT UPDATE THEM MANUALLY!

	// operatorVersion is the current BINARY version of the operator, not the image tag.
	operatorVersion = "0.0.0"
	// tagVersion is the images version tag.
	// For example, docker.io/apache/incubator-kie-sonataflow-operator:main
	//
	// This tag must reflect an existing tag in the registry. In development, must follow the git branch naming.
	// When released, this version should reflect the `major.minor` version in the registry.
	// For example, docker.io/apache/incubator-kie-sonataflow-operator:main -> 10.0
	tagVersion = "main"
	// Kogito images tag version. Used for data-index and jobs-service images.
	kogitoImagesTagVersion = "main"
	// OpenJDK image tag version
	openJDKImageTagVersion = "1.20"
)

// GetOpenJDKImageTagVersion gets the current OpenJDK image version.
func GetOpenJDKImageTagVersion() string {
	return openJDKImageTagVersion
}

// GetOperatorVersion gets the current binary version of the operator. Do not use it to compose image tags!
func GetOperatorVersion() string {
	return operatorVersion
}

// GetTagVersion gets the current tag version for the operator and platform images.
func GetTagVersion() string {
	return tagVersion
}

// GetKogitoImagesTagVersion gets the current kogito version for the upstream kogito images.
func GetKogitoImagesTagVersion() string {
	return kogitoImagesTagVersion
}
