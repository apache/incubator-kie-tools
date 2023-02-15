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

package utils

import (
	"bytes"
	"context"
	"encoding/gob"
	"encoding/json"
	"errors"

	"k8s.io/apimachinery/pkg/util/runtime"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const defaultImageTag = ":latest"

// GetJSONWorkflow return a Kogito compliant JSON format workflow as bytearray give a specific workflow CR
func GetJSONWorkflow(workflowCR *operatorapi.KogitoServerlessWorkflow, ctx context.Context) ([]byte, error) {
	logger := ctrllog.FromContext(ctx)
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

// SameOrMatch return true if the build it is related to the workflow, false otherwise
func SameOrMatch(build *operatorapi.KogitoServerlessBuild, workflow *operatorapi.KogitoServerlessWorkflow) (bool, error) {
	if build.Name == workflow.Name {
		if build.Namespace == workflow.Namespace {
			return true, nil
		}
		return false, errors.New("build & Workflow namespaces are not matching")
	}
	return false, errors.New("build & Workflow names are not matching")
}

// GetWorkflowSpecHash comute a hash of the workflow definition (hash), useful to compare 2 different definitions
func GetWorkflowSpecHash(s operatorapi.KogitoServerlessWorkflowSpec) []byte {
	var b bytes.Buffer
	runtime.Must(gob.NewEncoder(&b).Encode(s))
	return b.Bytes()
}

// GetWorkflowImageTag retrieve the tag for the image based on the Workflow based annotation, :latest otherwise
func GetWorkflowImageTag(v *operatorapi.KogitoServerlessWorkflow) string {
	tag := v.Annotations[metadata.Version]
	if tag != "" {
		return ":" + tag
	}
	return defaultImageTag
}
