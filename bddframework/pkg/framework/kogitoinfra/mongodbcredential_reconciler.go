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

package kogitoinfra

import (
	"fmt"
	"github.com/RHsyseng/operator-utils/pkg/resource"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	mongodb "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	v12 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"
)

const (
	mongoDBSecretName = "kogito-mongodb-%s-credential"
)

type mongoDBCredentialReconciler struct {
	infraContext
	mongoDBInstance *mongodb.MongoDB
	runtime         api.RuntimeType
	secretHandler   infrastructure.SecretHandler
}

func newMongoDBCredentialReconciler(infraContext infraContext, mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) Reconciler {
	return &mongoDBCredentialReconciler{
		infraContext:    infraContext,
		mongoDBInstance: mongoDBInstance,
		runtime:         runtime,
		secretHandler:   infrastructure.NewSecretHandler(infraContext.Context),
	}
}

func (i *mongoDBCredentialReconciler) Reconcile() (err error) {
	// Create Required resource
	requestedResources, err := i.createRequiredResources()
	if err != nil {
		return
	}

	// Get Deployed resource
	deployedResources, err := i.getDeployedResources()
	if err != nil {
		return
	}

	// Process Delta
	if err = i.processDelta(requestedResources, deployedResources); err != nil {
		return err
	}

	i.instance.GetStatus().AddSecretEnvFromReferences(i.getCredentialSecretName())
	return nil
}

func (i *mongoDBCredentialReconciler) createRequiredResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	credentials, err := i.retrieveMongoDBCredentialsFromInstance()
	if err != nil {
		return nil, err
	}
	secret := i.createCustomKogitoMongoDBSecret(credentials)
	if err := framework.SetOwner(i.infraContext.instance, i.infraContext.Scheme, secret); err != nil {
		return resources, err
	}
	resources[reflect.TypeOf(v12.Secret{})] = []resource.KubernetesResource{secret}
	return resources, nil
}

func (i *mongoDBCredentialReconciler) getDeployedResources() (map[reflect.Type][]resource.KubernetesResource, error) {
	resources := make(map[reflect.Type][]resource.KubernetesResource)
	// fetch owned image stream
	deployedSecret, err := i.secretHandler.FetchSecret(types.NamespacedName{Name: i.getCredentialSecretName(), Namespace: i.infraContext.instance.GetNamespace()})
	if err != nil {
		return nil, err
	}
	if deployedSecret != nil {
		resources[reflect.TypeOf(v12.Secret{})] = []resource.KubernetesResource{deployedSecret}
	}
	return resources, nil
}

func (i *mongoDBCredentialReconciler) processDelta(requestedResources map[reflect.Type][]resource.KubernetesResource, deployedResources map[reflect.Type][]resource.KubernetesResource) (err error) {
	comparator := i.secretHandler.GetComparator()
	deltaProcessor := infrastructure.NewDeltaProcessor(i.infraContext.Context)
	_, err = deltaProcessor.ProcessDelta(comparator, requestedResources, deployedResources)
	return err
}

// MongoDBCredential holds the credentials information of a user into an mongoDB server
type MongoDBCredential struct {
	AuthDatabase string `yaml:"auth-database"`
	Username     string `yaml:"username"`
	Password     string `yaml:"password"`
	Database     string `yaml:"database"`
}

// retrieveMongoDBCredentialsFromInstance retrieves the credentials of the MongoDB server deployed with the Kogito Operator
// based on the kogitoinfra given properties
func (i *mongoDBCredentialReconciler) retrieveMongoDBCredentialsFromInstance() (*MongoDBCredential, error) {
	creds := &MongoDBCredential{}
	if len(i.instance.GetSpec().GetInfraProperties()[infraPropertiesUserKey]) == 0 {
		return nil, errorForMissingResourceConfig(i.instance, infraPropertiesUserKey)
	} else if len(i.instance.GetSpec().GetInfraProperties()[infraPropertiesDatabaseKey]) == 0 {
		return nil, errorForMissingResourceConfig(i.instance, infraPropertiesDatabaseKey)
	}
	creds.Username = i.instance.GetSpec().GetInfraProperties()[infraPropertiesUserKey]
	creds.Database = i.instance.GetSpec().GetInfraProperties()[infraPropertiesDatabaseKey]
	creds.AuthDatabase = i.instance.GetSpec().GetInfraProperties()[infraPropertiesAuthDatabaseKey]
	if len(creds.AuthDatabase) == 0 {
		creds.AuthDatabase = infrastructure.DefaultMongoDBAuthDatabase
	}

	user := i.findMongoDBUserByUsernameAndAuthDatabase(i.mongoDBInstance, creds.Username, creds.AuthDatabase)
	if user == nil {
		return nil, errorForResourceConfigError(i.instance, fmt.Sprintf("No user found in MongoDB configuration for username %s and authentication database %s", creds.Username, creds.AuthDatabase))
	}
	i.Log.Debug("Found", "user", user.Name, "authDB", user.DB, "password ref", user.PasswordSecretRef)

	secret := &v12.Secret{ObjectMeta: metav1.ObjectMeta{Name: user.PasswordSecretRef.Name, Namespace: i.mongoDBInstance.Namespace}}
	if exists, err := kubernetes.ResourceC(i.Client).Fetch(secret); err != nil {
		return nil, err
	} else if !exists {
		return nil, errorForResourceNotFound("Secret", user.PasswordSecretRef.Name, i.instance.GetNamespace())
	} else {
		i.Log.Debug("Found MongoDB secret", "password ref", user.PasswordSecretRef.Name)
		passwordKey := infrastructure.DefaultMongoDBPasswordSecretRef
		if user.PasswordSecretRef.Key != "" {
			passwordKey = user.PasswordSecretRef.Key
		}
		creds.Password = string(secret.Data[passwordKey])
	}

	return creds, nil
}

// Setup authentication to MongoDB
// https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/docs/users.md
func (i *mongoDBCredentialReconciler) createCustomKogitoMongoDBSecret(credentials *MongoDBCredential) *v12.Secret {
	secret := &v12.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      i.getCredentialSecretName(),
			Namespace: i.instance.GetNamespace(),
		},
		Type: v12.SecretTypeOpaque,
		StringData: map[string]string{
			propertiesMongoDB[i.runtime][envVarMongoDBAuthDatabase]: credentials.AuthDatabase,
			propertiesMongoDB[i.runtime][envVarMongoDBUser]:         credentials.Username,
			propertiesMongoDB[i.runtime][envVarMongoDBPassword]:     credentials.Password,
			propertiesMongoDB[i.runtime][envVarMongoDBDatabase]:     credentials.Database,
		},
	}
	return secret
}

func (i *mongoDBCredentialReconciler) findMongoDBUserByUsernameAndAuthDatabase(mongoDBInstance *mongodb.MongoDB, username, authDB string) *mongodb.MongoDBUser {
	i.Log.Debug("Looking info", "user", username, "password", authDB)
	for _, user := range mongoDBInstance.Spec.Users {
		if user.DB == authDB {
			if user.Name == username {
				return &user
			}
		}
	}
	return nil
}

func (i *mongoDBCredentialReconciler) getCredentialSecretName() string {
	return fmt.Sprintf(mongoDBSecretName, i.runtime)
}
