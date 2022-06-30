/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package main

import (
	_ "embed"
	"flag"

	"github.com/kiegroup/kie-tools/extended-services/pkg/config"
	"github.com/kiegroup/kie-tools/extended-services/pkg/kogito"
)

// Embed the jitrunner into the runner variable, to produce a self-contained binary.
//go:embed jitexecutor
var jitexecutor []byte

func main() {
	var config config.Config
	conf := config.GetConfig()
	port := flag.Int("p", conf.Proxy.Port, "KIE Sandbox Extended Services Port")
	insecureSkipVerify := conf.Proxy.InsecureSkipVerify
	flag.Parse()
	kogito.Systray(*port, jitexecutor, insecureSkipVerify)
}
