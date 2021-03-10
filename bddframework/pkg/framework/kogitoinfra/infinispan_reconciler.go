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
	"context"
	"fmt"
	"sort"
	"strings"

	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"sigs.k8s.io/controller-runtime/pkg/builder"
	"software.sslmate.com/src/go-pkcs12"

	infinispan "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/framework"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
)

const (
	credentialSecretName = "kogito-infinispan-credential"
	truststoreSecretName = "kogito-infinispan-truststore"

	// appPropInfinispanServerList application property for setting infinispan server
	appPropInfinispanServerList int = iota
	// appPropInfinispanUseAuth application property for enabling infinispan authentication
	appPropInfinispanUseAuth
	// appPropInfinispanSaslMechanism application property for setting infinispan SASL mechanism
	// InfinispanSaslMechanismType is the possible SASL Mechanism used during infinispan connection.
	// For more information, see https://en.wikipedia.org/wiki/Simple_Authentication_and_Security_Layer#SASL_mechanisms.
	appPropInfinispanSaslMechanism
	// appPropInfinispanAuthRealm application property for setting infinispan auth realm
	appPropInfinispanAuthRealm
	appPropInfinispanTrustStore
	appPropInfinispanTrustStoreType
	appPropInfinispanTrustStorePassword
	// envVarInfinispanUser environment variable for setting infinispan username
	envVarInfinispanUser
	// envVarInfinispanPassword environment variable for setting infinispan password
	envVarInfinispanPassword
	// saslPlain is the PLAIN type.
	saslPlain                         = "PLAIN"
	pkcs12CertType                    = "PKCS12"
	certMountPath                     = operator.KogitoHomeDir + "/certs/infinispan"
	truststoreSecretKey               = "truststore.p12"
	truststoreMountPath               = certMountPath + "/" + truststoreSecretKey
	infinispanTLSSecretKey            = "tls.crt"
	infinispanCertMountName           = "infinispan-cert"
	infinispanEnvKeyCredSecret        = "INFINISPAN_CREDENTIAL_SECRET"
	infinispanEnablePersistenceEnvKey = "ENABLE_PERSISTENCE"
	infinispanConditionWellFormed     = "wellFormed"
)

var (
	//Infinispan variables for the KogitoInfra deployed infrastructure.
	//For Quarkus: https://quarkus.io/guides/infinispan-client#quarkus-infinispan-client_configuration
	//For Spring: https://github.com/infinispan/infinispan-spring-boot/blob/master/infinispan-spring-boot-starter-remote/src/test/resources/test-application.properties

	propertiesInfinispan = map[api.RuntimeType]map[int]string{
		api.QuarkusRuntimeType: {
			appPropInfinispanServerList:         "quarkus.infinispan-client.server-list",
			appPropInfinispanUseAuth:            "quarkus.infinispan-client.use-auth",
			appPropInfinispanSaslMechanism:      "quarkus.infinispan-client.sasl-mechanism",
			appPropInfinispanAuthRealm:          "quarkus.infinispan-client.auth-realm",
			appPropInfinispanTrustStore:         "quarkus.infinispan-client.trust-store",
			appPropInfinispanTrustStoreType:     "quarkus.infinispan-client.trust-store-type",
			appPropInfinispanTrustStorePassword: "quarkus.infinispan-client.trust-store-password",

			envVarInfinispanUser:     "QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME",
			envVarInfinispanPassword: "QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD",
		},
		api.SpringBootRuntimeType: {
			appPropInfinispanServerList:         "infinispan.remote.server-list",
			appPropInfinispanUseAuth:            "infinispan.remote.use-auth",
			appPropInfinispanSaslMechanism:      "infinispan.remote.sasl-mechanism",
			appPropInfinispanAuthRealm:          "infinispan.remote.auth-realm",
			appPropInfinispanTrustStore:         "infinispan.remote.trust-store-file-name",
			appPropInfinispanTrustStoreType:     "infinispan.remote.trust-store-type",
			appPropInfinispanTrustStorePassword: "infinispan.remote.trust-store-password",

			envVarInfinispanUser:     "INFINISPAN_REMOTE_AUTH_USERNAME",
			envVarInfinispanPassword: "INFINISPAN_REMOTE_AUTH_PASSWORD",
		},
	}
)

