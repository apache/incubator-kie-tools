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

package framework

import (
	"fmt"
	"io"
	"io/ioutil"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/google/uuid"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/env"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/version"
)

const (
	fileFlags                 = os.O_CREATE | os.O_WRONLY | os.O_APPEND
	permissionMode            = 0666
	customKogitoImagePrefix   = "custom-"
	labelKeyVersion           = "version"
	kogitoBuilderImageEnvVar  = "BUILDER_IMAGE"
	kogitoRuntimeJVMEnvVar    = "RUNTIME_IMAGE"
	kogitoRuntimeNativeEnvVar = "RUNTIME_NATIVE_IMAGE"
	// defaultBuilderImage Builder Image for Kogito
	defaultBuilderImage = "kogito-s2i-builder"
	// defaultRuntimeJVM Runtime Image for Kogito with  JRE
	defaultRuntimeJVM = "kogito-runtime-jvm"
	//defaultRuntimeNative Runtime Image for Kogito for Native Quarkus Application
	defaultRuntimeNative = "kogito-runtime-native"
	// imageRegistryEnvVar ...
	imageRegistryEnvVar = "IMAGE_REGISTRY"
	// defaultImageRegistry the default services image repository
	defaultImageRegistry = "quay.io/kiegroup"
)

// GenerateNamespaceName generates a namespace name, taking configuration into account (local or not)
func GenerateNamespaceName(prefix string) string {
	rand.Seed(time.Now().UnixNano())
	ns := fmt.Sprintf("%s-%s", prefix, GenerateShortUID(4))
	if config.IsLocalTests() {
		username := env.GetEnvUsername()
		ns = fmt.Sprintf("%s-local-%s", username, ns)
	} else if len(config.GetCiName()) > 0 {
		ns = fmt.Sprintf("%s-%s", config.GetCiName(), ns)
	}
	return ns
}

// ReadFromURI reads string content from given URI (URL or Filesystem)
func ReadFromURI(uri string) (string, error) {
	var data []byte
	if strings.HasPrefix(uri, "http") {
		resp, err := http.Get(uri)
		if err != nil {
			return "", err
		}
		defer resp.Body.Close()
		if data, err = ioutil.ReadAll(resp.Body); err != nil {
			return "", err
		}
	} else {
		// It should be a Filesystem uri
		absPath, err := filepath.Abs(uri)
		if err != nil {
			return "", err
		}
		data, err = ioutil.ReadFile(absPath)
		if err != nil {
			return "", err
		}
	}
	return string(data), nil
}

// WaitFor waits for a specification condition to be met or until one error condition is met
func WaitFor(namespace, display string, timeout time.Duration, condition func() (bool, error), errorConditions ...func() (bool, error)) error {
	GetLogger(namespace).Info(fmt.Sprintf("Wait %s for %s", timeout.String(), display))

	timeoutChan := time.After(timeout)
	tick := time.NewTicker(1 * time.Second)
	defer tick.Stop()

	for {
		select {
		case <-timeoutChan:
			return fmt.Errorf("Timeout waiting for %s", display)
		case <-tick.C:
			running, err := condition()
			if err != nil {
				GetLogger(namespace).Warn(fmt.Sprintf("Problem in condition execution, waiting for %s => %v", display, err))
			}

			if running {
				GetLogger(namespace).Info(fmt.Sprintf("'%s' is successful", display))
				return nil
			}

			for _, errorCondition := range errorConditions {
				if hasErrors, err := errorCondition(); hasErrors {
					GetLogger(namespace).Error(err, "Problem in condition execution", "display", display)
					return err
				}
			}
		}
	}
}

