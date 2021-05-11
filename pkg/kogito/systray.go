package kogito

import (
	"fmt"
	"log"
	"os/exec"
	"runtime"
	"strconv"

	"github.com/kiegroup/kogito-tooling-go/pkg/images"
	"github.com/getlantern/systray"
)

type KogitoSystray struct {
	controller     *Proxy
	runnerPortItem *systray.MenuItem
	openModeler    *systray.MenuItem
	restartItem    *systray.MenuItem
	StartStopItem  *systray.MenuItem
	StatusItem     *systray.MenuItem
}

func (self *KogitoSystray) Run() {
	systray.Run(self.onReady, self.onExit)
}

func (self *KogitoSystray) onReady() {
	systray.SetTemplateIcon(images.Data, images.Data)
	systray.SetTooltip(NAME)

	self.mainSection()
	systray.AddSeparator()
	self.operationSection()
	systray.AddSeparator()
	quitItem := systray.AddMenuItem(QUIT, QUIT)

	self.StartStopItem.SetTitle(STARTING)
	go self.controller.Start()


	for {
		select {
		case <-self.openModeler.ClickedCh:
			self.openBrowser(MODELER_LINK)
		case <-self.restartItem.ClickedCh:
			if self.controller.Started {
				self.Stop()
				self.Start()
			} else {
				self.Start()
			}
		case <-self.StartStopItem.ClickedCh:
			if self.controller.Started {
				self.Stop()
			} else {
				self.Start()
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
	self.openModeler = systray.AddMenuItem(BUSINESS_MODELER, BUSINESS_MODELER)

	self.StatusItem = systray.AddMenuItem(SERVER_STATUS, SERVER_STATUS)
	self.StatusItem.Disable()

	self.informationMenu()
}

func (self *KogitoSystray) informationMenu() {
	information := systray.AddMenuItem(INFORMATION, INFORMATION)
	port := information.AddSubMenuItem(INFORMATION_PORT+": "+strconv.Itoa(self.controller.Port), INFORMATION_PORT)
	port.Disable()
	self.runnerPortItem = information.AddSubMenuItem(INFORMATION_RUNNER_PORT+": "+self.getRunnerPortStatus(), INFORMATION_RUNNER_PORT)
	self.runnerPortItem.Disable()

	businessModelerUrlItem := information.AddSubMenuItem(INFORMATION_BUSINESS_MODELER_URL+": "+MODELER_LINK, INFORMATION_BUSINESS_MODELER_URL)
	businessModelerUrlItem.Disable()
}

func (self *KogitoSystray) operationSection() {
	self.StartStopItem = systray.AddMenuItem(START, START)
	self.restartItem = systray.AddMenuItem(RESTART, RESTART)

}

func (self *KogitoSystray) Refresh() {
	self.refreshRunnerPort()
	self.changeStartStop()
	self.changeStatus()
}

func (self *KogitoSystray) refreshRunnerPort() {
	if self.runnerPortItem != nil {
		self.runnerPortItem.SetTitle(INFORMATION_RUNNER_PORT + ": " + self.getRunnerPortStatus())
	}
}

func (self *KogitoSystray) getRunnerPortStatus() string {
	status := NOT_STARTED
	if self.controller.RunnerPort != 0 {
		status = strconv.Itoa(self.controller.RunnerPort)
	}
	return status
}

func (self *KogitoSystray) changeStatus() {
	if self.controller.Started {
		self.StatusItem.SetTitle(SERVER_STATUS_ON)
	} else {
		self.StatusItem.SetTitle(SERVER_STATUS_OFF)
	}
}

func (self *KogitoSystray) changeStartStop() {
	if self.controller.Started {
		self.StartStopItem.SetTitle(STOP)
	} else {
		self.StartStopItem.SetTitle(START)
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
