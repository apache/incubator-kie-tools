// Copyright 2024 Apache Software Foundation (ASF)
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

package validation

import (
	"archive/tar"
	"context"
	"fmt"
	"io"

	"github.com/google/go-cmp/cmp"
	"github.com/google/go-containerregistry/pkg/name"
	v1 "github.com/google/go-containerregistry/pkg/v1"
	"github.com/google/go-containerregistry/pkg/v1/remote"
	"k8s.io/apimachinery/pkg/util/yaml"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
)

type ImageValidator struct{}

var workflowName = "deployments/app/workflow.sw.json"

func (v ImageValidator) Validate(ctx context.Context, client client.Client, sonataflow *operatorapi.SonataFlow, req ctrl.Request) error {
	if sonataflow.HasContainerSpecImage() {
		err := client.Get(ctx, req.NamespacedName, sonataflow)
		equals, err := validateImage(sonataflow, sonataflow.Spec.PodTemplate.Container.Image)
		if err != nil {
			return err
		}
		if !equals {
			return fmt.Errorf("Workflow, defined in the image %s doesn't match deployment workflow", sonataflow.Spec.PodTemplate.Container.Image)
		}
	}
	return nil
}

func validateImage(sonataflow *operatorapi.SonataFlow, image string) (bool, error) {
	imageRef, err := name.ParseReference(image)
	if err != nil {
		return false, err
	}

	ref, err := remote.Image(imageRef)
	if err != nil {
		return false, err
	}

	reader, err := readLayers(ref, workflowName)
	if err != nil {
		return false, err
	}

	workflowDockerImage, err := jsonFromDockerImage(reader)
	if err != nil {
		return false, err
	}

	return cmp.Equal(workflowDockerImage, sonataflow.Spec.Flow), nil
}

func readLayers(image v1.Image, workflow string) (*tar.Reader, error) {
	layers, err := image.Layers()
	if err != nil {
		return nil, err
	}

	for i := len(layers) - 1; i >= 0; i-- {
		if reader, err := readLayer(layers[i], workflow); err == nil && reader != nil {
			return reader, nil
		} else if err != nil {
			return nil, err
		}
	}
	return nil, fmt.Errorf("file not found %s in docker image", workflow)
}

func readLayer(layer v1.Layer, workflow string) (*tar.Reader, error) {
	uncompressedLayer, err := layer.Uncompressed()
	if err != nil {
		return nil, fmt.Errorf("failed to get uncompressed layer: %v", err)
	}
	defer uncompressedLayer.Close()

	tarReader := tar.NewReader(uncompressedLayer)
	for {
		header, err := tarReader.Next()
		if err != nil {
			if err.Error() == "EOF" {
				break
			}
			return nil, fmt.Errorf("error reading tar: %v", err)
		}

		if header.Typeflag == '0' && header.Name == workflow {
			return tarReader, nil
		}
	}

	return nil, nil
}

func jsonFromDockerImage(reader io.Reader) (operatorapi.Flow, error) {
	data, err := io.ReadAll(reader)
	workflow := &operatorapi.Flow{}
	if err = yaml.Unmarshal(data, workflow); err != nil {
		return *workflow, err
	}
	return *workflow, nil
}
