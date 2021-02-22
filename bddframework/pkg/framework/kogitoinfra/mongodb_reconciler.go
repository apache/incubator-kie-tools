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
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/client"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/framework"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	mongodb "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"net/url"
	"sigs.k8s.io/controller-runtime/pkg/builder"
)

const (
	mongoDBSecretName = "kogito-mongodb-credential"

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

	mongoDBEnvKeyCredSecret        = "MONGODB_CREDENTIAL_SECRET"
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

// AppendMongoDBWatchedObjects ...
func AppendMongoDBWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.Secret{})
}

func (i *mongoDBInfraReconciler) getMongoDBRuntimeSecretEnvVars(mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) ([]corev1.EnvVar, error) {
	var envProps []corev1.EnvVar

	customMongoDBSecret, resultErr := i.loadCustomKogitoMongoDBSecret(i.instance.GetNamespace())
	if resultErr != nil {
		return nil, resultErr
	}

	if customMongoDBSecret == nil {
		customMongoDBSecret, resultErr = i.createCustomKogitoMongoDBSecret(i.instance.GetNamespace(), mongoDBInstance)
		if resultErr != nil {
			return nil, resultErr
		}
	}

	envProps = append(envProps, framework.CreateEnvVar(mongoDBEnablePersistenceEnvKey, "true"))
	mongoDBSecretName := customMongoDBSecret.Name
	envProps = append(envProps, framework.CreateEnvVar(mongoDBEnvKeyCredSecret, mongoDBSecretName))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesMongoDB[runtime][envVarMongoDBAuthDatabase], mongoDBSecretName, infrastructure.MongoDBAppSecretAuthDatabaseKey))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesMongoDB[runtime][envVarMongoDBUser], mongoDBSecretName, infrastructure.MongoDBAppSecretUsernameKey))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesMongoDB[runtime][envVarMongoDBPassword], mongoDBSecretName, infrastructure.MongoDBAppSecretPasswordKey))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesMongoDB[runtime][envVarMongoDBDatabase], mongoDBSecretName, infrastructure.MongoDBAppSecretDatabaseKey))
	return envProps, nil
}

func (i *mongoDBInfraReconciler) getMongoDBRuntimeAppProps(mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) (map[string]string, error) {
	appProps := map[string]string{}

	mongoDBURI := mongoDBInstance.Status.MongoURI
	if len(mongoDBURI) > 0 {
		mongoDBParsedURL, err := url.ParseRequestURI(mongoDBURI)
		if err != nil {
			return nil, err
		}
		if runtime == api.QuarkusRuntimeType {
			appProps[propertiesMongoDB[runtime][appPropMongoDBURI]] = mongoDBURI
		} else if runtime == api.SpringBootRuntimeType {
			appProps[propertiesMongoDB[runtime][appPropMongoDBHost]] = mongoDBParsedURL.Hostname()
			appProps[propertiesMongoDB[runtime][appPropMongoDBPort]] = mongoDBParsedURL.Port()
		}
	}

	return appProps, nil
}

func (i *mongoDBInfraReconciler) getMongoDBRuntimeProps(mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) (api.RuntimePropertiesInterface, error) {
	runtimeProps := v1beta1.RuntimeProperties{}
	appProps, err := i.getMongoDBRuntimeAppProps(mongoDBInstance, runtime)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.AppProps = appProps

	envVars, err := i.getMongoDBRuntimeSecretEnvVars(mongoDBInstance, runtime)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.Env = envVars

	return runtimeProps, nil
}

func (i *mongoDBInfraReconciler) updateMongoDBRuntimePropsInStatus(mongoDBInstance *mongodb.MongoDB, runtime api.RuntimeType) error {
	i.Log.Debug("going to Update MongoDB runtime properties in kogito infra instance status", "runtime", runtime)
	runtimeProps, err := i.getMongoDBRuntimeProps(mongoDBInstance, runtime)
	if err != nil {
		return err
	}
	setRuntimeProperties(i.instance, runtime, runtimeProps)
	i.Log.Debug("Following MongoDB runtime properties are set in infra status:", "runtime", runtime, "properties", runtimeProps)
	return nil
}

