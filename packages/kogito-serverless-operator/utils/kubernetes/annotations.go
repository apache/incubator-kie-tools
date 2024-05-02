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

package kubernetes

import (
	"context"
	"strconv"

	"k8s.io/klog/v2"

	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
)

func getWorkflow(namespace string, name string, c client.Client, ctx context.Context) *operatorapi.SonataFlow {
	serverlessWorkflowType := &operatorapi.SonataFlow{}
	serverlessWorkflowType.Namespace = namespace
	serverlessWorkflowType.Name = name
	serverlessWorkflow := &operatorapi.SonataFlow{}
	if err := c.Get(ctx, client.ObjectKeyFromObject(serverlessWorkflowType), serverlessWorkflow); err != nil {
		klog.V(log.E).ErrorS(err, "unable to retrieve SonataFlow definition")
	}
	return serverlessWorkflow
}

func GetLastGeneration(namespace string, name string, c client.Client, ctx context.Context) int64 {
	workflow := getWorkflow(namespace, name, c, ctx)
	return workflow.Generation
}

// GetAnnotationAsBool returns the boolean value from the given annotation.
// If the annotation is not present or is there an error in the ParseBool conversion, returns false.
func GetAnnotationAsBool(object client.Object, key string) bool {
	if object.GetAnnotations() != nil {
		b, err := strconv.ParseBool(object.GetAnnotations()[key])
		if err != nil {
			return false
		}
		return b
	}
	return false
}

// SetAnnotation Safely set the annotation to the object
func SetAnnotation(object client.Object, key, value string) {
	if object.GetAnnotations() != nil {
		object.GetAnnotations()[key] = value
	} else {
		object.SetAnnotations(map[string]string{key: value})
	}
}
