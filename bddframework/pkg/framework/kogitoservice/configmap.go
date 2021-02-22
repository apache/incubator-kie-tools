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

package kogitoservice

import (
	"crypto/md5"
	"fmt"
	"github.com/imdario/mergo"
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sort"
	"strings"
)

const (
	appPropConfigMapSuffix = "-properties"
	defaultAppPropContent  = ""
	// AppPropContentHashKey is the annotation key for the content hash of application.properties
	AppPropContentHashKey = "appPropContentHash"
	// ConfigMapApplicationPropertyKey is the file name used as a key for ConfigMaps mounted in Kogito services deployments
	ConfigMapApplicationPropertyKey = "application.properties"
	appPropConcatPattern            = "%s\n%s=%s"
)

// AppPropsConfigMapHandler ...
type AppPropsConfigMapHandler interface {
	GetAppPropConfigMapContentHash(service api.KogitoService, appProps map[string]string) (string, *corev1.ConfigMap, error)
}

type appPropsConfigMapHandler struct {
	*operator.Context
}

// NewAppPropsConfigMapHandler ...
func NewAppPropsConfigMapHandler(context *operator.Context) AppPropsConfigMapHandler {
	return &appPropsConfigMapHandler{
		context,
	}
}

// GetAppPropConfigMapContentHash calculates the hash of the application.properties contents in the ConfigMap
// If the ConfigMap doesn't exist, create a new one and return it.
func (c *appPropsConfigMapHandler) GetAppPropConfigMapContentHash(service api.KogitoService, appProps map[string]string) (string, *corev1.ConfigMap, error) {
	configMapName := getAppPropConfigMapName(service)
	configMap := &corev1.ConfigMap{ObjectMeta: metav1.ObjectMeta{Name: configMapName, Namespace: service.GetNamespace()}}

	exist, err := kubernetes.ResourceC(c.Client).Fetch(configMap)
	if err != nil {
		return "", nil, err
	}

	appPropsToApply := getAppPropsFromConfigMap(configMap, exist)
	if err = mergo.Merge(&appPropsToApply, appProps, mergo.WithOverride); err != nil {
		return "", nil, err
	}

	appPropContent := defaultAppPropContent
	if len(appPropsToApply) > 0 {
		var keys []string
		for key := range appPropsToApply {
			keys = append(keys, key)
		}
		sort.Strings(keys)
		for _, key := range keys {
			appPropContent = fmt.Sprintf(appPropConcatPattern, appPropContent, key, appPropsToApply[key])
		}
	}
	configMap.Data = map[string]string{
		ConfigMapApplicationPropertyKey: appPropContent,
	}

	contentHash := fmt.Sprintf("%x", md5.Sum([]byte(configMap.Data[ConfigMapApplicationPropertyKey])))

	return contentHash, configMap, nil
}

// getAppPropConfigMapName gets the name of the config map for application.properties
func getAppPropConfigMapName(service api.KogitoService) string {
	if len(service.GetSpec().GetPropertiesConfigMap()) > 0 {
		return service.GetSpec().GetPropertiesConfigMap()
	}
	return service.GetName() + appPropConfigMapSuffix
}

// getAppPropsFromConfigMap extracts the application properties from the ConfigMap to a string map
func getAppPropsFromConfigMap(configMap *corev1.ConfigMap, exist bool) map[string]string {
	appProps := map[string]string{}
	if exist {
		if data, ok := configMap.Data[ConfigMapApplicationPropertyKey]; ok {
			props := strings.Split(data, "\n")
			for _, p := range props {
				ps := strings.Split(p, "=")
				if len(ps) > 1 {
					appProps[strings.TrimSpace(ps[0])] = strings.TrimSpace(ps[1])
				}
			}
		}
	}
	return appProps
}
