// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"testing"

	mongodb "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func TestRetrieveMongoDBCredentialsFromInstance(t *testing.T) {
	passwordToFind := "passwordToFind"
	authDB := infrastructure.DefaultMongoDBAuthDatabase
	db := "database"
	username := "developer"
	type args struct {
		username string
		key      string
		authDB   string
		database string
	}
	type result struct {
		errorExpected bool
		username      string
		password      string
		authDB        string
		database      string
	}
	tests := []struct {
		name   string
		args   args
		result result
	}{
		{"Default", args{username: username, database: db}, result{username: username, password: passwordToFind, authDB: authDB, database: db}},
		{"No user", args{database: db}, result{username: "", password: "", authDB: authDB, database: "", errorExpected: true}},
		{"No database", args{username: username}, result{username: "", password: "", authDB: authDB, database: "", errorExpected: true}},
		{"Different key", args{username: username, database: db, key: "testKey"}, result{username: username, password: passwordToFind, authDB: authDB, database: db}},
		{"Different Auth DB", args{username: username, database: db, authDB: "authDBToFind"}, result{username: username, password: passwordToFind, authDB: "authDBToFind", database: db}},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			mongoDBInstance := &mongodb.MongoDB{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "test",
					Namespace: t.Name(),
				},
				Spec: mongodb.MongoDBSpec{},
			}

			secretKey := infrastructure.DefaultMongoDBPasswordSecretRef
			if len(tt.args.key) > 0 {
				secretKey = tt.args.key
			}
			secret := &corev1.Secret{
				ObjectMeta: metav1.ObjectMeta{
					Name:      "mongodb-developer-secret",
					Namespace: t.Name(),
				},
				Data: map[string][]byte{
					secretKey: []byte(passwordToFind),
				},
			}

			if len(tt.args.username) > 0 {
				mongoDBInstance.Spec.Users = append(mongoDBInstance.Spec.Users, *createUser(tt.args.username, authDB, tt.args.database, tt.args.key))
				if len(tt.args.authDB) > 0 {
					mongoDBInstance.Spec.Users = append(mongoDBInstance.Spec.Users, *createUser(tt.args.username, tt.args.authDB, tt.args.database, tt.args.key))
				}
			}
			cli := test.NewFakeClientBuilder().AddK8sObjects(mongoDBInstance, secret).Build()
			reconciler := mongoDBInfraReconciler{
				infraContext: infraContext{
					Context: &operator.Context{
						Client: cli,
						Log:    test.TestLogger,
						Scheme: meta.GetRegisteredSchema(),
					},
				},
			}

			kogitoInfra := &v1beta1.KogitoInfra{
				Spec: v1beta1.KogitoInfraSpec{
					Resource: v1beta1.Resource{
						Name:       "name",
						Namespace:  "namespace",
						Kind:       "kind",
						APIVersion: "APIVersion",
					},
					InfraProperties: map[string]string{
						infraPropertiesUserKey:         tt.args.username,
						infraPropertiesAuthDatabaseKey: tt.args.authDB,
						infraPropertiesDatabaseKey:     tt.args.database,
					},
				},
			}
			creds, err := reconciler.retrieveMongoDBCredentialsFromInstance(cli, kogitoInfra, mongoDBInstance)
			if tt.result.errorExpected {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
				assert.Equal(t, tt.result.username, creds.Username)
				assert.Equal(t, tt.result.password, creds.Password)
				assert.Equal(t, tt.result.database, creds.Database)
				assert.Equal(t, tt.result.authDB, creds.AuthDatabase)
			}
		})
	}
}

func createUser(username, authDB, database, secretKey string) *mongodb.MongoDBUser {
	user := &mongodb.MongoDBUser{
		Name: username,
		DB:   authDB,
		PasswordSecretRef: mongodb.SecretKeyReference{
			Name: "mongodb-developer-secret",
		},
	}
	user.Roles = append(user.Roles, mongodb.Role{DB: "defaultDB"})
	if len(database) > 0 {
		user.Roles = append(user.Roles, mongodb.Role{DB: database})
	}

	if len(secretKey) > 0 {
		user.PasswordSecretRef.Key = secretKey
	}
	return user
}
