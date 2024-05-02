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

package client

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"

	rbac "k8s.io/api/rbac/v1"
	"k8s.io/apimachinery/pkg/runtime"
	controllerruntime "sigs.k8s.io/controller-runtime"
	logger "sigs.k8s.io/controller-runtime/pkg/log"

	frameworklogger "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/logger"

	appsv1 "github.com/openshift/client-go/apps/clientset/versioned/typed/apps/v1"
	corev1 "k8s.io/api/core/v1"
	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	apimeta "k8s.io/apimachinery/pkg/api/meta"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/client-go/discovery"
	"k8s.io/client-go/kubernetes"
	restclient "k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"sigs.k8s.io/controller-runtime/pkg/client"
	controllercliconfig "sigs.k8s.io/controller-runtime/pkg/client/config"

	buildv1 "github.com/openshift/client-go/build/clientset/versioned/typed/build/v1"
	imagev1 "github.com/openshift/client-go/image/clientset/versioned/typed/image/v1"
)

var (
	log = frameworklogger.Logger{Logger: logger.Log.WithName("client_api")}
)

const (
	// OpenShiftGroupName name included in OpenShift APIs
	OpenShiftGroupName = "openshift.io"
)

// Client wraps clients functions from controller-runtime, Kube and OpenShift cli for generic API calls to the cluster
type Client struct {
	// ControlCli is a reference for the controller-runtime client, normally built by a Manager inside the controller context.
	ControlCli             client.Client
	BuildCli               buildv1.BuildV1Interface
	ImageCli               imagev1.ImageV1Interface
	Discovery              discovery.DiscoveryInterface
	DeploymentCli          appsv1.AppsV1Interface
	KubernetesExtensionCli kubernetes.Interface
}

// NewForConsole will create a brand new client using the local machine
func NewForConsole(scheme *runtime.Scheme) *Client {
	client, err := NewClientBuilder(scheme).WithBuildClient().WithDiscoveryClient().Build()
	if err != nil {
		panic(err)
	}
	return client
}

// NewForController creates a new client based on the rest config and the controller client created by Operator SDK
// Panic if something goes wrong
func NewForController(manager controllerruntime.Manager) *Client {
	newClient, err := NewClientBuilder(manager.GetScheme()).
		WithAllClients().
		UseConfig(manager.GetConfig()).
		UseControllerClient(manager.GetClient()).
		Build()
	if err != nil {
		panic(err)
	}
	return newClient
}

// IsOpenshift detects if the application is running on OpenShift or not
func (c *Client) IsOpenshift() bool {
	return c.HasServerGroup(OpenShiftGroupName)
}

// HasServerGroup detects if the given api group is supported by the server
func (c *Client) HasServerGroup(groupName string) bool {
	if c.Discovery != nil {
		groups, err := c.Discovery.ServerGroups()
		if err != nil {
			log.Warn("Impossible to get server groups using discovery API", "error", err)
			return false
		}
		for _, group := range groups.Groups {
			if strings.Contains(group.Name, groupName) {
				return true
			}
		}
		return false
	}
	log.Warn("Tried to discover the platform, but no discovery API is available")
	return false
}

func newKubeClient(config *restclient.Config, scheme *runtime.Scheme, useDynamicRestMapper bool) (client.Client, error) {
	log.Debug("Creating a new core client for kube connection")
	var options client.Options
	if useDynamicRestMapper {
		options = newControllerCliOptionsWithDynamicMapper(scheme)
	} else {
		options = newControllerCliOptions(scheme)
	}
	controlCli, err := client.New(config, options)
	if err != nil {
		return nil, err
	}
	return controlCli, nil
}

func buildKubeConnectionConfig() (*restclient.Config, error) {
	return controllercliconfig.GetConfig()
}

