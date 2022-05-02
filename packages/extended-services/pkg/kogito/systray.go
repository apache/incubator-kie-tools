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
	"strconv"

	"github.com/getlantern/systray"
	"github.com/kiegroup/kie-tools/extended-services/pkg/config"
	"github.com/kiegroup/kie-tools/extended-services/pkg/images"
)

type KogitoSystray struct {
	controller               *Proxy
	runnerPortItem           *systray.MenuItem
	openModeler              *systray.MenuItem
	StartStopItem            *systray.MenuItem
	ToggleInsecureSkipVerify *systray.MenuItem
}

func (self *KogitoSystray) Run() {
	systray.Run(self.onReady, self.onExit)
}

func (self *KogitoSystray) onReady() {
	systray.SetTemplateIcon(images.DataStarted, images.DataStarted)
	systray.SetTooltip(APPNAME)

	self.mainSection()
	systray.AddSeparator()
	self.operationSection()
	systray.AddSeparator()
	quitItem := systray.AddMenuItem(QUIT, "")

	self.StartStopItem.SetTitle(STARTING)
	go self.controller.Start()

	for {
		select {
		case <-self.openModeler.ClickedCh:
			self.openBrowser(MODELER_LINK)
		case <-self.StartStopItem.ClickedCh:
			if self.controller.Started {
				self.Stop()
			} else {
				self.Start()
			}
		case <-self.ToggleInsecureSkipVerify.ClickedCh:
			if self.controller.InsecureSkipVerify {
				self.controller.InsecureSkipVerify = false
				self.ToggleInsecureSkipVerify.SetTitle(ALLOW_INSECURE_SKIP_VERIFY)
			} else {
				self.controller.InsecureSkipVerify = true
				self.ToggleInsecureSkipVerify.SetTitle(DISALLOW_INSECURE_SKIP_VERIFY)
			}
		case <-quitItem.ClickedCh:
			self.Stop()
			systray.Quit()
			return
		}
	}
}

func (self *KogitoSystray) onExit() {

}

func (self *KogitoSystray) Start() {
	fmt.Println("Executing Start command")
	self.StartStopItem.SetTitle(STARTING)
	self.controller.Start()
}

func (self *KogitoSystray) Stop() {
	fmt.Println("Executing Stop command")
	self.StartStopItem.SetTitle(STOPPING)
	self.controller.Stop()
}

func (self *KogitoSystray) mainSection() {
	self.openModeler = systray.AddMenuItem(BUSINESS_MODELER, "")

	systray.AddSeparator()

	var config config.Config
	conf := config.GetConfig()

	version := systray.AddMenuItem(VERSION+": "+conf.GetConfig().App.Version, "")
	version.Disable()

	self.runnerPortItem = systray.AddMenuItem(INFORMATION_PORTS+": "+strconv.Itoa(self.controller.Port)+" -> "+self.getRunnerPortStatus(), "")
	self.runnerPortItem.Disable()
}

func (self *KogitoSystray) operationSection() {
	if self.controller.InsecureSkipVerify {
		self.ToggleInsecureSkipVerify = systray.AddMenuItem(DISALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of self-signed certificates")
	} else {
		self.ToggleInsecureSkipVerify = systray.AddMenuItem(ALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of self-signed certificates")
	}
	self.StartStopItem = systray.AddMenuItem(START, "")

}

func (self *KogitoSystray) Refresh() {
	self.refreshRunnerPort()
	self.changeStartStop()
	self.changeIcon()
}

func (self *KogitoSystray) refreshRunnerPort() {
	self.runnerPortItem.SetTitle(INFORMATION_PORTS + ": " + strconv.Itoa(self.controller.Port) + " -> " + self.getRunnerPortStatus())
}

func (self *KogitoSystray) getRunnerPortStatus() string {
	status := NOT_STARTED
	if self.controller.RunnerPort != 0 {
		status = strconv.Itoa(self.controller.RunnerPort)
	}
	return status
}

func (self *KogitoSystray) SetLoading() {
	if runtime.GOOS == "linux" {
		systray.SetTemplateIcon(images.DataLoadingLinux, images.DataLoadingLinux)
	} else {
		systray.SetTemplateIcon(images.DataLoading, images.DataLoading)
	}
}

func (self *KogitoSystray) changeStartStop() {
	if self.controller.Started {
		self.StartStopItem.SetTitle(STOP)
	} else {
		self.StartStopItem.SetTitle(START)
	}
}

func (self *KogitoSystray) changeIcon() {
	if self.controller.Started {
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

func (self *KogitoSystray) openBrowser(url string) {
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
