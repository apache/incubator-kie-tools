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
	"fmt"
	"os"
	"strings"

	coordination "k8s.io/api/coordination/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"

	"github.com/kiegroup/container-builder/util/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/utils"
)

const (
	// DefaultPlatformName is the standard name used for the platform.
	DefaultPlatformName = "kogito-serverless-platform"

	OperatorWatchNamespaceEnvVariable = "WATCH_NAMESPACE"
	operatorNamespaceEnvVariable      = "NAMESPACE"
	operatorPodNameEnvVariable        = "POD_NAME"
)

// Copied from https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry

// LocalRegistryHostingV1 describes a local registry that developer tools can
// connect to. A local registry allows clients to load images into the local
// cluster by pushing to this registry.
type LocalRegistryHostingV1 struct {
	// Host documents the host (hostname and port) of the registry, as seen from
	// outside the cluster.
	//
	// This is the registry host that tools outside the cluster should push images
	// to.
	Host string `yaml:"host,omitempty"`

	// HostFromClusterNetwork documents the host (hostname and port) of the
	// registry, as seen from networking inside the container pods.
	//
	// This is the registry host that tools running on pods inside the cluster
	// should push images to. If not set, then tools inside the cluster should
	// assume the local registry is not available to them.
	HostFromClusterNetwork string `yaml:"hostFromClusterNetwork,omitempty"`

	// HostFromContainerRuntime documents the host (hostname and port) of the
	// registry, as seen from the cluster's container runtime.
	//
	// When tools apply Kubernetes objects to the cluster, this host should be
	// used for image name fields. If not set, users of this field should use the
	// value of Host instead.
	//
	// Note that it doesn't make sense semantically to define this field, but not
	// define Host or HostFromClusterNetwork. That would imply a way to pull
	// images without a way to push images.
	HostFromContainerRuntime string `yaml:"hostFromContainerRuntime,omitempty"`

	// Help contains a URL pointing to documentation for users on how to set
	// up and configure a local registry.
	//
	// Tools can use this to nudge users to enable the registry. When possible,
	// the writer should use as permanent a URL as possible to prevent drift
	// (e.g., a version control SHA).
	//
	// When image pushes to a registry host specified in one of the other fields
	// fail, the tool should display this help URL to the user. The help URL
	// should contain instructions on how to diagnose broken or misconfigured
	// registries.
	Help string `yaml:"help,omitempty"`
}

const OperatorLockName = "kogito-serverless-lock"

// IsCurrentOperatorGlobal returns true if the operator is configured to watch all namespaces.
func IsCurrentOperatorGlobal() bool {
	if watchNamespace, envSet := os.LookupEnv(OperatorWatchNamespaceEnvVariable); !envSet || strings.TrimSpace(watchNamespace) == "" {
		return true
	}
	return false
}

// GetOperatorNamespace returns the namespace where the current operator is located (if set).
func GetOperatorNamespace() string {
	if podNamespace, envSet := os.LookupEnv(operatorNamespaceEnvVariable); envSet {
		return podNamespace
	}
	return ""
}

// GetOperatorLockName returns the name of the lock lease that is electing a leader on the particular namepsace.
func GetOperatorLockName(operatorID string) string {
	return fmt.Sprintf("%s-lock", operatorID)
}

// GetActivePlatform returns the currently installed active platform in the local namespace.
func GetActivePlatform(ctx context.Context, c ctrl.Reader, namespace string) (*operatorapi.KogitoServerlessPlatform, error) {
	return GetLocalPlatform(ctx, c, namespace, true)
}

// GetLocalPlatform returns the currently installed platform or any platform existing in local namespace.
func GetLocalPlatform(ctx context.Context, c ctrl.Reader, namespace string, active bool) (*operatorapi.KogitoServerlessPlatform, error) {
	log.Debug("Finding available platforms")

	lst, err := ListPrimaryPlatforms(ctx, c, namespace)
	if err != nil {
		return nil, err
	}

	for _, platform := range lst.Items {
		platform := platform // pin
		if IsActive(&platform) {
			log.Debugf("Found active local build platform %s", platform.Name)
			return &platform, nil
		}
	}

	if !active && len(lst.Items) > 0 {
		// does not require the platform to be active, just return one if present
		res := lst.Items[0]
		log.Debugf("Found local build platform %s", res.Name)
		return &res, nil
	}

	log.Debugf("Not found a local build platform")
	return nil, k8serrors.NewNotFound(operatorapi.Resource("KogitoServerlessPlatform"), DefaultPlatformName)
}

// ListPrimaryPlatforms returns all non-secondary platforms installed in a given namespace (only one will be active).
func ListPrimaryPlatforms(ctx context.Context, c ctrl.Reader, namespace string) (*operatorapi.KogitoServerlessPlatformList, error) {
	lst, err := ListAllPlatforms(ctx, c, namespace)
	if err != nil {
		return nil, err
	}

	filtered := &operatorapi.KogitoServerlessPlatformList{}
	for i := range lst.Items {
		pl := lst.Items[i]
		if !IsSecondary(&pl) {
			filtered.Items = append(filtered.Items, pl)
		}
	}
	return filtered, nil
}

