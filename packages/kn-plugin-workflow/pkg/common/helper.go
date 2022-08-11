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
	"context"
	"fmt"
	"html/template"
	"os/exec"
	"time"

	"github.com/briandowns/spinner"
	"github.com/spf13/cobra"
)

func RunCommand(command *exec.Cmd, verbose bool, commandName string, friendlyMessages []string) error {
	stdout, _ := command.StdoutPipe()
	stderr, _ := command.StderrPipe()

	if err := command.Start(); err != nil {
		fmt.Printf("ERROR: starting command \"%s\" failed\n", commandName)
		return err
	}

	s := spinner.New(spinner.CharSets[42], 100*time.Millisecond)
	ctx, cancel := context.WithCancel(context.Background())
	if verbose {
		VerboseLog(stdout, stderr)
	} else {
		s.Start()
		s.Suffix = friendlyMessages[0]
		printBuildActivity(ctx, s, friendlyMessages)
	}

	if err := command.Wait(); err != nil {
		s.Stop()
		cancel()
		fmt.Printf("ERROR: something went wrong during command \"%s\"\n", commandName)
		return err
	}

	if !verbose {
		s.Stop()
	}

	go func() {
		cancel()
	}()

	return nil
}

func RunExtensionCommand(verbose bool, extensionCommand string, friendlyMessages []string, extensions string) error {
	command := ExecCommand("mvn", extensionCommand, fmt.Sprintf("-Dextensions=%s", extensions))
	if err := RunCommand(command, verbose, extensionCommand, friendlyMessages); err != nil {
		fmt.Println("ERROR: It wasn't possible to add Quarkus extension in your pom.xml.")
		return err
	}
	return nil
}

func printBuildActivity(ctx context.Context, s *spinner.Spinner, friendlyMessages []string) {
	i := 1
	ticker := time.NewTicker(10 * time.Second)
	go func() {
		for {
			select {
			case <-ticker.C:
				s.Suffix = friendlyMessages[i]
				i++
				i = i % len(friendlyMessages)
			case <-ctx.Done():
				s.Suffix = ""
				ticker.Stop()
				return
			}
		}
	}()
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
