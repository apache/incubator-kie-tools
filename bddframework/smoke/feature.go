// Code from here has been copied and a bit rearranged from github.com/cucumber/godog/suite.go
// as we needed the functionality to access features from the suite but it not possible
// Stay here as long as https://github.com/cucumber/godog/issues/222 is not solved

package operator

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"

	"github.com/cucumber/godog/gherkin"
)

func parseFeatures(filter string, paths []string) ([]*gherkin.Feature, error) {
	if len(paths) == 0 {
		inf, err := os.Stat("features")
		if err == nil && inf.IsDir() {
			paths = []string{"features"}
		}
	}

	features := make(map[string]*gherkin.Feature)
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
			if _, duplicate := features[ft.Name]; duplicate {
				continue
			}
			features[ft.Name] = ft
		}
	}
	return filterFeatures(filter, features), nil
}

func parsePath(path string) ([]*gherkin.Feature, error) {
	var features []*gherkin.Feature

	fi, err := os.Stat(path)
	if err != nil {
		return features, err
	}

	if fi.IsDir() {
		return parseFeatureDir(path)
	}

	ft, err := parseFeatureFile(path)
	if err != nil {
		return features, err
	}

	return append(features, ft), nil
}

func parseFeatureFile(path string) (*gherkin.Feature, error) {
	reader, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer reader.Close()

	var buf bytes.Buffer
	ft, err := gherkin.ParseFeature(io.TeeReader(reader, &buf))
	if err != nil {
		return nil, fmt.Errorf("%s - %v", path, err)
	}
	return ft, nil
}

func parseFeatureDir(dir string) ([]*gherkin.Feature, error) {
	var features []*gherkin.Feature
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

		feat, err := parseFeatureFile(p)
		if err != nil {
			return err
		}
		features = append(features, feat)
		return nil
	})
}

func filterFeatures(tags string, collected map[string]*gherkin.Feature) (features []*gherkin.Feature) {
	for _, ft := range collected {
		applyTagFilter(tags, ft)
		features = append(features, ft)
	}

	return features
}

func applyTagFilter(tags string, ft *gherkin.Feature) {
	if len(tags) == 0 {
		return
	}
	ft.ScenarioDefinitions = getMatchingScenarios(tags, ft)
}

func getMatchingScenarios(tags string, ft *gherkin.Feature) []interface{} {
	var scenarios []interface{}
	for _, scenario := range ft.ScenarioDefinitions {
		switch t := scenario.(type) {
		case *gherkin.ScenarioOutline:
			var allExamples []*gherkin.Examples
			for _, examples := range t.Examples {
				if matchesTags(tags, allTags(ft, t, examples)) {
					allExamples = append(allExamples, examples)
				}
			}
			t.Examples = allExamples
			if len(t.Examples) > 0 {
				scenarios = append(scenarios, scenario)
			}
		case *gherkin.Scenario:
			if matchesTags(tags, allTags(ft, t)) {
				scenarios = append(scenarios, scenario)
			}
		}
	}
	return scenarios
}

// based on http://behat.readthedocs.org/en/v2.5/guides/6.cli.html#gherkin-filters
func matchesTags(filter string, tags []string) (ok bool) {
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

func hasTag(tags []string, tag string) bool {
	for _, t := range tags {
		if t == tag {
			return true
		}
	}
	return false
}

func allTags(nodes ...interface{}) []string {
	var tags, tmp []string
	for _, node := range nodes {
		var gr []*gherkin.Tag
		switch t := node.(type) {
		case *gherkin.Feature:
			gr = t.Tags
		case *gherkin.ScenarioOutline:
			gr = t.Tags
		case *gherkin.Scenario:
			gr = t.Tags
		case *gherkin.Examples:
			gr = t.Tags
		}

		for _, gtag := range gr {
			tag := strings.TrimSpace(gtag.Name)
			if tag[0] == '@' {
				tag = tag[1:]
			}
			copy(tmp, tags)
			var found bool
			for _, tg := range tmp {
				if tg == tag {
					found = true
					break
				}
			}
			if !found {
				tags = append(tags, tag)
			}
		}
	}
	return tags
}