// ListAllPlatforms returns all platforms installed in a given namespace.
func ListAllPlatforms(ctx context.Context, c ctrl.Reader, namespace string) (*operatorapi.KogitoServerlessPlatformList, error) {
	lst := operatorapi.NewKogitoServerlessPlatformList()
	if err := c.List(ctx, &lst, ctrl.InNamespace(namespace)); err != nil {
		return nil, err
	}
	return &lst, nil
}

// IsActive determines if the given platform is being used.
func IsActive(p *operatorapi.KogitoServerlessPlatform) bool {
	return p.Status.Phase != "" && p.Status.Phase != operatorapi.PlatformPhaseDuplicate
}

// IsSecondary determines if the given platform is marked as secondary.
func IsSecondary(p *operatorapi.KogitoServerlessPlatform) bool {
	if l, ok := p.Annotations[metadata.SecondaryPlatformAnnotation]; ok && l == "true" {
		return true
	}
	return false
}

// IsNamespaceLocked tells if the namespace contains a lock indicating that an operator owns it.
func IsNamespaceLocked(ctx context.Context, c ctrl.Reader, namespace string) (bool, error) {
	if namespace == "" {
		return false, nil
	}

	platforms, err := ListPrimaryPlatforms(ctx, c, namespace)
	if err != nil {
		return true, err
	}

	for _, platform := range platforms.Items {
		lease := coordination.Lease{}

		var operatorLockName string
		if platform.Name != "" {
			operatorLockName = GetOperatorLockName(platform.Name)
		} else {
			operatorLockName = OperatorLockName
		}

		if err := c.Get(ctx, ctrl.ObjectKey{Namespace: namespace, Name: operatorLockName}, &lease); err == nil || !k8serrors.IsNotFound(err) {
			return true, err
		}
	}

	return false, nil
}

// IsOperatorAllowedOnNamespace returns true if the current operator is allowed to react on changes in the given namespace.
func IsOperatorAllowedOnNamespace(ctx context.Context, c ctrl.Reader, namespace string) (bool, error) {
	// allow all local operators
	if !IsCurrentOperatorGlobal() {
		return true, nil
	}

	// allow global operators that use a proper operator id
	if utils.OperatorID() != "" {
		return true, nil
	}

	operatorNamespace := GetOperatorNamespace()
	if operatorNamespace == namespace {
		// Global operator is allowed on its own namespace
		return true, nil
	}
	alreadyOwned, err := IsNamespaceLocked(ctx, c, namespace)
	if err != nil {
		return false, err
	}
	return !alreadyOwned, nil
}

// IsOperatorHandler Operators matching the annotation operator id are allowed to reconcile.
// For legacy resources that are missing a proper operator id annotation the default global operator or the local
// operator in this namespace are candidates for reconciliation.
func IsOperatorHandler(object ctrl.Object) bool {
	if object == nil {
		return true
	}
	resourceID := utils.GetOperatorIDAnnotation(object)
	operatorID := utils.OperatorID()

	// allow operator with matching id to handle the resource
	if resourceID == operatorID {
		return true
	}

	// check if we are dealing with resource that is missing a proper operator id annotation
	if resourceID == "" {
		// allow default global operator to handle legacy resources (missing proper operator id annotations)
		if operatorID == DefaultPlatformName {
			return true
		}

		// allow local operators to handle legacy resources (missing proper operator id annotations)
		if !IsCurrentOperatorGlobal() {
			return true
		}
	}

	return false
}

// IsOperatorHandlerConsideringLock uses normal IsOperatorHandler checks and adds additional check for legacy resources
// that are missing a proper operator id annotation. In general two kind of operators race for reconcile these legacy resources.
// The local operator for this namespace and the default global operator instance. Based on the existence of a namespace
// lock the current local operator has precedence. When no lock exists the default global operator should reconcile.
func IsOperatorHandlerConsideringLock(ctx context.Context, c ctrl.Reader, namespace string, object ctrl.Object) bool {
	isHandler := IsOperatorHandler(object)
	if !isHandler {
		return false
	}

	resourceID := utils.GetOperatorIDAnnotation(object)
	// add additional check on resources missing an operator id
	if resourceID == "" {
		operatorNamespace := GetOperatorNamespace()
		if operatorNamespace == namespace {
			// Global operator is allowed on its own namespace
			return true
		}

		if locked, err := IsNamespaceLocked(ctx, c, namespace); err != nil || locked {
			// namespace is locked so local operators do have precedence
			return !IsCurrentOperatorGlobal()
		}
	}

	return true
}