type infinispanInfraReconciler struct {
	infraContext
}

func initInfinispanInfraReconciler(context infraContext) Reconciler {
	context.Log = context.Log.WithValues("resource", "Infinispan")
	return &infinispanInfraReconciler{
		infraContext: context,
	}
}

// AppendInfinispanWatchedObjects ...
func AppendInfinispanWatchedObjects(b *builder.Builder) *builder.Builder {
	return b.Owns(&corev1.Secret{})
}

// Reconcile reconcile Kogito infra object
func (i *infinispanInfraReconciler) Reconcile() (requeue bool, resultErr error) {

	var infinispanInstance *infinispan.Infinispan

	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	if !infinispanHandler.IsInfinispanAvailable() {
		return false, errorForResourceAPINotFound(i.instance.GetSpec().GetResource().GetAPIVersion())
	}

	if len(i.instance.GetSpec().GetResource().GetName()) > 0 {
		i.Log.Debug("Custom infinispan instance reference is provided")

		namespace := i.instance.GetSpec().GetResource().GetNamespace()
		if len(namespace) == 0 {
			namespace = i.instance.GetNamespace()
			i.Log.Debug("Namespace is not provided for custom resource, taking", "Namespace", namespace)
		}
		infinispanInstance, resultErr = infinispanHandler.FetchInfinispanInstance(types.NamespacedName{Name: i.instance.GetSpec().GetResource().GetName(), Namespace: namespace})
		if resultErr != nil {
			return false, resultErr
		}
		if infinispanInstance == nil {
			return false, errorForResourceNotFound("Infinispan", i.instance.GetSpec().GetResource().GetName(), namespace)
		}
	} else {
		return false, errorForResourceConfigError(i.instance, "No Infinispan resource name given")
	}
	infinispanCondition := getInfinispanWellFormedCondition(infinispanInstance)
	if infinispanCondition == nil || infinispanCondition.Status != string(v1.ConditionTrue) {
		return false, errorForResourceNotReadyError(fmt.Errorf("infinispan instance %s not ready. Waiting for Condition with Condition.Type == %s to have Condition.Status == %s", infinispanInstance.Name, infinispanConditionWellFormed, v1.ConditionTrue))
	}
	if resultErr := i.updateInfinispanVolumesInStatus(infinispanInstance); resultErr != nil {
		return false, nil
	}
	if resultErr := i.updateInfinispanRuntimePropsInStatus(infinispanInstance, api.QuarkusRuntimeType); resultErr != nil {
		return false, nil
	}
	if resultErr := i.updateInfinispanRuntimePropsInStatus(infinispanInstance, api.SpringBootRuntimeType); resultErr != nil {
		return false, nil
	}
	return false, resultErr
}

func (i *infinispanInfraReconciler) getInfinispanRuntimeSecretEnvVars(infinispanInstance *infinispan.Infinispan, runtime api.RuntimeType) ([]corev1.EnvVar, error) {
	var envProps []corev1.EnvVar

	customInfinispanSecret, resultErr := i.loadCustomKogitoInfinispanSecret()
	if resultErr != nil {
		return nil, resultErr
	}

	if customInfinispanSecret == nil {
		customInfinispanSecret, resultErr = i.createCustomKogitoInfinispanSecret(infinispanInstance)
		if resultErr != nil {
			return nil, resultErr
		}
	}

	envProps = append(envProps, framework.CreateEnvVar(infinispanEnablePersistenceEnvKey, "true"))
	secretName := customInfinispanSecret.Name
	envProps = append(envProps, framework.CreateEnvVar(infinispanEnvKeyCredSecret, secretName))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesInfinispan[runtime][envVarInfinispanUser], secretName, infrastructure.InfinispanSecretUsernameKey))
	envProps = append(envProps, framework.CreateSecretEnvVar(propertiesInfinispan[runtime][envVarInfinispanPassword], secretName, infrastructure.InfinispanSecretPasswordKey))
	sort.Slice(envProps, func(i, j int) bool {
		return envProps[i].Name < envProps[j].Name
	})
	return envProps, nil
}

