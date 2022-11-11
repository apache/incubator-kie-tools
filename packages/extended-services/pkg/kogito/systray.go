//go:build !headless
// +build !headless

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

package kogito

import (
	"fmt"
	"log"
	"os/exec"
	"runtime"

	"github.com/getlantern/systray"
	"github.com/kiegroup/kie-tools/packages/extended-services/pkg/images"
	"github.com/kiegroup/kie-tools/packages/extended-services/pkg/metadata"
)

type Systray struct {
	Server                   *Proxy
	StartStopItem            *systray.MenuItem
	ToggleInsecureSkipVerify *systray.MenuItem

	runnerPortItem *systray.MenuItem
	openModeler    *systray.MenuItem
}

func (s *Systray) Run() {
	systray.Run(s.onReady, s.onExit)
}

func (s *Systray) onReady() {
	systray.SetTemplateIcon(images.DataStarted, images.DataStarted)
	systray.SetTooltip(APPNAME)

	s.mainSection()
	systray.AddSeparator()
	s.operationSection()
	systray.AddSeparator()
	quitItem := systray.AddMenuItem(QUIT, "")

	s.StartStopItem.SetTitle(STARTING)
	go s.Server.Start()

	for {
		select {
		case <-s.openModeler.ClickedCh:
			s.openBrowser(MODELER_LINK)
		case <-s.StartStopItem.ClickedCh:
			if s.Server.Started {
				s.Stop()
			} else {
				s.Start()
			}
		case <-s.ToggleInsecureSkipVerify.ClickedCh:
			if s.Server.InsecureSkipVerify {
				s.Server.InsecureSkipVerify = false
				s.ToggleInsecureSkipVerify.SetTitle(ALLOW_INSECURE_SKIP_VERIFY)
			} else {
				s.Server.InsecureSkipVerify = true
				s.ToggleInsecureSkipVerify.SetTitle(DISALLOW_INSECURE_SKIP_VERIFY)
			}
		case <-quitItem.ClickedCh:
			s.Stop()
			systray.Quit()
			return
		}
	}
}

func (s *Systray) onExit() {

}

func (s *Systray) Start() {
	fmt.Println("Executing Start command")
	s.StartStopItem.SetTitle(STARTING)
	s.Server.Start()
}

func (s *Systray) Stop() {
	fmt.Println("Executing Stop command")
	s.StartStopItem.SetTitle(STOPPING)
	s.Server.Stop()
}

func (s *Systray) mainSection() {
	s.openModeler = systray.AddMenuItem(BUSINESS_MODELER, "")

	systray.AddSeparator()

	version := systray.AddMenuItem(VERSION+": "+metadata.Version, "")
	version.Disable()

	s.runnerPortItem = systray.AddMenuItem(INFORMATION_PORTS+": "+s.Server.Port+" -> "+s.getRunnerPortStatus(), "")
	s.runnerPortItem.Disable()
}

func (s *Systray) operationSection() {
	if s.Server.InsecureSkipVerify {
		s.ToggleInsecureSkipVerify = systray.AddMenuItem(DISALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of s-signed certificates")
	} else {
		s.ToggleInsecureSkipVerify = systray.AddMenuItem(ALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of s-signed certificates")
	}
	s.StartStopItem = systray.AddMenuItem(START, "")

}

func (s *Systray) Refresh() {
	s.refreshRunnerPort()
	s.changeStartStop()
	s.changeIcon()
}

func (s *Systray) refreshRunnerPort() {
	s.runnerPortItem.SetTitle(INFORMATION_PORTS + ": " + s.Server.Port + " -> " + s.getRunnerPortStatus())
}

func (s *Systray) getRunnerPortStatus() string {
	status := NOT_STARTED
	if s.Server.RunnerPort != "0" {
		status = s.Server.RunnerPort
	}
	return status
}

func (s *Systray) SetLoading() {
	if runtime.GOOS == "linux" {
		systray.SetTemplateIcon(images.DataLoadingLinux, images.DataLoadingLinux)
	} else {
		systray.SetTemplateIcon(images.DataLoading, images.DataLoading)
	}
}

func (s *Systray) changeStartStop() {
	if s.Server.Started {
		s.StartStopItem.SetTitle(STOP)
	} else {
		s.StartStopItem.SetTitle(START)
	}
}

func (s *Systray) changeIcon() {
	if s.Server.Started {
		if runtime.GOOS == "linux" {
			systray.SetTemplateIcon(images.DataStartedLinux, images.DataStartedLinux)
		} else {
			systray.SetTemplateIcon(images.DataStarted, images.DataStarted)
		}
	} else {
		if runtime.GOOS == "linux" {
			systray.SetTemplateIcon(images.DataStoppedLinux, images.DataStoppedLinux)
		} else {
			systray.SetTemplateIcon(images.DataStopped, images.DataStopped)
		}
	}
}

func (s *Systray) openBrowser(url string) {
	var err error

	switch runtime.GOOS {
	case "linux":
		err = exec.Command("xdg-open", url).Start()
	case "windows":
		err = exec.Command("rundll32", "url.dll,FileProtocolHandler", url).Start()
	case "darwin":
		err = exec.Command("open", url).Start()
	default:
		err = fmt.Errorf("unsupported platform")
	}
	if err != nil {
		log.Fatal(err)
	}
}
