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

package kogitoinfra

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	mongodb "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/builder"
)

const (
	// Using URI for Quarkus as this is what we get from MongoDB instance
	// and host/port for Spring Boot because URI cannot be used with credentials (Spring Boot Starter restriction) ...
	// https://github.com/spring-projects/spring-boot/blob/b7fdf8fe87da1c01ff6aca041170a02f11280a1a/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/mongo/MongoProperties.java#L61-L64
	appPropMongoDBURI  = iota // for Quarkus
	appPropMongoDBHost        // for Spring boot
	appPropMongoDBPort        // for Spring boot

	envVarMongoDBAuthDatabase
	envVarMongoDBUser
	envVarMongoDBPassword
	envVarMongoDBDatabase

	//mongoDBEnvKeyCredSecret        = "MONGODB_CREDENTIAL_SECRET"
	mongoDBEnablePersistenceEnvKey = "ENABLE_PERSISTENCE"

	infraPropertiesUserKey         = "username"
	infraPropertiesDatabaseKey     = "database"
	infraPropertiesAuthDatabaseKey = "auth-database"
)

var (
	// MongoDB variables for the KogitoInfra deployed infrastructure.
	//For Quarkus: https://quarkus.io/guides/mongoDB-client#quarkus-mongoDB-client_configuration
	//For Spring: https://github.com/mongoDB/mongoDB-spring-boot/blob/master/mongoDB-spring-boot-starter-remote/src/test/resources/test-application.properties

	propertiesMongoDB = map[api.RuntimeType]map[int]string{
		api.QuarkusRuntimeType: {
			appPropMongoDBURI: "quarkus.mongodb.connection-string",

			envVarMongoDBAuthDatabase: "QUARKUS_MONGODB_CREDENTIALS_AUTH_SOURCE",
			envVarMongoDBUser:         "QUARKUS_MONGODB_CREDENTIALS_USERNAME",
			envVarMongoDBPassword:     "QUARKUS_MONGODB_CREDENTIALS_PASSWORD",
			envVarMongoDBDatabase:     "QUARKUS_MONGODB_DATABASE",
		},
		api.SpringBootRuntimeType: {
			appPropMongoDBHost: "spring.data.mongodb.host",
			appPropMongoDBPort: "spring.data.mongodb.port",

			envVarMongoDBAuthDatabase: "SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE",
			envVarMongoDBUser:         "SPRING_DATA_MONGODB_USERNAME",
			envVarMongoDBPassword:     "SPRING_DATA_MONGODB_PASSWORD",
			envVarMongoDBDatabase:     "SPRING_DATA_MONGODB_DATABASE",
		},
	}
)

type mongoDBInfraReconciler struct {
	infraContext
}

func initMongoDBInfraReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "mongoDB")
	return &mongoDBInfraReconciler{
		infraContext: context,
	}
}

// AppendMongoDBWatchedObjects ...
func AppendMongoDBWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.Secret{})
}

// Reconcile reconcile Kogito infra object
func (i *mongoDBInfraReconciler) Reconcile() (resultErr error) {
	var mongoDBInstance *mongodb.MongoDB
	mongoDBHandler := infrastructure.NewMongoDBHandler(i.Context)
	if !mongoDBHandler.IsMongoDBAvailable() {
		return errorForResourceAPINotFound(i.instance.GetSpec().GetResource().GetAPIVersion())
	}

	// Step 1: check whether user has provided custom mongoDB instance reference
	mongoDBNamespace := i.instance.GetSpec().GetResource().GetNamespace()
	mongoDBName := i.instance.GetSpec().GetResource().GetName()
	if len(mongoDBNamespace) == 0 {
		mongoDBNamespace = i.instance.GetNamespace()
		i.Log.Debug("Namespace is not provided for infrastructure MongoDB resource", "instance", i.instance.GetName(), "namespace", mongoDBNamespace)
	}
	if len(mongoDBName) == 0 {
		return errorForResourceConfigError(i.instance, "No resource name given")
	}

	if mongoDBInstance, resultErr = mongoDBHandler.FetchMongoDBInstance(types.NamespacedName{Name: mongoDBName, Namespace: mongoDBNamespace}); resultErr != nil {
		return resultErr
	} else if mongoDBInstance == nil {
		return errorForResourceNotFound("MongoDB", i.instance.GetSpec().GetResource().GetName(), mongoDBNamespace)
	}

	i.Log.Debug("Got MongoDB instance", "instance", mongoDBInstance)
	if mongoDBInstance.Status.Phase != mongodb.Running {
		return errorForResourceNotReadyError(fmt.Errorf("mongoDB instance %s not ready. Waiting for Status.Phase == Running", mongoDBInstance.Name))
	}
	i.Log.Info("MongoDB instance is running")
	if resultErr = i.updateMongoDBRuntimePropsInStatus(mongoDBInstance, api.QuarkusRuntimeType); resultErr != nil {
		return resultErr
	}
	if resultErr = i.updateMongoDBRuntimePropsInStatus(mongoDBInstance, api.SpringBootRuntimeType); resultErr != nil {
		return resultErr
	}
	return resultErr
}

func (i *mongoDBInfraReconciler) updateMongoDBRuntimePropsInStatus(mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) error {
	i.Log.Debug("going to Update MongoDB runtime properties in kogito infra instance status", "runtime", runtime)
	mongoDBConfigReconciler := newMongoDBConfigReconciler(i.infraContext, mongoDBInstance, runtime)
	if err := mongoDBConfigReconciler.Reconcile(); err != nil {
		return err
	}

	mongoDBCredentialReconciler := newMongoDBCredentialReconciler(i.infraContext, mongoDBInstance, runtime)
	if err := mongoDBCredentialReconciler.Reconcile(); err != nil {
		return err
	}
	return nil
}
