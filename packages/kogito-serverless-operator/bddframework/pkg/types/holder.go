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

package types

import (
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
)

// KogitoServiceHolder Helper structure holding informations which are not available in KogitoService
type KogitoServiceHolder struct {
	api.KogitoService

	DatabaseType string
}

// KogitoBuildHolder Helper structure holding informations for Kogito build
type KogitoBuildHolder struct {
	*KogitoServiceHolder

	// Specifies folder with prebuilt Kogito binaries to be uploaded to KogitoBuild
	BuiltBinaryFolder string
}