func (i *mongoDBInfraReconciler) loadDeployedMongoDBInstance(instanceName string, namespace string) (*mongodb.MongoDB, error) {
	i.Log.Debug("fetching deployed kogito mongoDB instance")
	mongoDBInstance := &mongodb.MongoDB{}
	if exists, err := kubernetes.ResourceC(i.Client).FetchWithKey(types.NamespacedName{Name: instanceName, Namespace: namespace}, mongoDBInstance); err != nil {
		i.Log.Error(err, "Error occurs while fetching kogito mongoDB instance")
		return nil, err
	} else if !exists {
		i.Log.Debug("Kogito mongoDB instance is not exists")
		return nil, nil
	} else {
		i.Log.Debug("Kogito mongoDB instance found")
		i.Log.Debug("Kogito mongoDB instance found", "instance", mongoDBInstance)
		return mongoDBInstance, nil
	}
}

func (i *mongoDBInfraReconciler) loadCustomKogitoMongoDBSecret(namespace string) (*corev1.Secret, error) {
	i.Log.Debug("Fetching", "secret", mongoDBSecretName)
	secret := &corev1.Secret{}
	if exists, err := kubernetes.ResourceC(i.Client).FetchWithKey(types.NamespacedName{Name: mongoDBSecretName, Namespace: namespace}, secret); err != nil {
		i.Log.Error(err, "Error occurs while fetching", "secret", mongoDBSecretName)
		return nil, err
	} else if !exists {
		i.Log.Debug("not found", "secret", mongoDBSecretName)
		return nil, nil
	} else {
		i.Log.Debug("successfully fetched", "secret", mongoDBSecretName)
		return secret, nil
	}
}

// Setup authentication to MongoDB
// https://github.com/mongodb/mongodb-kubernetes-operator/blob/master/docs/users.md
func (i *mongoDBInfraReconciler) createCustomKogitoMongoDBSecret(namespace string, mongoDBInstance *mongodb.MongoDB) (*corev1.Secret, error) {
	i.Log.Debug("Creating new secret", "secret", mongoDBSecretName)

	credentials, err := i.retrieveMongoDBCredentialsFromInstance(i.Client, i.instance, mongoDBInstance)
	if err != nil {
		return nil, err
	}
	secret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      mongoDBSecretName,
			Namespace: namespace,
		},
		Type: corev1.SecretTypeOpaque,
		StringData: map[string]string{
			infrastructure.MongoDBAppSecretAuthDatabaseKey: credentials.AuthDatabase,
			infrastructure.MongoDBAppSecretUsernameKey:     credentials.Username,
			infrastructure.MongoDBAppSecretPasswordKey:     credentials.Password,
			infrastructure.MongoDBAppSecretDatabaseKey:     credentials.Database,
		},
	}
	if err := framework.SetOwner(i.instance, i.Scheme, secret); err != nil {
		return nil, err
	}
	if err := kubernetes.ResourceC(i.Client).Create(secret); err != nil {
		i.Log.Error(err, "Error occurs while creating", "secret", secret)
		return nil, err
	}
	i.Log.Debug("Successfully created", "Secret", secret)
	return secret, nil
}

type mongoDBInfraReconciler struct {
	infraContext
}

func initMongoDBInfraReconciler(context infraContext) *mongoDBInfraReconciler {
	context.Log = context.Log.WithValues("resource", "mongoDB")
	return &mongoDBInfraReconciler{
		infraContext: context,
	}
}

