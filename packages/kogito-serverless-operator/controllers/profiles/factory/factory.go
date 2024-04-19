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
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/gitops"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/preview"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/dev"
)

const (
	defaultProfile = metadata.PreviewProfile
)

type reconcilerBuilder func(client client.Client, cfg *rest.Config, recorder record.EventRecorder) profiles.ProfileReconciler

var profileBuilders = map[metadata.ProfileType]reconcilerBuilder{
	metadata.PreviewProfile: preview.NewProfileReconciler,
	metadata.DevProfile:     dev.NewProfileReconciler,
	metadata.GitOpsProfile:  gitops.NewProfileForOpsReconciler,
}

func profileBuilder(workflow *operatorapi.SonataFlow) reconcilerBuilder {
	profile := workflow.Annotations[metadata.Profile]
	if len(profile) == 0 {
		profile = defaultProfile.String()
	}
	// keep backward compatibility
	if profile == metadata.ProdProfile.String() {
		klog.V(log.W).Infof("Profile %s is deprecated, please use '%s' instead.", metadata.ProdProfile, metadata.PreviewProfile)
		profile = metadata.PreviewProfile.String()
	}
	// Enforce GitOps profile if the .spec.podTemplate.container.image is set in the Preview profile.
	if (profile == metadata.PreviewProfile.String() || profile == metadata.ProdProfile.String()) && workflow.HasContainerSpecImage() {
		workflow.Annotations[metadata.Profile] = metadata.GitOpsProfile.String()
		return profileBuilders[metadata.GitOpsProfile]
	}
	if _, ok := profileBuilders[metadata.ProfileType(profile)]; !ok {
		klog.V(log.W).Infof("Profile %s not supported, please use '%s' or '%s'. Falling back to %s", profile, metadata.PreviewProfile, metadata.DevProfile, defaultProfile)
		return profileBuilders[defaultProfile]
	}
	return profileBuilders[metadata.ProfileType(profile)]
}

// NewReconciler creates a new ProfileReconciler based on the given workflow and context.
func NewReconciler(client client.Client, cfg *rest.Config, recorder record.EventRecorder, workflow *operatorapi.SonataFlow) profiles.ProfileReconciler {
	return profileBuilder(workflow)(client, cfg, recorder)
}
