package main

import (
	_ "embed"
	"flag"

	"github.com/kiegroup/kogito-tooling-go/pkg/config"
	"github.com/kiegroup/kogito-tooling-go/pkg/kogito"
)

// Embed the jitrunner into the runner variable, to produce a self-contained binary.
//go:embed jitexecutor
var jitexecutor []byte

func main() {
	var config config.Config
	conf := config.GetConfig()
	port := flag.Int("p", conf.Proxy.Port, "KIE Tooling Extended Services Port")
	flag.Parse()
	kogito.Systray(*port, jitexecutor)
}
