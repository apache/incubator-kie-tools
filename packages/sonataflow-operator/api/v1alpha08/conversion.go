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

package v1alpha08

import (
	"context"
	"errors"
	"path"
	"regexp"
	"strings"

	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	controllerruntime "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/yaml"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
)

var namingRegexp = regexp.MustCompile("^[a-z0-9](-?[a-z0-9])*$")
var allowedCharsRegexp = regexp.MustCompile("[^-a-z0-9]")
var startingDashRegexp = regexp.MustCompile("^-+")
var crdVersionRegexp = regexp.MustCompile("v[0-9](alpha|beta)?")

const (
	// see https://kubernetes.io/docs/concepts/overview/working-with-objects/names/
	dash      = "-"
	charLimit = 253
)

// FromCNCFWorkflow converts the given CNCF Serverless Workflow instance in a new SonataFlow Custom Resource.
func FromCNCFWorkflow(cncfWorkflow *cncfmodel.Workflow, context context.Context) (*SonataFlow, error) {
	if cncfWorkflow == nil {
		return nil, errors.New("CNCF Workflow is nil")
	}
	workflowCR := &SonataFlow{
		ObjectMeta: metav1.ObjectMeta{
			Name: extractName(cncfWorkflow),
			Annotations: map[string]string{
				metadata.ExpressionLang: string(cncfWorkflow.ExpressionLang),
				metadata.Version:        cncfWorkflow.Version,
				metadata.Description:    cncfWorkflow.Description,
			},
		},
	}
	workflowBytes, err := yaml.Marshal(cncfWorkflow)
	if err != nil {
		return nil, err
	}
	workflowCRFlow := &Flow{}
	if err = yaml.Unmarshal(workflowBytes, workflowCRFlow); err != nil {
		return nil, err
	}
	workflowCR.Spec.Flow = *workflowCRFlow

	s, _ := SchemeBuilder.Build()
	gvks, _, err := s.ObjectKinds(workflowCR)
	if err != nil {
		return nil, err
	}
	for _, gvk := range gvks {
		if len(gvk.Version) == 0 {
			continue
		}
		workflowCR.SetGroupVersionKind(gvk)
	}
	warnIfSpecVersionNotSupported(cncfWorkflow, context)

	return workflowCR, nil
}

// ToCNCFWorkflow converts a SonataFlow object to a Workflow one in order to be able to convert it to a YAML/Json
func ToCNCFWorkflow(workflowCR *SonataFlow, context context.Context) (*cncfmodel.Workflow, error) {
	if workflowCR == nil {
		return nil, errors.New("SonataFlow is nil")
	}
	cncfWorkflow := &cncfmodel.Workflow{}

	workflowBytes, err := yaml.Marshal(workflowCR.Spec.Flow)
	if err != nil {
		return nil, err
	}
	if err = yaml.Unmarshal(workflowBytes, cncfWorkflow); err != nil {
		return nil, err
	}

	cncfWorkflow.ID = workflowCR.ObjectMeta.Name
	if key, ok := workflowCR.ObjectMeta.Annotations[metadata.Key]; ok {
		cncfWorkflow.Key = key
	}
	if name, ok := workflowCR.ObjectMeta.Annotations[metadata.Name]; ok {
		cncfWorkflow.Name = name
	}
	if description, ok := workflowCR.ObjectMeta.Annotations[metadata.Description]; ok {
		cncfWorkflow.Description = description
	}
	if version, ok := workflowCR.ObjectMeta.Annotations[metadata.Version]; ok {
		cncfWorkflow.Version = version
	}
	cncfWorkflow.SpecVersion = extractSpecVersion(workflowCR)
	cncfWorkflow.ExpressionLang = cncfmodel.ExpressionLangType(extractExpressionLang(workflowCR.ObjectMeta.Annotations))

	warnIfSpecVersionNotSupported(cncfWorkflow, context)

	return cncfWorkflow, nil
}

// warnIfSpecVersionNotSupported simple check if the version is not supported by the operator.
// Clearly this will be reviewed once we support 0.9.
func warnIfSpecVersionNotSupported(workflow *cncfmodel.Workflow, context context.Context) {
	// simple guard to avoid polluting user's log.
	if len(workflow.SpecVersion) == 0 {
		workflow.SpecVersion = metadata.SpecVersion
		return
	}
	if metadata.SpecVersion != workflow.SpecVersion {
		controllerruntime.LoggerFrom(context).Info("SpecVersion not supported", "Workflow SpecVersion", workflow.Version)
	}
}

func extractExpressionLang(annotations map[string]string) string {
	expressionLang := annotations[metadata.ExpressionLang]
	if expressionLang != "" {
		return expressionLang
	}
	return metadata.DefaultExpressionLang
}

// Function to extract from the apiVersion the ServerlessWorkflow schema version
// For example given SonataFlow APIVersion, we would like to extract 0.8
func extractSpecVersion(workflowCR *SonataFlow) string {
	schemaVersion := path.Base(workflowCR.APIVersion)
	if len(schemaVersion) == 0 {
		return metadata.SpecVersion
	}
	schemaVersion = crdVersionRegexp.ReplaceAllString(schemaVersion, "")
	// we only support major minor from the spec
	return schemaVersion[0:1] + "." + "" + schemaVersion[1:]
}

func extractName(workflow *cncfmodel.Workflow) string {
	if len(workflow.ID) > 0 {
		return sanitizeNaming(workflow.ID)
	}
	if len(workflow.Key) > 0 {
		return sanitizeNaming(workflow.Key)
	}
	if len(workflow.Name) > 0 {
		return sanitizeNaming(workflow.Name)
	}
	return ""
}

func sanitizeNaming(name string) string {
	if len(name) == 0 || namingRegexp.MatchString(name) {
		return name
	}
	sanitized := startingDashRegexp.ReplaceAllString(allowedCharsRegexp.ReplaceAllString(strings.TrimSpace(strings.ToLower(name)), dash), "")
	if len(sanitized) > charLimit {
		return sanitized[:charLimit]
	}
	return sanitized
}
