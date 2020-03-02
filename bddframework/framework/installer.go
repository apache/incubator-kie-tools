// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"strings"
)

// InstallerType defines the type of installer for services
type InstallerType string

const (
	cliInstallerKey = "cli"
	crInstallerKey  = "cr"
)

var (
	// CLIInstallerType defines the CLI installer
	CLIInstallerType InstallerType = cliInstallerKey
	// CRInstallerType defines the CR installer
	CRInstallerType InstallerType = crInstallerKey
)

// MustParseInstallerType returns the correct installer type, based on the given string
func MustParseInstallerType(typeStr string) InstallerType {
	switch t := strings.ToLower(typeStr); t {
	case cliInstallerKey:
		return CLIInstallerType
	case crInstallerKey:
		return CRInstallerType
	default:
		panic(fmt.Errorf("Unknown installer type %s", typeStr))
	}
}