// PrintDataMap prints a formatted dataMap using the given writer
func PrintDataMap(keys []string, dataMaps []map[string]string, writer io.StringWriter) error {
	// Get size of strings to be written, to be able to format correctly
	maxStringSizeMap := make(map[string]int)
	for _, key := range keys {
		maxSize := len(key)
		for _, dataMap := range dataMaps {
			if len(dataMap[key]) > maxSize {
				maxSize = len(dataMap[key])
			}
		}
		maxStringSizeMap[key] = maxSize
	}

	// Write headers
	for _, header := range keys {
		if _, err := writer.WriteString(header); err != nil {
			return fmt.Errorf("Error in writing the header: %v", err)
		}
		if _, err := writer.WriteString(getWhitespaceStr(maxStringSizeMap[header] - len(header) + 1)); err != nil {
			return fmt.Errorf("Error in writing headers: %v", err)
		}
		if _, err := writer.WriteString(" | "); err != nil {
			return fmt.Errorf("Error in writing headers : %v", err)
		}
	}
	if _, err := writer.WriteString("\n"); err != nil {
		return fmt.Errorf("Error in writing headers '|': %v", err)

	}

	// Write events
	for _, dataMap := range dataMaps {
		for _, key := range keys {
			if _, err := writer.WriteString(dataMap[key]); err != nil {
				return fmt.Errorf("Error in writing events: %v", err)
			}
			if _, err := writer.WriteString(getWhitespaceStr(maxStringSizeMap[key] - len(dataMap[key]) + 1)); err != nil {
				return fmt.Errorf("Error in writing events: %v", err)
			}
			if _, err := writer.WriteString(" | "); err != nil {
				return fmt.Errorf("Error in writing events: %v", err)
			}
		}
		if _, err := writer.WriteString("\n"); err != nil {
			return fmt.Errorf("Error in writing events: %v", err)
		}
	}
	return nil
}

func getWhitespaceStr(size int) string {
	whiteSpaceStr := ""
	for i := 0; i < size; i++ {
		whiteSpaceStr += " "
	}
	return whiteSpaceStr
}

// CreateFolder  creates a folder and all its parents if not exist
func CreateFolder(folder string) error {
	return os.MkdirAll(folder, os.ModePerm)
}

// CreateTemporaryFolder creates a folder in default directory for temporary files
func CreateTemporaryFolder(folderPrefix string) (string, error) {
	return ioutil.TempDir("", folderPrefix)
}

// DeleteFolder deletes a folder and all its subfolders
func DeleteFolder(folder string) error {
	return os.RemoveAll(folder)
}

// CreateFile Creates file in folder with supplied content
func CreateFile(folder, fileName, fileContent string) error {
	f, err := os.Create(folder + "/" + fileName)
	if err != nil {
		return fmt.Errorf("Error creating file %s in folder %s: %v ", fileName, folder, err)
	}

	if _, err = f.WriteString(fileContent); err != nil {
		f.Close()
		return fmt.Errorf("Error writing to file %s in folder %s: %v ", fileName, folder, err)
	}

	if err := f.Close(); err != nil {
		return fmt.Errorf("Error closing file %s in folder %s: %v ", fileName, folder, err)
	}
	return nil
}

// CreateTemporaryFile Creates file in default directory for temporary files with supplied content
func CreateTemporaryFile(filePattern, fileContent string) (string, error) {
	f, err := ioutil.TempFile("", filePattern)
	if err != nil {
		return "", fmt.Errorf("Error creating file with pattern %s in temporary folder: %v ", filePattern, err)
	}

	if _, err = f.WriteString(fileContent); err != nil {
		f.Close()
		return "", fmt.Errorf("Error writing to file %s in temporary folder: %v ", f.Name(), err)
	}

	if err := f.Close(); err != nil {
		return "", fmt.Errorf("Error closing file %s in temporary folder: %v ", f.Name(), err)
	}

	return f.Name(), nil
}

// DeleteFile deletes a file
func DeleteFile(folder, fileName string) error {
	return os.Remove(folder + "/" + fileName)
}

// GetKogitoBuildS2IImage returns the S2I builder image tag
func GetKogitoBuildS2IImage() string {
	if len(config.GetBuildBuilderImageStreamTag()) > 0 {
		return config.GetBuildBuilderImageStreamTag()
	}

	return ConstructDefaultImageFullTag(GetDefaultBuilderImage())
}

// GetKogitoBuildRuntimeImage returns the Runtime image tag
func GetKogitoBuildRuntimeImage(native bool) string {
	var imageName string
	if native {
		if len(config.GetBuildRuntimeNativeImageStreamTag()) > 0 {
			return config.GetBuildRuntimeNativeImageStreamTag()
		}
		imageName = GetDefaultRuntimeNativeImage()
	} else {
		if len(config.GetBuildRuntimeJVMImageStreamTag()) > 0 {
			return config.GetBuildRuntimeJVMImageStreamTag()
		}
		imageName = GetDefaultRuntimeJVMImage()
	}

	return ConstructDefaultImageFullTag(imageName)
}

