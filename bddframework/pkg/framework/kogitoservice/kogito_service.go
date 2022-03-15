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

package kogitoservice

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/operator"
	"net/url"
	"os"
)

//Constants ...
const (
	EnvVarKogitoServiceURL = "LOCAL_KOGITO_SERVICE_URL"
	webSocketScheme        = "ws"
	webSocketSecureScheme  = "wss"
	httpScheme             = "http"
)

// ServiceHandler ...
type ServiceHandler interface {
	GetKogitoServiceURL(kogitoService api.KogitoService) string
	GetKogitoServiceEndpoints(instance api.KogitoService, serviceHTTPRouteEnv string, serviceWSRouteEnv string) (endpoints *ServiceEndpoints, err error)
}

type kogitoServiceHandler struct {
	operator.Context
}

// NewKogitoServiceHandler ...
func NewKogitoServiceHandler(context operator.Context) ServiceHandler {
	return &kogitoServiceHandler{
		context,
	}
}

// GetKogitoServiceURl gets the endpoint depending on
// if the envVarKogitoServiceURL is set (for when running
// operator locally). Else, the internal endpoint is
// returned.
func (k *kogitoServiceHandler) GetKogitoServiceURL(kogitoService api.KogitoService) string {
	externalURL := os.Getenv(EnvVarKogitoServiceURL)

	if len(externalURL) > 0 {
		return externalURL
	}
	return k.getKogitoServiceURL(kogitoService)
}

// getKogitoServiceURL provides kogito service URL for given instance name
func (k *kogitoServiceHandler) getKogitoServiceURL(service api.KogitoService) string {
	k.Log.Debug("Creating kogito service instance URL.")
	// resolves to http://servicename.mynamespace for example
	serviceURL := fmt.Sprintf("http://%s.%s", service.GetName(), service.GetNamespace())
	k.Log.Debug("", "kogito service instance URL", serviceURL)
	return serviceURL
}

// GetKogitoServiceEndpoints ...
func (k *kogitoServiceHandler) GetKogitoServiceEndpoints(instance api.KogitoService, serviceHTTPRouteEnv string, serviceWSRouteEnv string) (endpoints *ServiceEndpoints, err error) {
	srvEndpoint := k.GetKogitoServiceURL(instance)
	endpoints = &ServiceEndpoints{
		HTTPRouteEnv: serviceHTTPRouteEnv,
		WSRouteEnv:   serviceWSRouteEnv,
	}
	var srvURL *url.URL
	srvURL, err = url.Parse(srvEndpoint)
	if err != nil {
		k.Log.Error(err, "Failed to parse srv url, set to empty", "srvURL", srvURL)
		return
	}
	endpoints.HTTPRouteURI = srvURL.String()
	if httpScheme == srvURL.Scheme {
		endpoints.WSRouteURI = fmt.Sprintf("%s://%s", webSocketScheme, srvURL.Host)
	} else {
		endpoints.WSRouteURI = fmt.Sprintf("%s://%s", webSocketSecureScheme, srvURL.Host)
	}
	return
}
