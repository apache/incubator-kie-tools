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

type KogitoSystray struct {
	server                   *Proxy
	runnerPortItem           *systray.MenuItem
	openModeler              *systray.MenuItem
	StartStopItem            *systray.MenuItem
	ToggleInsecureSkipVerify *systray.MenuItem
}

func (ks *KogitoSystray) Run() {
	systray.Run(ks.onReady, ks.onExit)
}

func (ks *KogitoSystray) onReady() {
	systray.SetTemplateIcon(images.DataStarted, images.DataStarted)
	systray.SetTooltip(APPNAME)

	ks.mainSection()
	systray.AddSeparator()
	ks.operationSection()
	systray.AddSeparator()
	quitItem := systray.AddMenuItem(QUIT, "")

	ks.StartStopItem.SetTitle(STARTING)
	go ks.server.Start()

	for {
		select {
		case <-ks.openModeler.ClickedCh:
			ks.openBrowser(MODELER_LINK)
		case <-ks.StartStopItem.ClickedCh:
			if ks.server.Started {
				ks.Stop()
			} else {
				ks.Start()
			}
		case <-ks.ToggleInsecureSkipVerify.ClickedCh:
			if ks.server.InsecureSkipVerify {
				ks.server.InsecureSkipVerify = false
				ks.ToggleInsecureSkipVerify.SetTitle(ALLOW_INSECURE_SKIP_VERIFY)
			} else {
				ks.server.InsecureSkipVerify = true
				ks.ToggleInsecureSkipVerify.SetTitle(DISALLOW_INSECURE_SKIP_VERIFY)
			}
		case <-quitItem.ClickedCh:
			ks.Stop()
			systray.Quit()
			return
		}
	}
}

func (ks *KogitoSystray) onExit() {

}

func (ks *KogitoSystray) Start() {
	fmt.Println("Executing Start command")
	ks.StartStopItem.SetTitle(STARTING)
	ks.server.Start()
}

func (ks *KogitoSystray) Stop() {
	fmt.Println("Executing Stop command")
	ks.StartStopItem.SetTitle(STOPPING)
	ks.server.Stop()
}

func (ks *KogitoSystray) mainSection() {
	ks.openModeler = systray.AddMenuItem(BUSINESS_MODELER, "")

	systray.AddSeparator()

	version := systray.AddMenuItem(VERSION+": "+metadata.Version, "")
	version.Disable()

	ks.runnerPortItem = systray.AddMenuItem(INFORMATION_PORTS+": "+ks.server.Port+" -> "+ks.getRunnerPortStatus(), "")
	ks.runnerPortItem.Disable()
}

func (ks *KogitoSystray) operationSection() {
	if ks.server.InsecureSkipVerify {
		ks.ToggleInsecureSkipVerify = systray.AddMenuItem(DISALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of ks-signed certificates")
	} else {
		ks.ToggleInsecureSkipVerify = systray.AddMenuItem(ALLOW_INSECURE_SKIP_VERIFY, "Toggle InsecureSkipVerify allowing or not the use of ks-signed certificates")
	}
	ks.StartStopItem = systray.AddMenuItem(START, "")

}

func (ks *KogitoSystray) Refresh() {
	ks.refreshRunnerPort()
	ks.changeStartStop()
	ks.changeIcon()
}

func (ks *KogitoSystray) refreshRunnerPort() {
	ks.runnerPortItem.SetTitle(INFORMATION_PORTS + ": " + ks.server.Port + " -> " + ks.getRunnerPortStatus())
}

func (ks *KogitoSystray) getRunnerPortStatus() string {
	status := NOT_STARTED
	if ks.server.RunnerPort != "0" {
		status = ks.server.RunnerPort
	}
	return status
}

func (ks *KogitoSystray) SetLoading() {
	if runtime.GOOS == "linux" {
		systray.SetTemplateIcon(images.DataLoadingLinux, images.DataLoadingLinux)
	} else {
		systray.SetTemplateIcon(images.DataLoading, images.DataLoading)
	}
}

func (ks *KogitoSystray) changeStartStop() {
	if ks.server.Started {
		ks.StartStopItem.SetTitle(STOP)
	} else {
		ks.StartStopItem.SetTitle(START)
	}
}

func (ks *KogitoSystray) changeIcon() {
	if ks.server.Started {
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

func (ks *KogitoSystray) openBrowser(url string) {
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
