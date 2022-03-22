// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package framework

import (
	"strconv"
	"strings"

	appsv1 "github.com/openshift/api/apps/v1"
	dockerv10 "github.com/openshift/api/image/docker10"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

const (
	// DefaultPortName is the default protocol exposed by inner services specified in image metadata
	DefaultPortName = "http"
	// DefaultExposedPort TODO: found an agnostic API to fetch the ImageRaw from the docker image and read this value from there.
	DefaultExposedPort = 8080
	// LabelKeyOrgKie is the label key for KIE metadata
	LabelKeyOrgKie = "org.kie" + labelNamespaceSep
	// LabelKeyOrgKiePersistence is the label key for Persistence metadata
	LabelKeyOrgKiePersistence = LabelKeyOrgKie + "persistence"
	// LabelKeyOrgKiePersistenceRequired is the label key to check if persistence is enabled or not
	LabelKeyOrgKiePersistenceRequired = LabelKeyOrgKiePersistence + labelNamespaceSep + "required"

	// LabelKeyPrometheus is the label key for Prometheus metadata
	LabelKeyPrometheus = "prometheus.io"
	// LabelPrometheusScrape is the label key for prometheus scrape configuration
	LabelPrometheusScrape = LabelKeyPrometheus + "/scrape"
	// LabelPrometheusPath is the label key for prometheus metrics path
	LabelPrometheusPath = LabelKeyPrometheus + "/path"
	// LabelPrometheusPort is the label key for prometheus metrics port
	LabelPrometheusPort = LabelKeyPrometheus + "/port"
	// LabelPrometheusScheme is the label key for Prometheus metrics endpoint scheme
	LabelPrometheusScheme = LabelKeyPrometheus + "/scheme"

	labelNamespaceSep               = "/"
	dockerLabelServicesSep, portSep = ",", ":"
	// imageLabelForExposeServices is the label defined in images to identify ports that need to be exposed by the container
	imageLabelForExposeServices = "io.openshift.expose-services"
	portFormatWrongMessage      = "Service on " + imageLabelForExposeServices + " label in wrong format. Won't be possible to expose Services for this application. Should be PORT_NUMBER:PROTOCOL. e.g. 8080:http"
)

var defaultProbe = &corev1.Probe{
	TimeoutSeconds:   int32(1),
	PeriodSeconds:    int32(10),
	SuccessThreshold: int32(1),
	FailureThreshold: int32(3),
}

func dockerImageHasLabels(dockerImage *dockerv10.DockerImage) bool {
	if dockerImage == nil || dockerImage.Config == nil || dockerImage.Config.Labels == nil {
		return false
	}
	return true
}

// MergeImageMetadataWithDeploymentConfig retrieves org.kie and prometheus.io labels from DockerImage and adds them to the DeploymentConfig
// returns true if any changes occurred in the deploymentConfig based on the dockerImage labels
func MergeImageMetadataWithDeploymentConfig(dc *appsv1.DeploymentConfig, dockerImage *dockerv10.DockerImage) bool {
	if !dockerImageHasLabels(dockerImage) {
		return false
	}

	log.Debug("Preparing to read docker labels and add them to the Deployment", "labels", dockerImage.Config.Labels)

	if dc.Spec.Template.Annotations == nil {
		dc.Spec.Template.Annotations = map[string]string{}
	}
	if dc.Labels == nil {
		dc.Labels = map[string]string{}
	}
	if dc.Spec.Selector == nil {
		dc.Spec.Selector = map[string]string{}
	}
	if dc.Spec.Template.Labels == nil {
		dc.Spec.Template.Labels = map[string]string{}
	}

	added := false
	for key, value := range dockerImage.Config.Labels {
		if strings.Contains(key, LabelKeyOrgKie) && !strings.Contains(key, LabelKeyOrgKiePersistence) {
			splitedKey := strings.Split(key, labelNamespaceSep)
			// we're only interested on keys like org.kie/something
			if len(splitedKey) > 1 {
				importedKey := strings.Join(splitedKey[1:], labelNamespaceSep)
				if !added {
					_, lblPresent := dc.Labels[importedKey]
					_, selectorPresent := dc.Spec.Selector[importedKey]
					_, podLblPresent := dc.Spec.Template.Labels[importedKey]

					added = !(lblPresent && selectorPresent && podLblPresent)
				}
				dc.Labels[importedKey] = value
				dc.Spec.Selector[importedKey] = value
				dc.Spec.Template.Labels[importedKey] = value
			}
		} else if strings.Contains(key, LabelKeyPrometheus) {
			if !added {
				_, present := dc.Spec.Template.Annotations[key]
				added = !present
			}

			dc.Spec.Template.Annotations[key] = value
		}
	}

	return added
}

// DiscoverPortsAndProbesFromImage set Ports and Probes based on labels set on the DockerImage of this DeploymentConfig
func DiscoverPortsAndProbesFromImage(dc *appsv1.DeploymentConfig, dockerImage *dockerv10.DockerImage) {
	if !dockerImageHasLabels(dockerImage) {
		return
	}
	var containerPorts []corev1.ContainerPort
	var nonSecureProbe *corev1.Probe
	for key, value := range dockerImage.Config.Labels {
		if key == imageLabelForExposeServices {
			services := strings.Split(value, dockerLabelServicesSep)
			for _, service := range services {
				ports := strings.Split(service, portSep)
				if len(ports) == 0 {
					log.Warn(portFormatWrongMessage, "service name", service)
					continue
				}
				portNumber, err := strconv.Atoi(strings.Split(service, portSep)[0])
				if err != nil {
					log.Warn(portFormatWrongMessage, "service name", service)
					continue
				}
				portName := ports[1]
				containerPorts = append(containerPorts, corev1.ContainerPort{Name: portName, ContainerPort: int32(portNumber), Protocol: corev1.ProtocolTCP})
				// we have at least one service exported using default HTTP protocols, let's used as a probe!
				if portName == DefaultPortName {
					nonSecureProbe = defaultProbe
					nonSecureProbe.Handler.TCPSocket = &corev1.TCPSocketAction{Port: intstr.FromInt(portNumber)}
				}
			}
			break
		}
	}
	// set the ports we've found
	if len(containerPorts) != 0 {
		dc.Spec.Template.Spec.Containers[0].Ports = containerPorts
		if nonSecureProbe != nil {
			dc.Spec.Template.Spec.Containers[0].LivenessProbe = nonSecureProbe
			dc.Spec.Template.Spec.Containers[0].ReadinessProbe = nonSecureProbe
		}
	}
}

// ExtractPrometheusConfigurationFromImage retrieves prometheus configurations from the prometheus.io labels of the dockerImage
func ExtractPrometheusConfigurationFromImage(dockerImage *dockerv10.DockerImage) (scrape bool, scheme string, path string, port *intstr.IntOrString, err error) {
	if !dockerImageHasLabels(dockerImage) {
		return
	}

	if scrapeVal, has := dockerImage.Config.Labels[LabelPrometheusScrape]; has {
		scrape, _ = strconv.ParseBool(scrapeVal)
	}

	scheme = dockerImage.Config.Labels[LabelPrometheusScheme]

	path = dockerImage.Config.Labels[LabelPrometheusPath]

	if portVal, has := dockerImage.Config.Labels[LabelPrometheusPort]; has {
		portInt, err := strconv.ParseInt(portVal, 10, 32)
		if err != nil {
			return false, "", "", nil, err
		}
		portV := intstr.FromInt(int(portInt))
		port = &portV
	}

	return
}

// IsPersistenceEnabled verifies if the image has labels indicating that persistence is enabled
func IsPersistenceEnabled(dockerImage *dockerv10.DockerImage) (enabled bool) {
	if !dockerImageHasLabels(dockerImage) {
		return false
	}
	var err error
	if enabled, err = strconv.ParseBool(dockerImage.Config.Labels[LabelKeyOrgKiePersistenceRequired]); err != nil {
		return false
	}
	return enabled
}
