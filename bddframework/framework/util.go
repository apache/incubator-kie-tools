// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

const (
	enabledKey  = "enabled"
	disabledKey = "disabled"
)

// GenerateNamespaceName generates a namespace name, taking configuration into account (local or not)
func GenerateNamespaceName(prefix string) string {
	rand.Seed(time.Now().UnixNano())
	ns := fmt.Sprintf("%s-%s", prefix, RandSeq(4))
	if config.IsLocalTests() {
		username := getEnvUsername()
		ns = fmt.Sprintf("%s-local-%s", username, ns)
	} else if len(config.GetCiName()) > 0 {
		ns = fmt.Sprintf("%s-%s", config.GetCiName(), ns)
	}
	return ns
}

// RandSeq returns a generated string
func RandSeq(size int) string {
	randomLetters := []rune("abcdefghijklmnopqrstuvwxyz0123456789")
	b := make([]rune, size)
	for i := range b {
		b[i] = randomLetters[rand.Intn(len(randomLetters))]
	}
	return string(b)
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
		data, err = ioutil.ReadAll(resp.Body)
	} else {
		// It should be a Filesystem uri
		absPath, err := filepath.Abs(uri)
		data, err = ioutil.ReadFile(absPath)
		if err != nil {
			return "", err
		}
	}
	return string(data), nil
}

// WaitFor waits for a specific condition to be met
func WaitFor(namespace, display string, timeout time.Duration, condition func() (bool, error)) error {
	GetLogger(namespace).Infof("Wait %s for %s", timeout.String(), display)

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
				GetLogger(namespace).Warnf("Problem in condition execution, waiting for %s => %v", display, err)
			}
			if running {
				GetLogger(namespace).Infof("'%s' is successful", display)
				return nil
			}
		}
	}
}

// PrintDataMap prints a formatted dataMap using the given writer
func PrintDataMap(keys []string, dataMaps []map[string]string, writer io.StringWriter) {
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
		writer.WriteString(header)
		writer.WriteString(getWhitespaceStr(maxStringSizeMap[header] - len(header) + 1))
		writer.WriteString(" | ")
	}
	writer.WriteString("\n")

	// Write events
	for _, dataMap := range dataMaps {
		for _, key := range keys {
			writer.WriteString(dataMap[key])
			writer.WriteString(getWhitespaceStr(maxStringSizeMap[key] - len(dataMap[key]) + 1))
			writer.WriteString(" | ")
		}
		writer.WriteString("\n")
	}
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

// MustParseEnabledDisabled parse a boolean string value
func MustParseEnabledDisabled(value string) bool {
	switch value {
	case enabledKey:
		return true
	case disabledKey:
		return false
	default:
		panic(fmt.Errorf("Unknown value for enabled/disabled: %s", value))
	}
}
