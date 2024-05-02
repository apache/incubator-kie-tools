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

package config

// ImageType represents the image base image name
type ImageType string

const (
	// DataIndexImageType ...
	DataIndexImageType ImageType = "data-index"
	// ExplainabilityImageType ...
	ExplainabilityImageType ImageType = "explainability"
	// JobServiceImageType ...
	JobServiceImageType ImageType = "jobs-service"
	// ManagementConsoleImageType ...
	ManagementConsoleImageType ImageType = "mgmt-console"
	// TaskConsoleImageType ...
	TaskConsoleImageType ImageType = "task-console"
	// TrustyImageType ...
	TrustyImageType ImageType = "trusty"
	// TrustyUIImageType ...
	TrustyUIImageType ImageType = "trusty-ui"
)

// ImagePersistenceType represents the persistence type for the base image
type ImagePersistenceType string

const (
	// EphemeralPersistenceType ...
	EphemeralPersistenceType ImagePersistenceType = "ephemeral"
	// InfinispanPersistenceType ...
	InfinispanPersistenceType ImagePersistenceType = "infinispan"
	// MongoDBPersistenceType ...
	MongoDBPersistenceType ImagePersistenceType = "mongodb"
	// PosgresqlPersistenceType ...
	PosgresqlPersistenceType ImagePersistenceType = "posgresql"
	// RedisPersistenceType ...
	RedisPersistenceType ImagePersistenceType = "redis"
)

var (
	imageTypePersistenceMapping map[ImageType][]ImagePersistenceType = map[ImageType][]ImagePersistenceType{
		DataIndexImageType:         {EphemeralPersistenceType, InfinispanPersistenceType, MongoDBPersistenceType, PosgresqlPersistenceType},
		ExplainabilityImageType:    {EphemeralPersistenceType},
		JobServiceImageType:        {EphemeralPersistenceType, InfinispanPersistenceType, MongoDBPersistenceType, PosgresqlPersistenceType},
		ManagementConsoleImageType: {EphemeralPersistenceType},
		TaskConsoleImageType:       {EphemeralPersistenceType},
		TrustyImageType:            {InfinispanPersistenceType, RedisPersistenceType},
		TrustyUIImageType:          {EphemeralPersistenceType},
	}
)

type imageTags struct {
	tags map[ImageType]map[ImagePersistenceType]*string
}

func (imgTags *imageTags) GetImageTagPointerFromPersistenceType(imageType ImageType, persistenceType ImagePersistenceType) *string {
	if len(imgTags.tags) <= 0 {
		imgTags.tags = make(map[ImageType]map[ImagePersistenceType]*string)
	}
	if len(imgTags.tags[imageType]) <= 0 {
		imgTags.tags[imageType] = make(map[ImagePersistenceType]*string)
	}
	if imgTags.tags[imageType][persistenceType] == nil {
		tag := ""
		imgTags.tags[imageType][persistenceType] = &tag
	}

	return imgTags.tags[imageType][persistenceType]
}