func (i *infinispanInfraReconciler) getInfinispanRuntimeAppProps(name string, namespace string, runtime api.RuntimeType) (map[string]string, error) {
	appProps := map[string]string{}

	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	infinispanURI, resultErr := infinispanHandler.FetchInfinispanInstanceURI(types.NamespacedName{Name: name, Namespace: namespace})
	if resultErr != nil {
		return nil, resultErr
	}

	appProps[propertiesInfinispan[runtime][appPropInfinispanUseAuth]] = "true"
	if len(infinispanURI) > 0 {
		appProps[propertiesInfinispan[runtime][appPropInfinispanServerList]] = infinispanURI
	}
	appProps[propertiesInfinispan[runtime][appPropInfinispanSaslMechanism]] = saslPlain

	if hasInfinispanMountedVolume(i.instance) {
		appProps[propertiesInfinispan[runtime][appPropInfinispanTrustStoreType]] = pkcs12CertType
		appProps[propertiesInfinispan[runtime][appPropInfinispanTrustStore]] = truststoreMountPath
		appProps[propertiesInfinispan[runtime][appPropInfinispanTrustStorePassword]] = pkcs12.DefaultPassword
	}
	return appProps, nil
}

func (i *infinispanInfraReconciler) getInfinispanRuntimeProps(infinispanInstance *infinispan.Infinispan, runtime api.RuntimeType) (api.RuntimePropertiesInterface, error) {
	runtimeProps := v1beta1.RuntimeProperties{}
	appProps, err := i.getInfinispanRuntimeAppProps(infinispanInstance.Name, infinispanInstance.Namespace, runtime)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.AppProps = appProps

	envVars, err := i.getInfinispanRuntimeSecretEnvVars(infinispanInstance, runtime)
	if err != nil {
		return runtimeProps, err
	}
	runtimeProps.Env = envVars

	return runtimeProps, nil
}

func (i *infinispanInfraReconciler) updateInfinispanRuntimePropsInStatus(infinispanInstance *infinispan.Infinispan, runtime api.RuntimeType) error {
	i.Log.Debug("going to Update Infinispan runtime properties in kogito infra instance status", "runtime", runtime)
	runtimeProps, err := i.getInfinispanRuntimeProps(infinispanInstance, runtime)
	if err != nil {
		return err
	}
	setRuntimeProperties(i.instance, runtime, runtimeProps)
	i.Log.Debug("Following Infinispan runtime properties are set in infra status:", "runtime", runtime, "properties", runtimeProps)
	return nil
}

func (i *infinispanInfraReconciler) updateInfinispanVolumesInStatus(infinispanInstance *infinispan.Infinispan) error {
	if len(infinispanInstance.Status.Security.EndpointEncryption.CertSecretName) == 0 {
		return nil
	}
	tlsSecret, err := i.ensureEncryptionTrustStoreSecret(infinispanInstance)
	if err != nil || tlsSecret == nil {
		return err
	}
	var volumes []api.KogitoInfraVolumeInterface
	volume := v1beta1.KogitoInfraVolume{
		Mount: corev1.VolumeMount{
			Name:      infinispanCertMountName,
			ReadOnly:  true,
			MountPath: truststoreMountPath,
			SubPath:   truststoreSecretKey,
		},
		NamedVolume: v1beta1.ConfigVolume{
			Name: infinispanCertMountName,
			ConfigVolumeSource: v1beta1.ConfigVolumeSource{
				Secret: &corev1.SecretVolumeSource{
					SecretName: tlsSecret.Name,
					Items: []corev1.KeyToPath{
						{
							Key:  truststoreSecretKey,
							Path: truststoreSecretKey,
							Mode: &framework.ModeForCertificates,
						},
					},
					DefaultMode: &framework.ModeForCertificates,
				},
			},
		},
	}
	volumes = append(volumes, volume)
	i.instance.GetStatus().SetVolumes(volumes)
	return nil
}

