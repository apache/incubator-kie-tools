// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package platform

import (
	"context"
	"runtime"
	"strings"
	"time"

	"gopkg.in/yaml.v2"
	corev1 "k8s.io/api/core/v1"
	rbacv1 "k8s.io/api/rbac/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	k8s "k8s.io/client-go/kubernetes"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/client"
	"github.com/kiegroup/container-builder/util/defaults"
	"github.com/kiegroup/container-builder/util/log"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
	"github.com/kiegroup/kogito-serverless-operator/install"
)

// BuilderServiceAccount --.
const BuilderServiceAccount = "kogito-builder"

// ResourceCustomizer can be used to inject code that changes the objects before they are created.
type ResourceCustomizer func(object ctrl.Object) ctrl.Object

func ConfigureRegistry(ctx context.Context, c client.Client, p *v08.KogitoServerlessPlatform, verbose bool) error {
	if p.Status.Cluster == v08.PlatformClusterOpenShift &&
		p.Status.BuildPlatform.Registry.Address == "" {
		log.Debugf("Kogito Serverless Platform [%s]: setting registry address", p.Namespace)
		// Default to using OpenShift internal container images registry when using a strategy other than S2I
		p.Status.BuildPlatform.Registry.Address = "image-registry.openshift-image-registry.svc:5000"

		// OpenShift automatically injects the service CA certificate into the service-ca.crt key on the ConfigMap
		cm, err := createServiceCaBundleConfigMap(ctx, c, p)
		if err != nil {
			return err
		}
		log.Debugf("Kogito Serverless Platform [%s]: setting registry certificate authority", p.Namespace)
		p.Status.BuildPlatform.Registry.CA = cm.Name

		// Default to using the registry secret that's configured for the builder service account
		if p.Status.BuildPlatform.Registry.Secret == "" {
			log.Debugf("Kogito Serverless Platform [%s]: setting registry secret", p.Namespace)
			// Bind the required role to push images to the registry
			err := createBuilderRegistryRoleBinding(ctx, c, p)
			if err != nil {
				return err
			}

			sa := corev1.ServiceAccount{}
			err = c.Get(ctx, types.NamespacedName{Namespace: p.Namespace, Name: BuilderServiceAccount}, &sa)
			if err != nil {
				return err
			}
			// We may want to read the secret keys instead of relying on the secret name scheme
			for _, secret := range sa.Secrets {
				if strings.Contains(secret.Name, "kogito-builder-dockercfg") {
					p.Status.BuildPlatform.Registry.Secret = secret.Name

					break
				}
			}
		}
	}
	if p.Status.BuildPlatform.Registry.Address == "" {
		// try KEP-1755
		address, err := GetRegistryAddress(ctx, c)
		if err != nil && verbose {
			log.Error(err, "Cannot find a registry where to push images via KEP-1755")
		} else if err == nil && address != nil {
			p.Status.BuildPlatform.Registry.Address = *address
		}
	}

	log.Debugf("Final Registry Address: %s", p.Status.BuildPlatform.Registry.Address)
	return nil
}

func createServiceCaBundleConfigMap(ctx context.Context, client client.Client, p *v08.KogitoServerlessPlatform) (*corev1.ConfigMap, error) {
	cm := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      BuilderServiceAccount + "-ca",
			Namespace: p.Namespace,
			Annotations: map[string]string{
				"service.beta.openshift.io/inject-cabundle": "true",
			},
		},
	}

	err := client.Create(ctx, cm)
	if err != nil && !k8serrors.IsAlreadyExists(err) {
		return nil, err
	}

	return cm, nil
}

