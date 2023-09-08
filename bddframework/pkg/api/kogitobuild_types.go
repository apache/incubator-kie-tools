// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package api

import (
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// KogitoBuildType describes the build types supported by the KogitoBuild CR
type KogitoBuildType string

const (
	// BinaryBuildType builds takes an uploaded binary file already compiled and creates a Kogito service image from it.
	BinaryBuildType KogitoBuildType = "Binary"
	// RemoteSourceBuildType builds pulls the source code from a Git repository, builds the binary and then the final Kogito service image.
	RemoteSourceBuildType KogitoBuildType = "RemoteSource"
	// LocalSourceBuildType builds takes an uploaded resource files such as DRL (rules), DMN (decision) or BPMN (process), builds the binary and the final Kogito service image.
	LocalSourceBuildType KogitoBuildType = "LocalSource"
)

// KogitoBuildConditionType ...
type KogitoBuildConditionType string

const (
	// KogitoBuildSuccessful condition for a successful build.
	KogitoBuildSuccessful KogitoBuildConditionType = "Successful"
	// KogitoBuildFailure condition for a failure build.
	KogitoBuildFailure KogitoBuildConditionType = "Failed"
	// KogitoBuildRunning condition for a running build.
	KogitoBuildRunning KogitoBuildConditionType = "Running"
)

// KogitoBuildConditionReason ...
type KogitoBuildConditionReason string

const (
	// OperatorFailureReason when operator fails to reconcile.
	OperatorFailureReason KogitoBuildConditionReason = "OperatorFailure"

	// BuildPhaseNewReason is automatically assigned to a newly created build.
	BuildPhaseNewReason KogitoBuildConditionReason = "New"

	// BuildPhasePendingReason indicates that a pod name has been assigned and a build is
	// about to start running.
	BuildPhasePendingReason KogitoBuildConditionReason = "Pending"

	// BuildPhaseRunningReason indicates that a pod has been created and a build is running.
	BuildPhaseRunningReason KogitoBuildConditionReason = "Running"

	// BuildPhaseCompleteReason indicates that a build has been successful.
	BuildPhaseCompleteReason KogitoBuildConditionReason = "Complete"

	// BuildPhaseFailedReason indicates that a build has executed and failed.
	BuildPhaseFailedReason KogitoBuildConditionReason = "Failed"

	// BuildPhaseErrorReason indicates that an error prevented the build from executing.
	BuildPhaseErrorReason KogitoBuildConditionReason = "Error"

	// BuildPhaseCancelledReason indicates that a running/pending build was stopped from executing.
	BuildPhaseCancelledReason KogitoBuildConditionReason = "Cancelled"

	// BuildNotStartedReason indicates that a build is not started yet.
	BuildNotStartedReason KogitoBuildConditionReason = "NotYetStarted"
)

// KogitoBuildInterface ...
type KogitoBuildInterface interface {
	client.Object
	// GetSpec gets the Kogito Service specification structure.
	GetSpec() KogitoBuildSpecInterface
	// GetStatus gets the Kogito Service Status structure.
	GetStatus() KogitoBuildStatusInterface
}

// KogitoBuildSpecInterface ...
type KogitoBuildSpecInterface interface {
	GetType() KogitoBuildType
	SetType(buildType KogitoBuildType)
	IsDisableIncremental() bool
	SetDisableIncremental(disableIncremental bool)
	GetEnv() []corev1.EnvVar
	SetEnv(env []corev1.EnvVar)
	GetGitSource() GitSourceInterface
	SetGitSource(gitSource GitSourceInterface)
	GetRuntime() RuntimeType
	SetRuntime(runtime RuntimeType)
	GetWebHooks() []WebHookSecretInterface
	SetWebHooks(webhooks []WebHookSecretInterface)
	IsNative() bool
	SetNative(native bool)
	GetResources() corev1.ResourceRequirements
	SetResources(resources corev1.ResourceRequirements)
	AddResourceRequest(name, value string)
	AddResourceLimit(name, value string)
	GetMavenMirrorURL() string
	SetMavenMirrorURL(mavenMirrorURL string)
	GetBuildImage() string
	SetBuildImage(buildImage string)
	GetRuntimeImage() string
	SetRuntimeImage(runtime string)
	GetTargetKogitoRuntime() string
	SetTargetKogitoRuntime(targetRuntime string)
	GetArtifact() ArtifactInterface
	SetArtifact(artifact ArtifactInterface)
	IsEnableMavenDownloadOutput() bool
	SetEnableMavenDownloadOutput(enableMavenDownloadOutput bool)
}

// KogitoBuildStatusInterface ...
type KogitoBuildStatusInterface interface {
	GetConditions() *[]metav1.Condition
	SetConditions(conditions *[]metav1.Condition)
	GetLatestBuild() string
	SetLatestBuild(latestBuild string)
	GetBuilds() BuildsInterface
	SetBuilds(builds BuildsInterface)
}

// BuildsInterface ...
type BuildsInterface interface {
	GetNew() []string
	SetNew(newBuilds []string)
	GetPending() []string
	SetPending(pendingBuilds []string)
	GetRunning() []string
	SetRunning(runningBuilds []string)
	GetComplete() []string
	SetComplete(completeBuilds []string)
	GetFailed() []string
	SetFailed(failedBuilds []string)
	GetError() []string
	SetError(errorBuilds []string)
	GetCancelled() []string
	SetCancelled(cancelled []string)
}
