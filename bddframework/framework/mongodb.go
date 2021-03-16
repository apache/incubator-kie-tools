// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package framework

import (
	"fmt"

	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/logger"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/meta"

	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	mongodb "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	membersSize = 1

	mongoDBVersion = "4.4.1"
)

// DeployMongoDBInstance deploys an instance of Mongo DB
func DeployMongoDBInstance(namespace string, instance *mongodb.MongoDB) error {
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
func GetMongoDBStub(openshift bool, namespace, name string, users []MongoDBUserCred) *mongodb.MongoDB {
	// Default capacity is 10G, default to 1G
	capacity, _ := resource.ParseQuantity("1G")

	// Taken from https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/deploy/crds/mongodb.com_v1_mongodb_cr.yaml
	stub := &mongodb.MongoDB{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Spec: mongodb.MongoDBSpec{
			Members:  membersSize,
			Type:     mongodb.ReplicaSet,
			Version:  mongoDBVersion,
			Security: mongodb.Security{Authentication: mongodb.Authentication{Modes: []mongodb.AuthMode{"SCRAM"}}},
			StatefulSetConfiguration: mongodb.StatefulSetConfiguration{
				Spec: v1.StatefulSetSpec{
					VolumeClaimTemplates: []corev1.PersistentVolumeClaim{
						{
							ObjectMeta: metav1.ObjectMeta{
								Name: "data-volume",
							},
							Spec: corev1.PersistentVolumeClaimSpec{
								Resources: corev1.ResourceRequirements{
									Requests: corev1.ResourceList{
										corev1.ResourceStorage: capacity,
									},
								},
							},
						},
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

	if openshift {
		// OCP Specificies https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/deploy/crds/mongodb.com_v1_mongodb_openshift_cr.yaml
		GetLogger(namespace).Debug("Setup MANAGED_SECURITY_CONTEXT env in MongoDB entity for Openshift")
		stub.Spec.StatefulSetConfiguration.Spec.Template = corev1.PodTemplateSpec{
			Spec: corev1.PodSpec{
				Containers: []corev1.Container{
					{
						Name: "mongodb-agent",
						Env: []corev1.EnvVar{{
							Name:  "MANAGED_SECURITY_CONTEXT",
							Value: "true",
						},
						},
					},
					{
						Name: "mongod",
						Env: []corev1.EnvVar{{
							Name:  "MANAGED_SECURITY_CONTEXT",
							Value: "true",
						},
						},
					},
				},
			},
		}
	}

	return stub
}

// IsMongoDBAvailable checks if MongoDB CRD is available in the cluster
func IsMongoDBAvailable(namespace string) bool {
	context := &operator.Context{
		Client: kubeClient,
		Log:    logger.GetLogger(namespace),
		Scheme: meta.GetRegisteredSchema(),
	}
	return infrastructure.NewMongoDBHandler(context).IsMongoDBAvailable()
}
