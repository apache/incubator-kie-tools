//go:build !headless
// +build !headless

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

package pkg

import (
	"fmt"
	"log"
	"os/exec"
	"runtime"

	"fyne.io/systray"
	"github.com/apache/incubator-kie-tools/packages/extended-services/pkg/images"
	"github.com/apache/incubator-kie-tools/packages/extended-services/pkg/metadata"
)

type Systray struct {
	Server        *Proxy
	StartStopItem *systray.MenuItem

	runnerPortItem *systray.MenuItem
	openKieSandbox *systray.MenuItem
}

func (s *Systray) Run() {
	systray.Run(s.onReady, s.onExit)
}

func (s *Systray) onReady() {
	systray.SetTemplateIcon(images.DataStarted, images.DataStarted)
	systray.SetTooltip(metadata.APPNAME)

	s.mainSection()
	systray.AddSeparator()
	s.operationSection()
	systray.AddSeparator()
	quitItem := systray.AddMenuItem(metadata.QUIT, "")

	s.StartStopItem.SetTitle(metadata.STARTING)
	go s.Server.Start()

	for {
		select {
		case <-s.openKieSandbox.ClickedCh:
			s.openBrowser(metadata.KieSandboxUrl)
		case <-s.StartStopItem.ClickedCh:
			if s.Server.Started {
				s.Stop()
			} else {
				s.Start()
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
	s.StartStopItem.SetTitle(metadata.STARTING)
	s.Server.Start()
}

func (s *Systray) Stop() {
	fmt.Println("Executing Stop command")
	s.StartStopItem.SetTitle(metadata.STOPPING)
	s.Server.Stop()
}

func (s *Systray) mainSection() {
	s.openKieSandbox = systray.AddMenuItem(metadata.OPEN_KIE_SANDBOX, "")

	systray.AddSeparator()

	version := systray.AddMenuItem(metadata.VERSION+": "+metadata.Version, "")
	version.Disable()

	s.runnerPortItem = systray.AddMenuItem(metadata.INFORMATION_PORTS+": "+s.Server.Port+" -> "+s.getRunnerPortStatus(), "")
	s.runnerPortItem.Disable()
}

func (s *Systray) operationSection() {
	s.StartStopItem = systray.AddMenuItem(metadata.START, "")
}

func (s *Systray) Refresh() {
	s.refreshRunnerPort()
	s.changeStartStop()
	s.changeIcon()
}

func (s *Systray) refreshRunnerPort() {
	s.runnerPortItem.SetTitle(metadata.INFORMATION_PORTS + ": " + s.Server.Port + " -> " + s.getRunnerPortStatus())
}

func (s *Systray) getRunnerPortStatus() string {
	status := metadata.NOT_STARTED
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
		s.StartStopItem.SetTitle(metadata.STOP)
	} else {
		s.StartStopItem.SetTitle(metadata.START)
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
