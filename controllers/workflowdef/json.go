// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package workflowdef

import (
	"context"
	"encoding/json"
	"path"
	"strings"

	"github.com/serverlessworkflow/sdk-go/v2/model"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	"github.com/kiegroup/kogito-serverless-operator/utils"

	"errors"

	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// GetJSONWorkflow return a Kogito compliant JSON format workflow as bytearray give a specific workflow CR
func GetJSONWorkflow(workflowCR *operatorapi.KogitoServerlessWorkflow, ctx context.Context) ([]byte, error) {
	logger := ctrllog.FromContext(ctx)
	// apply workflow metadata
	workflow, err := ToCNCFWorkflow(ctx, workflowCR)
	if err != nil {
		logger.Error(err, "Failed converting KogitoServerlessWorkflow into Workflow")
		return nil, err
	}
	jsonWorkflow, err := json.Marshal(workflow)
	if err != nil {
		logger.Error(err, "Failed converting KogitoServerlessWorkflow into JSON")
		return nil, err
	}
	return jsonWorkflow, nil
}

// ToCNCFWorkflow converts a KogitoServerlessWorkflow object to a model.Workflow one in order to be able to convert it to a YAML/Json
func ToCNCFWorkflow(ctx context.Context, workflowCR *operatorapi.KogitoServerlessWorkflow) (*model.Workflow, error) {
	if workflowCR != nil {
		logger := ctrllog.FromContext(ctx)

		workflowCR.Spec.Flow.ID = workflowCR.ObjectMeta.Name
		workflowCR.Spec.Flow.Key = workflowCR.ObjectMeta.Annotations[metadata.Key]
		workflowCR.Spec.Flow.Name = workflowCR.ObjectMeta.Name
		workflowCR.Spec.Flow.Description = workflowCR.ObjectMeta.Annotations[metadata.Description]
		workflowCR.Spec.Flow.Version = workflowCR.ObjectMeta.Annotations[metadata.Version]
		workflowCR.Spec.Flow.SpecVersion = extractSchemaVersion(workflowCR.APIVersion)
		workflowCR.Spec.Flow.ExpressionLang = model.ExpressionLangType(extractExpressionLang(workflowCR.ObjectMeta.Annotations))

		logger.V(utils.DebugV).Info("Created new Base Workflow with name", "name", workflowCR.Spec.Flow.Name)
		return &workflowCR.Spec.Flow, nil
	}
	return nil, errors.New("kogitoServerlessWorkflow is nil")
}

func extractExpressionLang(annotations map[string]string) string {
	expressionLang := annotations[metadata.ExpressionLang]
	if expressionLang != "" {
		return expressionLang
	}
	return metadata.DefaultExpressionLang
}

// Function to extract from the apiVersion the ServerlessWorkflow schema version
// For example given sw.kogito.kie.org/operatorapi we would like to extract v0.8
func extractSchemaVersion(version string) string {
	schemaVersion := path.Base(version)
	strings.Replace(schemaVersion, "v0", "v0.", 1)
	return schemaVersion
}
