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

package factory

import (
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/gitops"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/preview"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"

	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/dev"
)

type reconcilerBuilder func(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler

var profileBuilders = map[metadata.ProfileType]reconcilerBuilder{
	metadata.PreviewProfile: preview.NewProfileReconciler,
	metadata.DevProfile:     dev.NewProfileReconciler,
	metadata.GitOpsProfile:  gitops.NewProfileForOpsReconciler,
}

func profileBuilder(workflow *operatorapi.SonataFlow) reconcilerBuilder {
	profile := metadata.GetProfileOrDefault(workflow.Annotations)
	// keep backward compatibility
	if profile == metadata.ProdProfile {
		klog.V(log.W).Infof("Profile %s is deprecated, please use '%s' instead.", metadata.ProdProfile, metadata.PreviewProfile)
		profile = metadata.PreviewProfile
	}
	// Enforce GitOps profile if the .spec.podTemplate.container.image is set in the Preview profile.
	if (profile == metadata.PreviewProfile || profile == metadata.ProdProfile) && workflow.HasContainerSpecImage() {
		workflow.Annotations[metadata.Profile] = metadata.GitOpsProfile.String()
		return profileBuilders[metadata.GitOpsProfile]
	}
	if _, ok := profileBuilders[profile]; !ok {
		klog.V(log.W).Infof("Profile %s not supported, please use '%s' or '%s'. Falling back to %s", profile, metadata.PreviewProfile, metadata.DevProfile, metadata.DefaultProfile)
		return profileBuilders[metadata.DefaultProfile]
	}
	return profileBuilders[profile]
}

// NewReconciler creates a new ProfileReconciler based on the given workflow and context.
func NewReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder, workflow *operatorapi.SonataFlow) profiles.ProfileReconciler {
	return profileBuilder(workflow)(client, cfg, recorder)
}