// ConstructDefaultImageFullTag construct the full image tag (adding default registry and tag)
func ConstructDefaultImageFullTag(imageName string) string {
	image := &api.Image{
		Name: imageName,
	}
	AppendImageDefaultValues(image)

	return ConvertImageToImageTag(*image)
}

// AppendImageDefaultValues appends the image default values if none existing
func AppendImageDefaultValues(image *api.Image) {
	if len(image.Domain) == 0 {
		image.Domain = GetDefaultImageRegistry()
	}

	if len(image.Tag) == 0 {
		image.Tag = GetKogitoImageVersion(version.OperatorVersion)
	}
}

// AddLineToFile adds the given line to the given file
func AddLineToFile(line, filename string) error {
	file, err := os.OpenFile(filename, fileFlags, permissionMode)
	if err != nil {
		return err
	}
	defer file.Close()
	if _, err = file.WriteString(fmt.Sprintf("%s\n", line)); err != nil {
		return err
	}

	return nil
}

// GetDefaultRuntimeNativeImage ...
func GetDefaultRuntimeNativeImage() string {
	runtimeImage := os.Getenv(kogitoRuntimeNativeEnvVar)
	if len(runtimeImage) == 0 {
		runtimeImage = defaultRuntimeNative
	}
	return runtimeImage
}

// GetDefaultRuntimeJVMImage ...
func GetDefaultRuntimeJVMImage() string {
	runtimeImage := os.Getenv(kogitoRuntimeJVMEnvVar)
	if len(runtimeImage) == 0 {
		runtimeImage = defaultRuntimeJVM
	}
	return runtimeImage
}

// GetDefaultBuilderImage ...
func GetDefaultBuilderImage() string {
	builderImage := os.Getenv(kogitoBuilderImageEnvVar)
	if len(builderImage) == 0 {
		builderImage = defaultBuilderImage
	}
	return builderImage
}

// GenerateUID generates a Unique ID to be used across test cases
func GenerateUID() types.UID {
	uid, err := uuid.NewRandom()
	if err != nil {
		panic(err)
	}
	return types.UID(uid.String())
}

// GenerateShortUID same as GenerateUID, but returns a fraction of the generated UID instead.
// If count > than UID total length, returns the entire sequence.
func GenerateShortUID(count int) string {
	if count == 0 {
		return ""
	}
	uid := GenerateUID()
	if count > len(uid) {
		count = len(uid)
	}
	return string(uid)[:count]
}

// ConvertImageToImageTag converts an Image into a plain string (domain/namespace/name:tag).
func ConvertImageToImageTag(image api.Image) string {
	imageTag := ""
	if len(image.Domain) > 0 {
		imageTag += image.Domain + "/"
	}
	imageTag += image.Name
	if len(image.Tag) > 0 {
		imageTag += ":" + image.Tag
	}
	return imageTag
}

// GetDefaultImageRegistry ...
func GetDefaultImageRegistry() string {
	registry := os.Getenv(imageRegistryEnvVar)
	if len(registry) == 0 {
		registry = defaultImageRegistry
	}
	return registry
}

// GetKogitoImageVersion gets the Kogito Runtime latest micro version based on the given version
// E.g. Operator version is 0.9.0, the latest image version is 0.9.x-latest
// unit test friendly unexported function
// in this case we are considering only micro updates, that's 0.9.0 -> 0.9, thus for 1.0.0 => 1.0
// in the future this should be managed with carefully if we desire a behavior like 1.0.0 => 1, that's minor upgrades
func GetKogitoImageVersion(v string) string {
	if len(v) == 0 {
		return "latest"
	}

	versionPrefix := strings.Split(v, ".")
	length := len(versionPrefix)
	if length > 0 {
		lastIndex := 2   // micro updates
		if length <= 2 { // guard against unusual cases
			lastIndex = length
		}
		return strings.Join(versionPrefix[:lastIndex], ".")
	}
	return "latest"
}
