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

// Code from here has been copied and a bit rearranged from github.com/cucumber/godog/suite.go
// as we needed the functionality to access features from the suite but it not possible
// Stay here as long as https://github.com/cucumber/godog/issues/222 is not solved

package gherkin

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"

	"github.com/cucumber/gherkin-go/v19"
	"github.com/cucumber/messages-go/v16"
)

// Feature represents a Gherkin feature
type Feature struct {
	Document *messages.GherkinDocument
	Pickles  []*messages.Pickle
}

// ParseFeatures parse features from given paths
func ParseFeatures(filter string, paths []string) ([]*Feature, error) {
	if len(paths) == 0 {
		inf, err := os.Stat("features")
		if err == nil && inf.IsDir() {
			paths = []string{"features"}
		}
	}

	features := make(map[string]*Feature)
	for _, path := range paths {
		fts, err := parsePath(path)
		switch {
		case os.IsNotExist(err):
			return nil, fmt.Errorf(`feature path "%s" is not available`, path)
		case os.IsPermission(err):
			return nil, fmt.Errorf(`feature path "%s" is not accessible`, path)
		case err != nil:
			return nil, err
		}

		for _, ft := range fts {
			if _, duplicate := features[ft.Document.Feature.Name]; duplicate {
				continue
			}
			features[ft.Document.Feature.Name] = ft
		}
	}
	return filterFeatures(filter, features), nil
}

func parsePath(path string) ([]*Feature, error) {
	var features []*Feature

	fi, err := os.Stat(path)
	if err != nil {
		return features, err
	}

	if fi.IsDir() {
		return parseFeatureDir(path)
	}

	newIDFunc := (&messages.Incrementing{}).NewId
	ft, err := parseFeatureFile(path, newIDFunc)
	if err != nil {
		return features, err
	}

	return append(features, ft), nil
}

func parseFeatureFile(path string, newIDFunc func() string) (*Feature, error) {
	reader, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer reader.Close()

	var buf bytes.Buffer
	ft, err := gherkin.ParseGherkinDocument(io.TeeReader(reader, &buf), newIDFunc)
	if err != nil {
		return nil, fmt.Errorf("%s - %v", path, err)
	}

	pickles := gherkin.Pickles(*ft, path, newIDFunc)

	return &Feature{
		Document: ft,
		Pickles:  pickles,
	}, nil
}

func parseFeatureDir(dir string) ([]*Feature, error) {
	var features []*Feature
	return features, filepath.Walk(dir, func(p string, f os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		if f.IsDir() {
			return nil
		}

		if !strings.HasSuffix(p, ".feature") {
			return nil
		}

		feat, err := parseFeatureFile(p, (&messages.Incrementing{}).NewId)
		if err != nil {
			return err
		}
		features = append(features, feat)
		return nil
	})
}

func filterFeatures(tags string, collected map[string]*Feature) (features []*Feature) {
	for _, ft := range collected {
		applyTagFilter(tags, ft)
		features = append(features, ft)
	}

	return features
}

func applyTagFilter(tags string, ft *Feature) {
	if len(tags) == 0 {
		return
	}
	var pickles []*messages.Pickle
	for _, pickle := range ft.Pickles {
		if matchesTags(tags, pickle.Tags) {
			pickles = append(pickles, pickle)
		}
	}

	ft.Pickles = pickles
}

// based on http://behat.readthedocs.org/en/v2.5/guides/6.cli.html#gherkin-filters
func matchesTags(filter string, tags []*messages.PickleTag) (ok bool) {
	ok = true
	for _, andTags := range strings.Split(filter, "&&") {
		var okComma bool
		for _, tag := range strings.Split(andTags, ",") {
			tag = strings.Replace(strings.TrimSpace(tag), "@", "", -1)
			if tag[0] == '~' {
				tag = tag[1:]
				okComma = !hasTag(tags, tag) || okComma
			} else {
				okComma = hasTag(tags, tag) || okComma
			}
		}
		ok = ok && okComma
	}
	return
}

func hasTag(tags []*messages.PickleTag, tag string) bool {
	for _, t := range tags {
		tName := strings.Replace(t.Name, "@", "", -1)

		if tName == tag {
			return true
		}
	}
	return false
}
