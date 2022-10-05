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
package builder

import (
	"context"
	"fmt"
	"github.com/davidesalerno/kogito-serverless-operator/constants"
	"github.com/ricardozanini/kogito-builder/api"
	"github.com/ricardozanini/kogito-builder/builder"
	"github.com/ricardozanini/kogito-builder/client"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"os"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
	"time"
)

type Builder struct {
	ctx context.Context
}

// NewBuilder ...
func NewBuilder(contex context.Context) Builder {
	return Builder{ctx: contex}
}

func (b *Builder) BuildImageWithDefaults(sourceSwfName string, sourceSwf []byte) (*api.Build, error) {
	wd, _ := os.Getwd()
	dockerFile, _ := os.ReadFile(wd + "/builder/Dockerfile")
	ib := NewImageBuilder(sourceSwfName, sourceSwf, dockerFile)
	ib.OnNamespace(constants.BUILDER_NAMESPACE_DEFAULT)
	ib.WithPodMiddleName(constants.BUILDER_IMG_NAME_DEFAULT)
	ib.WithInsecureRegistry(false)
	ib.WithImageName(sourceSwfName + ":latest")
	ib.WithSecret(constants.DEFAULT_KANIKO_SECRET)
	ib.WithRegistryAddress(constants.DEFAULT_REGISTRY_REPO)
	ib.WithTimeout(5 * time.Minute)
	return b.BuildImage(ib.Build())
}

func (r *Builder) BuildImage(b KogitoBuilder) (*api.Build, error) {
	log := ctrllog.FromContext(r.ctx)
	cli, err := client.NewClient(true)
	platform := api.PlatformBuild{
		ObjectReference: api.ObjectReference{
			Namespace: b.Namespace,
			Name:      b.PodMiddleName,
		},
		Spec: api.PlatformBuildSpec{
			BuildStrategy:   api.BuildStrategyPod,
			PublishStrategy: api.PlatformBuildPublishStrategyKaniko,
			Registry: api.RegistrySpec{
				Insecure: b.InsecureRegistry,
				Address:  b.RegistryAddress,
				Secret:   b.Secret,
			},
			Timeout: &metav1.Duration{
				Duration: b.Timeout,
			},
		},
	}

	build, err := builder.NewBuild(platform, b.ImageName, b.PodMiddleName).
		WithResource(constants.BUILDER_RESOURCE_NAME_DEFAULT, b.DockerFile).WithResource(b.SourceSwfName+".sw.json", b.SourceSwf).
		WithClient(cli).
		Schedule()
	if err != nil {
		log.Error(err, err.Error())
		return nil, err
	}
	//FIXME: Remove this  For loop as soon as the KogitoServerlessBuild CR will be availbable
	// from now the Reconcile method can be called until the build is finished
	for build.Status.Phase != api.BuildPhaseSucceeded &&
		build.Status.Phase != api.BuildPhaseError &&
		build.Status.Phase != api.BuildPhaseFailed {
		log.Info("Build status is ", "status", build.Status.Phase)
		build, err = builder.FromBuild(build).WithClient(cli).Reconcile()
		if err != nil {
			log.Info("Failed to build")
			panic(fmt.Errorf("build %v just failed", build))
		}
		time.Sleep(10 * time.Second)
	}
	return build, err
}

type KogitoBuilder struct {
	SourceSwfName        string
	SourceSwf            []byte
	DockerFile           []byte
	Namespace            string
	InsecureRegistry     bool
	Timeout              time.Duration
	ImageName            string
	PodMiddleName        string
	RegistryAddress      string
	RegistryOrganization string
	Secret               string
}

type ImageBuilder struct {
	KogitoBuilder *KogitoBuilder
}

func NewImageBuilder(sourceSwfName string, sourceSwf []byte, dockerFile []byte) ImageBuilder {
	return ImageBuilder{KogitoBuilder: &KogitoBuilder{SourceSwfName: sourceSwfName, SourceSwf: sourceSwf, DockerFile: dockerFile}}
}

func (b *ImageBuilder) OnNamespace(namespace string) *ImageBuilder {
	b.KogitoBuilder.Namespace = namespace
	return b
}

func (b *ImageBuilder) WithTimeout(timeout time.Duration) *ImageBuilder {
	b.KogitoBuilder.Timeout = timeout
	return b
}

func (b *ImageBuilder) WithSecret(secret string) *ImageBuilder {
	b.KogitoBuilder.Secret = secret
	return b
}

func (b *ImageBuilder) WithRegistryAddress(registryAddress string) *ImageBuilder {
	b.KogitoBuilder.RegistryAddress = registryAddress
	return b
}

func (b *ImageBuilder) WithInsecureRegistry(insecureRegistry bool) *ImageBuilder {
	b.KogitoBuilder.InsecureRegistry = insecureRegistry
	return b
}

func (b *ImageBuilder) WithPodMiddleName(buildName string) *ImageBuilder {
	b.KogitoBuilder.PodMiddleName = buildName
	return b
}

func (b *ImageBuilder) WithImageName(imageName string) *ImageBuilder {
	b.KogitoBuilder.ImageName = imageName
	return b
}

func (b *ImageBuilder) Build() KogitoBuilder {
	return *b.KogitoBuilder
}
