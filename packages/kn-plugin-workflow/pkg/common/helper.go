/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import (
	"fmt"
	"html/template"
	"os"
	"os/exec"
	"regexp"
	"strings"

	"github.com/spf13/cobra"
)

func RunCommand(command *exec.Cmd, commandName string) error {
	stdout, _ := command.StdoutPipe()
	stderr, _ := command.StderrPipe()

	if err := command.Start(); err != nil {
		fmt.Printf("ERROR: starting command \"%s\" failed\n", commandName)
		return err
	}

	VerboseLog(stdout, stderr)

	if err := command.Wait(); err != nil {
		fmt.Printf("ERROR: something went wrong during command \"%s\"\n", commandName)
		return err
	}

	return nil
}

func RunExtensionCommand(extensionCommand string, extensions string) error {
	command := ExecCommand("mvn", extensionCommand, fmt.Sprintf("-Dextensions=%s", extensions))
	if err := RunCommand(command, extensionCommand); err != nil {
		fmt.Println("ERROR: It wasn't possible to add Quarkus extension in your pom.xml.")
		return err
	}
	return nil
}

func GetTemplate(cmd *cobra.Command, name string) *template.Template {
	var (
		body = cmd.Long + "\n\n" + cmd.UsageString()
		t    = template.New(name)
		tpl  = template.Must(t.Parse(body))
	)
	return tpl
}

func DefaultTemplatedHelp(cmd *cobra.Command, args []string) {
	tpl := GetTemplate(cmd, "help")
	var data = struct{ Name string }{Name: cmd.Root().Use}

	if err := tpl.Execute(cmd.OutOrStdout(), data); err != nil {
		fmt.Fprintf(cmd.ErrOrStderr(), "unable to display help text: %v", err)
	}
}

func CheckImageName(name string) (err error) {
	matched, err := regexp.MatchString("^[a-z]([-a-z0-9]*[a-z0-9])?$", name)
	if !matched {
		fmt.Println(`
ERROR: Image name should match [a-z]([-a-z0-9]*[a-z0-9])?
The name needs to start with a lower case letter and then it can be composed exclusvely of lower case letters, numbers or dashes ('-')
Example of valid names: "test-0-0-1", "test", "t1"
Example of invalid names: "1-test", "test.1", "test/1"
		`)
		err = fmt.Errorf("invalid image name")
	}
	return
}

// Use the --image-registry, --image-repository, --image-name, --image-tag to override the --image flag
func GetImageConfig(image string, registry string, repository string, imageName string, tag string) (string, string, string, string) {
	imageTagArray := strings.Split(image, ":")
	imageArray := strings.SplitN(imageTagArray[0], "/", 3)

	var resultantRegistry = ""
	if len(registry) > 0 {
		resultantRegistry = registry
	} else if len(imageArray) > 1 {
		resultantRegistry = imageArray[0]
	}

	var resultantRepository = ""
	if len(repository) > 0 {
		resultantRepository = repository
	} else if len(imageArray) == 3 {
		resultantRepository = imageArray[1]
	}

	var resultantName = ""
	if len(imageName) > 0 {
		resultantName = imageName
	} else if len(imageArray) == 1 {
		resultantName = imageArray[0]
	} else if len(imageArray) == 2 {
		resultantName = imageArray[1]
	} else if len(imageArray) == 3 {
		resultantName = imageArray[2]
	}

	var resultantTag = DEFAULT_TAG
	if len(tag) > 0 {
		resultantTag = tag
	} else if len(imageTagArray) > 1 && len(imageTagArray[1]) > 0 {
		resultantTag = imageTagArray[1]
	}

	return resultantRegistry, resultantRepository, resultantName, resultantTag
}

func GetImage(registry string, repository string, name string, tag string) string {
	if len(registry) == 0 && len(repository) == 0 {
		return fmt.Sprintf("%s:%s", name, tag)
	} else if len(registry) == 0 {
		return fmt.Sprintf("%s/%s:%s", repository, name, tag)
	} else if len(repository) == 0 {
		return fmt.Sprintf("%s/%s:%s", registry, name, tag)
	}
	return fmt.Sprintf("%s/%s/%s:%s", registry, repository, name, tag)
}

func GetCurrentPath() (path string, err error) {
	path, err = os.Getwd()
	if err != nil {
		fmt.Println("ERROR: error getting current path")
		return
	}
	return
}