func (i *infinispanInfraReconciler) ensureEncryptionTrustStoreSecret(infinispanInstance *infinispan.Infinispan) (*corev1.Secret, error) {
	if len(infinispanInstance.Status.Security.EndpointEncryption.CertSecretName) == 0 {
		return nil, nil
	}
	kogitoInfraEncryptionSecret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{Name: truststoreSecretName, Namespace: infinispanInstance.Namespace},
	}
	if exists, err := kubernetes.ResourceC(i.Client).Fetch(kogitoInfraEncryptionSecret); err != nil {
		return nil, err
	} else if !exists {
		infinispanSecret := &corev1.Secret{
			ObjectMeta: metav1.ObjectMeta{
				Name:      infinispanInstance.Status.Security.EndpointEncryption.CertSecretName,
				Namespace: infinispanInstance.Namespace}}
		if ispnSecretExists, err := kubernetes.ResourceC(i.Client).Fetch(infinispanSecret); err != nil || !ispnSecretExists {
			return nil, err
		}
		trustStore, err := framework.CreatePKCS12TrustStoreFromSecret(infinispanSecret, pkcs12.DefaultPassword, infinispanTLSSecretKey)
		if err != nil {
			return nil, err
		}
		kogitoInfraEncryptionSecret.Type = corev1.SecretTypeOpaque
		kogitoInfraEncryptionSecret.Data = map[string][]byte{truststoreSecretKey: trustStore}
		// we need to create the secret calling the API directly, for some reason the bytes of the generated file gets corrupted
		if err := framework.SetOwner(i.instance, i.Scheme, kogitoInfraEncryptionSecret); err != nil {
			return nil, err
		}
		if err := i.Client.ControlCli.Create(context.TODO(), kogitoInfraEncryptionSecret); err != nil {
			return nil, err
		}
	}
	return kogitoInfraEncryptionSecret, nil
}

func (i *infinispanInfraReconciler) loadCustomKogitoInfinispanSecret() (*corev1.Secret, error) {
	i.Log.Debug("Fetching", "Secret", credentialSecretName)
	secret := &corev1.Secret{}
	if exists, err := kubernetes.ResourceC(i.Client).FetchWithKey(types.NamespacedName{Name: credentialSecretName, Namespace: i.instance.GetNamespace()}, secret); err != nil {
		i.Log.Error(err, "Error occurs while fetching %s", "Secret", credentialSecretName)
		return nil, err
	} else if !exists {
		i.Log.Error(fmt.Errorf("credentials Not found"), "not found", "Secret", credentialSecretName)
		return nil, nil
	} else {
		i.Log.Debug("successfully fetched", "Secret", credentialSecretName)
		return secret, nil
	}
}

func (i *infinispanInfraReconciler) createCustomKogitoInfinispanSecret(infinispanInstance *infinispan.Infinispan) (*corev1.Secret, error) {
	i.Log.Debug("Creating new secret", "Secret", credentialSecretName)
	infinispanHandler := infrastructure.NewInfinispanHandler(i.Context)
	credentials, err := infinispanHandler.GetInfinispanCredential(infinispanInstance)
	if err != nil {
		return nil, err
	}
	secret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      credentialSecretName,
			Namespace: i.instance.GetNamespace(),
		},
		Type: corev1.SecretTypeOpaque,
	}
	if credentials != nil {
		secret.StringData = map[string]string{
			infrastructure.InfinispanSecretUsernameKey: credentials.Username,
			infrastructure.InfinispanSecretPasswordKey: credentials.Password,
		}
	}
	if err := controllerutil.SetOwnerReference(i.instance, secret, i.Scheme); err != nil {
		return nil, err
	}
	if err := kubernetes.ResourceC(i.Client).Create(secret); err != nil {
		i.Log.Error(err, "Error occurs while creating ", "Secret", secret)
		return nil, err
	}
	i.Log.Debug("successfully created", "Secret", secret)
	return secret, nil
}

func hasInfinispanMountedVolume(infra api.KogitoInfraInterface) bool {
	for _, volume := range infra.GetStatus().GetVolumes() {
		if volume.GetNamedVolume().GetName() == infinispanCertMountName {
			return true
		}
	}
	return false
}

func getInfinispanWellFormedCondition(instance *infinispan.Infinispan) *infinispan.InfinispanCondition {
	if len(instance.Status.Conditions) == 0 {
		return nil
	}

	// Infinispan does not have a condition transition date, so let's get the wellFormed condition
	for _, condition := range instance.Status.Conditions {
		// Can be replaced by instance.IsWellFormed() once Infinispan release operator 2.0.7 and we update the dependency
		if strings.EqualFold(condition.Type, infinispanConditionWellFormed) {
			return &condition
		}
	}

	return nil
}
