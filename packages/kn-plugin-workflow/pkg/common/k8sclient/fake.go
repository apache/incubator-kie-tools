package k8sclient

import (
	"fmt"
	"github.com/spf13/afero"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/util/yaml"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/dynamic/fake"
	"strings"
)

type Fake struct {
	FS afero.Fs
}

var currentDynamicClient = initDynamicClient()

func initDynamicClient() dynamic.Interface {
	scheme := runtime.NewScheme()
	fakeDynamicClient := fake.NewSimpleDynamicClient(scheme)
	return fakeDynamicClient
}

func (m Fake) FakeDynamicClient() (dynamic.Interface, error) {
	return currentDynamicClient, nil
}

func (m Fake) GetNamespace() (string, error) {
	return "default", nil
}

func (m Fake) FakeParseYamlFile(path string) ([]unstructured.Unstructured, error) {
	data, err := afero.ReadFile(m.FS, path)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(string(data)), 4096)
	result := []unstructured.Unstructured{}
	for {
		rawObj := &unstructured.Unstructured{}
		err := decoder.Decode(rawObj)
		if err != nil {
			break
		}
		result = append(result, *rawObj)
	}
	return result, nil
}
