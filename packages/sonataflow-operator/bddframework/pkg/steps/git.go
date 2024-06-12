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

package steps

import (
	"github.com/cucumber/godog"
	git "gopkg.in/src-d/go-git.v4"
	"gopkg.in/src-d/go-git.v4/plumbing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/framework"
)

// registerGitSteps register all existing GIT steps
func registerGitSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Clone Kogito examples into local directory$`, data.cloneKogitoExamplesIntoLocalDirectory)
}

func (data *Data) cloneKogitoExamplesIntoLocalDirectory() error {
	framework.GetLogger(data.Namespace).Info("Cloning kogito examples", "URI", config.GetExamplesRepositoryURI(), "branch", config.GetExamplesRepositoryRef(), "clonedLocation", data.KogitoExamplesLocation)

	cloneOptions := &git.CloneOptions{
		URL:          config.GetExamplesRepositoryURI(),
		SingleBranch: true,
	}

	var err error
	reference := config.GetExamplesRepositoryRef()
	if len(reference) == 0 {
		err = cloneExamples(data.KogitoExamplesLocation, cloneOptions)
	} else {
		// Try cloning as branch reference
		cloneOptions.ReferenceName = plumbing.NewBranchReferenceName(reference)
		err = cloneExamples(data.KogitoExamplesLocation, cloneOptions)
		// If branch clone was successful then return, otherwise try other cloning options
		if err == nil {
			return nil
		}

		// If branch cloning failed then try cloning as tag
		cloneOptions.ReferenceName = plumbing.NewTagReferenceName(reference)
		err = cloneExamples(data.KogitoExamplesLocation, cloneOptions)
	}
	return err
}

func cloneExamples(examplesLocation string, cloneOptions *git.CloneOptions) error {
	_, err := git.PlainClone(examplesLocation, false, cloneOptions)
	return err
}