func SetPlatformDefaults(p *v08.KogitoServerlessPlatform, verbose bool) error {
	if p.Status.BuildPlatform.PublishStrategyOptions == nil {
		log.Debugf("Kogito Serverless Platform [%s]: setting publish strategy options", p.Namespace)
		p.Status.BuildPlatform.PublishStrategyOptions = map[string]string{}
	}

	if _, ok := p.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoPVCName]; !ok {
		log.Debugf("Kogito Serverless Platform [%s]: setting publish strategy options", p.Namespace)
		p.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoPVCName] = p.Name
	}

	if p.Status.BuildPlatform.GetTimeout().Duration != 0 {
		d := p.Status.BuildPlatform.GetTimeout().Duration.Truncate(time.Second)

		if verbose && p.Status.BuildPlatform.GetTimeout().Duration != d {
			log.Log.Infof("Build timeout minimum unit is sec (configured: %s, truncated: %s)", p.Status.BuildPlatform.GetTimeout().Duration, d)
		}

		log.Debugf("Kogito Serverless Platform [%s]: setting build timeout", p.Namespace)
		p.Status.BuildPlatform.Timeout = &metav1.Duration{
			Duration: d,
		}
	}
	if p.Status.BuildPlatform.GetTimeout().Duration == 0 {
		p.Status.BuildPlatform.Timeout = &metav1.Duration{
			Duration: 5 * time.Minute,
		}
	}
	_, cacheEnabled := p.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoBuildCacheEnabled]
	if p.Status.BuildPlatform.PublishStrategy == api.PlatformBuildPublishStrategyKaniko && !cacheEnabled {
		// Default to disabling Kaniko cache warmer
		// Using the cache warmer pod seems unreliable with the current Kaniko version
		// and requires relying on a persistent volume.
		defaultKanikoBuildCache := "false"
		p.Status.BuildPlatform.PublishStrategyOptions[builder.KanikoBuildCacheEnabled] = defaultKanikoBuildCache
		if verbose {
			log.Log.Infof("Kaniko cache set to %s", defaultKanikoBuildCache)
		}
	}

	setStatusAdditionalInfo(p)

	if verbose {
		log.Log.Infof("BaseImage set to %s", p.Status.BuildPlatform.BaseImage)
		log.Log.Infof("Timeout set to %s", p.Status.BuildPlatform.GetTimeout())
	}
	return nil
}

func setStatusAdditionalInfo(platform *v08.KogitoServerlessPlatform) {
	platform.Status.Info = make(map[string]string)

	log.Debugf("Kogito Serverless Platform [%s]: setting build publish strategy", platform.Namespace)
	if platform.Spec.BuildPlatform.PublishStrategy == api.PlatformBuildPublishStrategyKaniko {
		platform.Status.Info["kanikoVersion"] = defaults.KanikoVersion
	}
	log.Debugf("Kogito Serverless [%s]: setting status info", platform.Namespace)
	platform.Status.Info["goVersion"] = runtime.Version()
	platform.Status.Info["goOS"] = runtime.GOOS
}

// IsOpenShift returns true if we are connected to a OpenShift cluster.
func IsOpenShift(client k8s.Interface) (bool, error) {
	_, err := client.Discovery().ServerResourcesForGroupVersion("image.openshift.io/v1")
	if err != nil && k8serrors.IsNotFound(err) {
		return false, nil
	} else if err != nil {
		return false, err
	}

	return true, nil
}

func createBuilderRegistryRoleBinding(ctx context.Context, client client.Client, p *v08.KogitoServerlessPlatform) error {
	rb := &rbacv1.RoleBinding{
		ObjectMeta: metav1.ObjectMeta{
			Name:      BuilderServiceAccount + "-registry",
			Namespace: p.Namespace,
		},
		Subjects: []rbacv1.Subject{
			{
				Kind: "ServiceAccount",
				Name: BuilderServiceAccount,
			},
		},
		RoleRef: rbacv1.RoleRef{
			Kind:     "ClusterRole",
			APIGroup: "rbac.authorization.k8s.io",
			Name:     "system:image-builder",
		},
	}

	err := client.Create(ctx, rb)
	if err != nil {
		if k8serrors.IsForbidden(err) {
			log.Log.Infof("Cannot grant permission to push images to the registry. "+
				"Run 'oc policy add-role-to-user system:image-builder system:serviceaccount:%s:%s' as a system admin.", p.Namespace, BuilderServiceAccount)
		} else if !k8serrors.IsAlreadyExists(err) {
			return err
		}
	}

	return nil
}

// GetRegistryAddress KEP-1755
// https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
func GetRegistryAddress(ctx context.Context, c client.Client) (*string, error) {
	config := corev1.ConfigMap{}
	err := c.Get(ctx, ctrl.ObjectKey{Namespace: "kube-public", Name: "local-registry-hosting"}, &config)
	if err != nil {
		if k8serrors.IsNotFound(err) {
			return nil, nil
		}
		return nil, err
	}
	if data, ok := config.Data["localRegistryHosting.v1"]; ok {
		result := LocalRegistryHostingV1{}
		if err := yaml.Unmarshal([]byte(data), &result); err != nil {
			return nil, err
		}
		return &result.HostFromClusterNetwork, nil
	}
	return nil, nil
}

func CreateBuilderServiceAccount(ctx context.Context, client client.Client, p *v08.KogitoServerlessPlatform) error {
	log.Debugf("Kogito Serverless Platform [%s]: creating build service account", p.Namespace)
	sa := corev1.ServiceAccount{}
	key := ctrl.ObjectKey{
		Name:      BuilderServiceAccount,
		Namespace: p.Namespace,
	}

	err := client.Get(ctx, key, &sa)
	if err != nil && k8serrors.IsNotFound(err) {
		return install.BuilderServiceAccountRoles(ctx, client, p.Namespace, p.Status.Cluster)
	}

	return err
}