// Reconcile reconcile Kogito infra object
func (i *mongoDBInfraReconciler) Reconcile() (requeue bool, resultErr error) {
	var mongoDBInstance *mongodb.MongoDB
	mongoDBHandler := infrastructure.NewMongoDBHandler(i.Context)
	if !mongoDBHandler.IsMongoDBAvailable() {
		return false, errorForResourceAPINotFound(i.instance.GetSpec().GetResource().GetAPIVersion())
	}

	// Step 1: check whether user has provided custom mongoDB instance reference
	mongoDBNamespace := i.instance.GetSpec().GetResource().GetNamespace()
	mongoDBName := i.instance.GetSpec().GetResource().GetName()
	if len(mongoDBNamespace) == 0 {
		mongoDBNamespace = i.instance.GetNamespace()
		i.Log.Debug("Namespace is not provided for infrastructure MongoDB resource", "instance", i.instance.GetName(), "namespace", mongoDBNamespace)
	}
	if len(mongoDBName) == 0 {
		return false, errorForResourceConfigError(i.instance, "No resource name given")
	}

	if mongoDBInstance, resultErr = i.loadDeployedMongoDBInstance(mongoDBName, mongoDBNamespace); resultErr != nil {
		return false, resultErr
	} else if mongoDBInstance == nil {
		return false, errorForResourceNotFound("MongoDB", i.instance.GetSpec().GetResource().GetName(), mongoDBNamespace)
	}

	i.Log.Debug("Got MongoDB instance", "instance", mongoDBInstance)
	if mongoDBInstance.Status.Phase != mongodb.Running {
		return false, errorForResourceNotReadyError(fmt.Errorf("mongoDB instance %s not ready. Waiting for Status.Phase == Running", mongoDBInstance.Name))
	}
	i.Log.Info("MongoDB instance is running")
	if resultErr = i.updateMongoDBRuntimePropsInStatus(mongoDBInstance, api.QuarkusRuntimeType); resultErr != nil {
		return true, resultErr
	}
	if resultErr = i.updateMongoDBRuntimePropsInStatus(mongoDBInstance, api.SpringBootRuntimeType); resultErr != nil {
		return true, resultErr
	}
	return false, resultErr
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
func (i *mongoDBInfraReconciler) retrieveMongoDBCredentialsFromInstance(cli *client.Client, kogitoInfra api.KogitoInfraInterface, mongoDBInstance *mongodb.MongoDB) (*MongoDBCredential, error) {
	creds := &MongoDBCredential{}
	if len(kogitoInfra.GetSpec().GetInfraProperties()[infraPropertiesUserKey]) == 0 {
		return nil, errorForMissingResourceConfig(kogitoInfra, infraPropertiesUserKey)
	} else if len(kogitoInfra.GetSpec().GetInfraProperties()[infraPropertiesDatabaseKey]) == 0 {
		return nil, errorForMissingResourceConfig(kogitoInfra, infraPropertiesDatabaseKey)
	}
	creds.Username = kogitoInfra.GetSpec().GetInfraProperties()[infraPropertiesUserKey]
	creds.Database = kogitoInfra.GetSpec().GetInfraProperties()[infraPropertiesDatabaseKey]
	creds.AuthDatabase = kogitoInfra.GetSpec().GetInfraProperties()[infraPropertiesAuthDatabaseKey]
	if len(creds.AuthDatabase) == 0 {
		creds.AuthDatabase = infrastructure.DefaultMongoDBAuthDatabase
	}

	user := i.findMongoDBUserByUsernameAndAuthDatabase(mongoDBInstance, creds.Username, creds.AuthDatabase)
	if user == nil {
		return nil, errorForResourceConfigError(kogitoInfra, fmt.Sprintf("No user found in MongoDB configuration for username %s and authentication database %s", creds.Username, creds.AuthDatabase))
	}
	i.Log.Debug("Found", "user", user.Name, "authDB", user.DB, "password ref", user.PasswordSecretRef)

	secret := &corev1.Secret{ObjectMeta: metav1.ObjectMeta{Name: user.PasswordSecretRef.Name, Namespace: mongoDBInstance.Namespace}}
	if exists, err := kubernetes.ResourceC(cli).Fetch(secret); err != nil {
		return nil, err
	} else if !exists {
		return nil, errorForResourceNotFound("Secret", user.PasswordSecretRef.Name, kogitoInfra.GetNamespace())
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

func (i *mongoDBInfraReconciler) findMongoDBUserByUsernameAndAuthDatabase(mongoDBInstance *mongodb.MongoDB, username, authDB string) *mongodb.MongoDBUser {
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
