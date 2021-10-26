// Copyright 2021 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package test

import (
	mongodb "github.com/kiegroup/kogito-operator/core/infrastructure/mongodb/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// CreateFakeMongoDB ...
func CreateFakeMongoDB(namespace string) *mongodb.MongoDBCommunity {
	return &mongodb.MongoDBCommunity{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "kogito-mongodb",
			Namespace: namespace,
		},
		Spec: mongodb.MongoDBCommunitySpec{
			Users: []mongodb.MongoDBUser{
				{
					DB:   "admin",
					Name: "mongodbUser",
					PasswordSecretRef: mongodb.SecretKeyReference{
						Name: "mongodb-developer-secret",
						Key:  "password",
					},
				},
			},
		},
		Status: mongodb.MongoDBCommunityStatus{
			Phase:    mongodb.Running,
			MongoURI: "http://mongodb-host:27017",
		},
	}
}

// CreateFakeMongoDBSecret ...
func CreateFakeMongoDBSecret(namespace string) *v1.Secret {
	return &v1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "mongodb-developer-secret",
			Namespace: namespace,
		},
		Data: map[string][]byte{
			"password": []byte("passwordToFind"),
		},
	}
}
