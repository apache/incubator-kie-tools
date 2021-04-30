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
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"regexp"
	"strings"

	grafanav1 "github.com/integr8ly/grafana-operator/v3/pkg/apis/integreatly/v1alpha1"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// dashboardPath which the dashboards are fetched
	dashboardsPath = "/monitoring/dashboards/"
)

// GrafanaDashboardManager ...
type GrafanaDashboardManager interface {
	ConfigureGrafanaDashboards(kogitoService api.KogitoService) error
}

type grafanaDashboardManager struct {
	operator.Context
}

// GrafanaDashboard is a structure that contains the fetched dashboards
type GrafanaDashboard struct {
	Name             string
	RawJSONDashboard string
}

// NewGrafanaDashboardManager ...
func NewGrafanaDashboardManager(context operator.Context) GrafanaDashboardManager {
	return &grafanaDashboardManager{
		context,
	}
}

var (
	dashboardNameRegex = regexp.MustCompile("[^a-zA-Z0-9-_]+")
)

func (d *grafanaDashboardManager) ConfigureGrafanaDashboards(kogitoService api.KogitoService) error {
	grafanaAvailable := d.isGrafanaAvailable()
	if !grafanaAvailable {
		d.Log.Debug("grafana operator not available in namespace")
		return nil
	}

	dashboards, err := d.fetchGrafanaDashboards(kogitoService)
	if err != nil {
		return err
	}

	err = d.deployGrafanaDashboards(dashboards, kogitoService)
	return err
}

// isPrometheusAvailable checks if Prometheus CRD is available in the cluster
func (d *grafanaDashboardManager) isGrafanaAvailable() bool {
	return d.Client.HasServerGroup(grafanav1.SchemeGroupVersion.Group)
}

func (d *grafanaDashboardManager) fetchGrafanaDashboards(instance api.KogitoService) ([]GrafanaDashboard, error) {
	deploymentHandler := NewDeploymentHandler(d.Context)
	available, err := deploymentHandler.IsDeploymentAvailable(instance)
	if err != nil {
		return nil, err
	}
	if !available {
		d.Log.Debug("Deployment not yet available")
		return nil, nil
	}

	kogitoServiceHandler := NewKogitoServiceHandler(d.Context)
	svcURL := kogitoServiceHandler.GetKogitoServiceEndpoint(instance)
	dashboardNames, err := d.fetchGrafanaDashboardNamesForURL(svcURL)
	if err != nil {
		return nil, err
	}

	return d.fetchDashboards(svcURL, dashboardNames)
}

func (d *grafanaDashboardManager) fetchGrafanaDashboardNamesForURL(serverURL string) ([]string, error) {
	dashboardsURL := fmt.Sprintf("%s%s%s", serverURL, dashboardsPath, "list.json")
	resp, err := http.Get(dashboardsURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if resp.StatusCode == http.StatusNotFound {
		d.Log.Debug("Dashboard list not found, the monitoring addon is disabled on the service. There are no dashboards to deploy.")
		return nil, nil
	}
	if resp.StatusCode != http.StatusOK {
		return nil, errorForServiceNotReachable(resp.StatusCode, dashboardsURL, "GET")
	}
	var dashboardNames []string
	if err := json.NewDecoder(resp.Body).Decode(&dashboardNames); err != nil {
		return nil, fmt.Errorf("failed to decode response from %s into dashboard names", dashboardsURL)
	}

	return dashboardNames, nil
}

func (d *grafanaDashboardManager) fetchDashboards(serverURL string, dashboardNames []string) ([]GrafanaDashboard, error) {
	var dashboards []GrafanaDashboard
	for _, name := range dashboardNames {
		dashboardURL := fmt.Sprintf("%s%s%s", serverURL, dashboardsPath, name)
		if dashboard, err := d.fetchDashboard(name, dashboardURL); err != nil {
			return nil, err
		} else if dashboard != nil {
			dashboards = append(dashboards, *dashboard)
		}
	}
	return dashboards, nil
}

// we create a separate function to be able to `defer` the HTTP response after the function call.
func (d *grafanaDashboardManager) fetchDashboard(name, dashboardURL string) (*GrafanaDashboard, error) {
	resp, err := http.Get(dashboardURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if resp.StatusCode == http.StatusNotFound {
		d.Log.Debug("Dashboard not found, ignoring the resource.", "dashboard name", name)
		return nil, nil
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("Received NOT expected status code %d while making GET request to %s ", resp.StatusCode, dashboardURL)
	}
	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		d.Log.Error(err, "Error in reading")
		return nil, err
	}
	return &GrafanaDashboard{Name: name, RawJSONDashboard: string(bodyBytes)}, nil
}

func (d *grafanaDashboardManager) deployGrafanaDashboards(dashboards []GrafanaDashboard, kogitoService api.KogitoService) error {
	for _, dashboard := range dashboards {
		resourceName := sanitizeDashboardName(dashboard.Name)
		dashboardDefinition := &grafanav1.GrafanaDashboard{
			ObjectMeta: metav1.ObjectMeta{
				Name:      resourceName,
				Namespace: kogitoService.GetNamespace(),
				Labels: map[string]string{
					framework.LabelAppKey: kogitoService.GetName(),
				},
			},
			Spec: grafanav1.GrafanaDashboardSpec{
				Json: dashboard.RawJSONDashboard,
				Name: dashboard.Name,
			},
		}
		if err := kubernetes.ResourceC(d.Client).CreateIfNotExistsForOwner(dashboardDefinition, kogitoService, d.Scheme); err != nil {
			d.Log.Error(err, "Error occurs while creating dashboard, not going to reconcile the resource", "dashboard name", dashboard.Name)
			return err
		}
	}
	return nil
}

func sanitizeDashboardName(name string) string {
	name = strings.ReplaceAll(strings.ToLower(name), ".json", "")
	return dashboardNameRegex.ReplaceAllString(name, "")
}