// GetKubeConfigFile gets the .kubeconfig file.
// Never returns an empty string, fallback to default path if not present in the known locations
func GetKubeConfigFile() string {
	filename := clientcmd.NewDefaultPathOptions().GetDefaultFilename()
	// make sure the path to the file exists
	if _, err := os.Stat(filename); os.IsNotExist(err) {
		dirName := filepath.Dir(filename)
		if err := os.MkdirAll(dirName, os.ModePerm); err != nil {
			panic(fmt.Errorf("Error while trying to create kube config directories %s: %s ", filename, err))
		}
	} else if err != nil {
		panic(fmt.Errorf("Error while trying to access the kube config file %s: %s ", filename, err))
	}
	if file, err := os.OpenFile(filename, os.O_RDONLY|os.O_CREATE, 0600); err != nil {
		panic(fmt.Errorf("Error while trying to access the kube config file %s: %s ", filename, err))
	} else {
		defer func() {
			if file != nil {
				if err := file.Close(); err != nil {
					panic(fmt.Errorf("Error closing kube config file %s: %s ", filename, err))
				}
			}
		}()
		if fileInfo, err := file.Stat(); err != nil {
			panic(fmt.Errorf("Error while trying to access the kube config file %s: %s ", filename, err))
		} else if fileInfo.Size() == 0 {
			log.Warn("Kubernetes local configuration is empty.", "filename", filename)
			log.Warn("Make sure to login to your cluster with oc/kubectl before using this tool")
		}
	}

	return filename
}

// restScope implementation
type restScope struct {
	name apimeta.RESTScopeName
}

func (r *restScope) Name() apimeta.RESTScopeName {
	return r.name
}

// newControllerCliOptions creates the mapper and schema options for the inner fallback cli. If set to defaults, the Controller Cli will try
// to discover the mapper by itself by querying the API, which can take too much time. Here we're setting this mapper manually.
// So it's need to keep adding them or find some kind of auto register in the kube api/apimachinery
func newControllerCliOptions(scheme *runtime.Scheme) client.Options {
	options := client.Options{}
	gvks, err := getGVKsFromAddToScheme(scheme)
	if err != nil {
		log.Error(err, "Error while creating SchemeBuilder for Kubernetes client")
		panic(err)
	}
	mapper := apimeta.NewDefaultRESTMapper([]schema.GroupVersion{})
	for _, gvk := range gvks {
		// namespaced resources
		if (gvk.GroupVersion() == corev1.SchemeGroupVersion && gvk.Kind == "Namespace") ||
			(gvk.GroupVersion() == apiextensionsv1.SchemeGroupVersion && gvk.Kind == "CustomResourceDefinition") ||
			(gvk.GroupVersion() == rbac.SchemeGroupVersion && gvk.Kind == "ClusterRole") ||
			(gvk.GroupVersion() == rbac.SchemeGroupVersion && gvk.Kind == "ClusterRoleBinding") {
			mapper.Add(gvk, &restScope{name: apimeta.RESTScopeNameRoot})
		} else { // everything else
			mapper.Add(gvk, &restScope{name: apimeta.RESTScopeNameNamespace})
		}
	}
	options.Scheme = scheme
	options.Mapper = mapper
	return options
}

func newControllerCliOptionsWithDynamicMapper(scheme *runtime.Scheme) client.Options {
	return client.Options{
		Scheme: scheme,
	}
}

// getGVKsFromAddToScheme takes in the runtime scheme and filters out all generic apimachinery meta types.
// It returns just the GVK specific to this scheme.
func getGVKsFromAddToScheme(s *runtime.Scheme) ([]schema.GroupVersionKind, error) {
	schemeAllKnownTypes := s.AllKnownTypes()
	var ownGVKs []schema.GroupVersionKind
	for gvk := range schemeAllKnownTypes {
		if !isKubeMetaKind(gvk.Kind) {
			ownGVKs = append(ownGVKs, gvk)
		}
	}

	return ownGVKs, nil
}

func isKubeMetaKind(kind string) bool {
	if strings.HasSuffix(kind, "List") ||
		kind == "PatchOptions" ||
		kind == "GetOptions" ||
		kind == "DeleteOptions" ||
		kind == "ExportOptions" ||
		kind == "APIVersions" ||
		kind == "APIGroupList" ||
		kind == "APIResourceList" ||
		kind == "UpdateOptions" ||
		kind == "CreateOptions" ||
		kind == "Status" ||
		kind == "WatchEvent" ||
		kind == "ListOptions" ||
		kind == "APIGroup" {
		return true
	}

	return false
}
