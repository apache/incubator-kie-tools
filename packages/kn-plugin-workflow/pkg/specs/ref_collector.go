/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package specs

import (
	"fmt"
	"gopkg.in/yaml.v3"
	"k8s.io/apimachinery/pkg/util/sets"
	"os"
	"strings"
)

type collector struct {
	filename string
	refs     sets.Set[string]
	doc      map[string]any
}

type node struct {
	section    string
	subsection string
	object     string
}

func newCollector(file string) (*collector, error) {
	data, err := os.ReadFile(file)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to read OpenAPI spec file %s: %w", file, err)
	}

	m := make(map[string]any)
	err = yaml.Unmarshal(data, &m)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to unmarshal OpenAPI spec file %s: %w", file, err)
	}

	return &collector{filename: file, doc: m, refs: sets.Set[string]{}}, nil
}

func (c *collector) collect(operations sets.Set[string]) (map[string]map[string]sets.Set[string], error) {
	for operation := range operations {
		operationNode, err := c.findByOperationId(operation, c.doc)
		if err != nil {
			return nil, err
		}
		mapEntry(operationNode.(map[string]interface{}), c.refs)
	}
	visited, err := c.collectDependentRefs()
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: failed to collect dependent refs in OpenAPI spec file %s: %w", c.filename, err)
	}

	preserve := map[string]map[string]sets.Set[string]{}
	for ref := range visited {
		node, err := c.parseRef(ref)
		if err != nil {
			return nil, fmt.Errorf("❌ ERROR: failed to parse ref at OpenAPI spec file %s: %w", c.filename, err)
		}
		if preserve[node.section] == nil {
			preserve[node.section] = map[string]sets.Set[string]{}
		}
		if preserve[node.section][node.subsection] == nil {
			preserve[node.section][node.subsection] = sets.Set[string]{}
		}
		preserve[node.section][node.subsection].Insert(node.object)
	}
	return preserve, nil
}

func (c *collector) collectDependentRefs() (sets.Set[string], error) {
	var visited = sets.Set[string]{}
	for c.refs.Len() > 0 {
		operation, _ := c.refs.PopAny()
		if !visited.Has(operation) {
			visited.Insert(operation)
			var current = sets.Set[string]{}
			node, err := c.findByRefObject(operation, c.doc)
			if err != nil {
				return nil, err
			}
			mapEntry(node, current)
			for current.Len() > 0 {
				operation, _ := current.PopAny()
				if !visited.Has(operation) {
					c.refs.Insert(operation)
				}
			}
		}
	}
	return visited, nil
}

func (c *collector) parseRef(ref string) (node, error) {
	if !strings.HasPrefix(ref, "#/") {
		return node{}, fmt.Errorf("invalid $ref: %s, must start with #/ at OpenAPI spec file %s", ref, c.filename)
	}
	parts := strings.Split(ref, "/")
	if len(parts) < 4 {
		return node{}, fmt.Errorf("invalid $ref %s at OpenAPI spec file %s", ref, c.filename)
	}
	return node{section: parts[1], subsection: parts[2], object: parts[3]}, nil
}

func (c *collector) findByRefObject(ref string, m map[string]interface{}) (map[string]interface{}, error) {
	parsedRef, err := c.parseRef(ref)
	if err != nil {
		return nil, err
	}
	section, ok := m[parsedRef.section].(map[string]interface{})
	if !ok {
		return nil, fmt.Errorf("OpenAPI spec file %s has no such section: %s", c.filename, ref)
	}
	subsection, ok := section[parsedRef.subsection].(map[string]interface{})
	if !ok {
		return nil, fmt.Errorf("OpenAPI spec file %s has no such subsection: %s", c.filename, ref)
	}
	object, ok := subsection[parsedRef.object].(map[string]interface{})
	if !ok {
		return nil, fmt.Errorf("OpenAPI spec file %s has no such object: %s", c.filename, ref)
	}

	return object, nil
}

func (c *collector) findByOperationId(operationId string, m map[string]interface{}) (any, error) {
	paths, ok := m["paths"].(map[string]interface{})
	if !ok {
		return nil, fmt.Errorf("OpenAPI spec file %s has no paths", c.filename)
	}
	for _, pathItem := range paths {
		operations, ok := pathItem.(map[string]interface{})
		if !ok {
			continue
		}
		for _, operationDetails := range operations {
			operation, ok := operationDetails.(map[string]interface{})
			if !ok {
				continue
			}
			if operation["operationId"] == operationId {
				return operation, nil
			}
		}
	}
	return nil, fmt.Errorf("operationId %s not found at OpenAPI spec file %s", operationId, c.filename)
}

func entry(e any, refs sets.Set[string]) {
	switch v := e.(type) {
	case map[string]interface{}:
		mapEntry(v, refs)
	case []interface{}:
		sliceEntry(v, refs)
	default:
		return
	}
}

func sliceEntry(s []interface{}, refs sets.Set[string]) {
	for _, v := range s {
		entry(v, refs)
	}
}

func mapEntry(m map[string]interface{}, refs sets.Set[string]) {
	for k, v := range m {
		if k == "$ref" {
			refs.Insert(v.(string))
			continue
		}
		entry(v, refs)
	}
}
