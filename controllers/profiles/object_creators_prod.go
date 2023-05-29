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

package profiles

import (
	"fmt"

	appsv1 "k8s.io/api/apps/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
)

const (
	imageOpenShiftTriggers            = "image.openshift.io/triggers"
	imageOpenShiftTriggersValueFormat = "[{\"from\":{\"kind\":\"ImageStreamTag\",\"name\":\"%s\"},\"fieldPath\":\"spec.template.spec.containers[?(@.name==\\\"" + defaultContainerName + "\\\")].image\"}]"
)

// addOpenShiftImageTriggerDeploymentMutateVisitor adds the ImageStream trigger annotation to the Deployment
//
// See: https://docs.openshift.com/container-platform/4.13/openshift_images/triggering-updates-on-imagestream-changes.html
func addOpenShiftImageTriggerDeploymentMutateVisitor(image string) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			annotations := make(map[string]string, len(object.(*appsv1.Deployment).Annotations)+1)
			for k, v := range object.(*appsv1.Deployment).Annotations {
				annotations[k] = v
			}
			annotations[imageOpenShiftTriggers] = fmt.Sprintf(imageOpenShiftTriggersValueFormat, image)
			object.(*appsv1.Deployment).Annotations = annotations
			return nil
		}
	}
}
