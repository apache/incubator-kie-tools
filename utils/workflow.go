/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils

import (
	"bytes"
	"context"
	"encoding/gob"
	"encoding/json"
	"errors"
	apiv08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/constants"
	"github.com/kiegroup/kogito-serverless-operator/converters"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
)

// GetWorkflowFromCR return a Kogito compliant workflow as bytearray give a specific workflow CR
func GetWorkflowFromCR(workflowCR *apiv08.KogitoServerlessWorkflow, ctx context.Context) ([]byte, error) {
	log := ctrllog.FromContext(ctx)
	converter := converters.NewKogitoServerlessWorkflowConverter(ctx)
	workflow, err := converter.ToCNCFWorkflow(workflowCR)
	if err != nil {
		log.Error(err, "Failed converting KogitoServerlessWorkflow into Workflow")
		return nil, err
	}
	jsonWorkflow, err := json.Marshal(workflow)
	if err != nil {
		log.Error(err, "Failed converting KogitoServerlessWorkflow into JSON")
		return nil, err
	}
	return jsonWorkflow, nil
}

// SameOrMatch return true if the build it is related to the workflow, false otherwise
func SameOrMatch(build *apiv08.KogitoServerlessBuild, workflow *apiv08.KogitoServerlessWorkflow) (bool, error) {
	if build.Name == workflow.Name {
		if build.Namespace == workflow.Namespace {
			return true, nil
		}
		return false, errors.New("Build & Workflow namespaces are not matching")
	}
	return false, errors.New("Build & Workflow names are not matching")
}

// GetWorkflowSpecHash comute a hash of the workflow definition (hash), useful to compare 2 different definitions
func GetWorkflowSpecHash(s apiv08.KogitoServerlessWorkflowSpec) []byte {
	var b bytes.Buffer
	gob.NewEncoder(&b).Encode(s)
	return b.Bytes()
}

// GetWorkflowImageTag retrieve the tag for the image based on the Workflow based annotation, :latest otherwise
func GetWorkflowImageTag(v *apiv08.KogitoServerlessWorkflow) string {
	tag := v.Annotations[constants.WorkflowMetadataKeys()("version")]
	if tag != "" {
		return ":" + tag
	}
	return constants.DEFAULT_IMAGES_TAG
}
