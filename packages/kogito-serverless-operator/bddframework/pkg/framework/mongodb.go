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

package framework

import (
	"fmt"

	v1 "k8s.io/api/apps/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/operator"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/meta"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	mongodb "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/mongodb/v1"
)

const (
	membersSize = 1

	mongoDBVersion = "4.2.6"
)

// DeployMongoDBInstance deploys an instance of Mongo DB
func DeployMongoDBInstance(namespace string, instance *mongodb.MongoDBCommunity) error {
	GetLogger(namespace).Info("Creating MongoDB instance")

	if err := kubernetes.ResourceC(kubeClient).Create(instance); err != nil {
		return fmt.Errorf("Error while creating MongoDB: %v ", err)
	}

	return nil
}

// CreateMongoDBSecret creates a new secret for MongoDB instance
func CreateMongoDBSecret(namespace, name, password string) error {
	GetLogger(namespace).Info("Create MongoDB Secret", "secret", name)
	return kubernetes.ResourceC(kubeClient).Create(GetMongoDBSecret(namespace, name, password))
}

// GetMongoDBSecret returns a MongoDB secret structure
func GetMongoDBSecret(namespace, secretName, password string) *corev1.Secret {
	return &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      secretName,
			Namespace: namespace,
		},
		Type: corev1.SecretTypeOpaque,
		StringData: map[string]string{
			infrastructure.DefaultMongoDBPasswordSecretRef: password,
		},
	}
}

// MongoDBUserCred holds information to create a MongoDB user in MongoDB, secretName containing the password
type MongoDBUserCred struct {
	Name         string
	AuthDatabase string
	SecretName   string
	Databases    []string
}

// GetMongoDBStub returns the preconfigured MongoDB stub with set namespace, name and secretName
func GetMongoDBStub(openshift bool, namespace, name string, users []MongoDBUserCred) *mongodb.MongoDBCommunity {
	// Taken from https://github.com/mongodb/mongodb-kubernetes-operator/blob/v0.7.0/config/samples/mongodb.com_v1_mongodbcommunity_openshift_cr.yaml
	stub := &mongodb.MongoDBCommunity{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Spec: mongodb.MongoDBCommunitySpec{
			Members:  membersSize,
			Type:     mongodb.ReplicaSet,
			Version:  mongoDBVersion,
			Security: mongodb.Security{Authentication: mongodb.Authentication{Modes: []mongodb.AuthMode{"SCRAM"}}},
			StatefulSetConfiguration: mongodb.StatefulSetConfiguration{
				SpecWrapper: mongodb.StatefulSetSpecWrapper{
					Spec: v1.StatefulSetSpec{
						ServiceName: "example-openshift-mongodb-svc",
						Selector:    &metav1.LabelSelector{},
					},
				},
			},
		},
	}
	for _, user := range users {
		userStub := mongodb.MongoDBUser{
			Name: user.Name,
			PasswordSecretRef: mongodb.SecretKeyReference{
				Name: user.SecretName,
			},
			ScramCredentialsSecretName: user.Name,
		}
		if len(user.AuthDatabase) > 0 {
			userStub.DB = user.AuthDatabase
		} else {
			// Need to set default else the MongoDB deployment is failing ...
			userStub.DB = infrastructure.DefaultMongoDBAuthDatabase
		}
		for _, database := range user.Databases {
			roles := []mongodb.Role{
				{
					Name: "dbOwner",
					DB:   database,
				},
				{
					Name: "clusterAdmin",
					DB:   database,
				},
				{
					Name: "userAdminAnyDatabase",
					DB:   database,
				},
			}
			userStub.Roles = append(userStub.Roles, roles...)
		}
		stub.Spec.Users = append(stub.Spec.Users, userStub)
	}

	return stub
}

// IsMongoDBAvailable checks if MongoDB CRD is available in the cluster
func IsMongoDBAvailable(namespace string) bool {
	context := operator.Context{
		Client: kubeClient,
		Log:    GetLogger(namespace),
		Scheme: meta.GetRegisteredSchema(),
	}
	return infrastructure.NewMongoDBHandler(context).IsMongoDBAvailable()
}

// SetMongoDBReplicas sets the number of replicas for an MongoDB instance
func SetMongoDBReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Info("Set MongoDB props for", "name", name, "replica number", nbPods)
	statefulset, err := GetStatefulSet(namespace, name)
	if err != nil {
		return err
	} else if statefulset == nil {
		return fmt.Errorf("No Mongodb StatefulSet found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	statefulset.Spec.Replicas = &replicas
	return UpdateObject(statefulset)
}
