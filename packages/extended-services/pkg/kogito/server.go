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
	"bufio"
	"context"
	"crypto/tls"
	"net"

	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"os/exec"
	"os/signal"
	"path/filepath"
	"runtime"
	"strconv"
	"time"

	"github.com/gorilla/mux"
	"github.com/kiegroup/kie-tools/extended-services/pkg/config"
	"github.com/kiegroup/kie-tools/extended-services/pkg/utils"
	"github.com/phayes/freeport"
)

type Proxy struct {
	view               *KogitoSystray
	srv                *http.Server
	cmd                *exec.Cmd
	Started            bool
	URL                string
	Port               int
	RunnerPort         int
	jitexecutorPath    string
	InsecureSkipVerify bool
}

func NewProxy(port int, jitexecutor []byte, insecureSkipVerify bool) *Proxy {
	proxy := &Proxy{Started: false}
	proxy.jitexecutorPath = proxy.createJitExecutor(jitexecutor)
	proxy.Port = port
	proxy.InsecureSkipVerify = insecureSkipVerify
	return proxy
}

func (self *Proxy) Start() {

	var config config.Config
	conf := config.GetConfig()

	self.RunnerPort = getFreePort()
	runnerPort := strconv.Itoa(self.RunnerPort)
	self.URL = "http://127.0.0.1:" + runnerPort
	target, err := url.Parse(self.URL)
	utils.Check(err)

	self.cmd = exec.Command(self.jitexecutorPath, "-Dquarkus.http.port="+runnerPort)

	stdout, _ := self.cmd.StdoutPipe()

	go func() {
		scanner := bufio.NewScanner(stdout)
		for scanner.Scan() {
			msg := scanner.Text()
			fmt.Printf("msg: %s\n", msg)
		}
	}()

	go startRunner(self.cmd)

	proxy := httputil.NewSingleHostReverseProxy(target)

	r := mux.NewRouter()
	r.PathPrefix("/devsandbox").HandlerFunc(devSandboxHandler(self))
	r.PathPrefix("/ping").HandlerFunc(pingHandler(self.Port))
	r.PathPrefix("/").HandlerFunc(proxyHandler(proxy, self.cmd))

	addr := conf.Proxy.IP + ":" + strconv.Itoa(self.Port)

	self.srv = &http.Server{
		Handler:      r,
		Addr:         addr,
		WriteTimeout: 15 * time.Second,
		ReadTimeout:  15 * time.Second,
	}

	fmt.Printf("Server started: %s \n", addr)

	go self.srv.ListenAndServe()
	go self.GracefulShutdown()

	self.Refresh()
}

func (self *Proxy) GracefulShutdown() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	<-c
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	log.Println("Signal detected, shutting down...")
	self.Stop()
	self.srv.Shutdown(ctx)
	os.Exit(0)
}

func (self *Proxy) Stop() {
	log.Println("Shutting down")

	stopRunner(self.cmd)

	ctx, cancel := context.WithTimeout(context.TODO(), time.Second*15)
	defer cancel()

	if err := self.srv.Shutdown(ctx); err != nil {
		log.Fatalf("Server Shutdown Failed:%+v", err)
	}
	log.Println("Shutdown complete")

	self.RunnerPort = 0
	self.Refresh()
}

func (self *Proxy) Refresh() {

	self.view.SetLoading()

	started := false
	countDown := 5
	retry := true

	for countDown > 0 && retry {
		resp, err := http.Get(self.URL)
		if err != nil {
			fmt.Println(err.Error())
			retry = true
			countDown--
		} else {
			fmt.Println(strconv.Itoa(resp.StatusCode) + " -> " + resp.Status)
			if resp.StatusCode == 200 {
				started = true
			}
			retry = false
		}
		time.Sleep(1 * time.Second)
	}

	self.Started = started
	self.view.Refresh()
}

func (self *Proxy) createJitExecutor(jitexecutor []byte) string {
	cacheDir, cacheError := os.UserCacheDir()
	utils.Check(cacheError)

	cachePath := filepath.Join(cacheDir, "org.kogito")

	if _, err := os.Stat(cachePath); os.IsNotExist(err) {
		os.Mkdir(cachePath, os.ModePerm)
	}

	var jitexecutorPath string

	if runtime.GOOS == "windows" {
		jitexecutorPath = filepath.Join(cachePath, "runner.exe")
	} else {
		jitexecutorPath = filepath.Join(cachePath, "runner")
	}

	if _, err := os.Stat(jitexecutorPath); err == nil {
		os.Remove(jitexecutorPath)
	}

	f, err := os.Create(jitexecutorPath)
	utils.Check(err)

	f.Chmod(0777)

	_, err = f.Write(jitexecutor)
	utils.Check(err)
	f.Close()
	return jitexecutorPath
}

func devSandboxHandler(self *Proxy) func(w http.ResponseWriter, r *http.Request) {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method == "OPTIONS" {
			w.Header().Add("Access-Control-Allow-Origin", "*")
			w.Header().Add("Access-Control-Allow-Methods", "*")
			w.Header().Add("Access-Control-Allow-Headers", "*")
			return
		}

		targetUrl, err := url.Parse(r.Header.Get("Target-Url"))
		utils.Check(err)
		emptyUrl, _ := url.Parse("")
		r.URL = emptyUrl
		r.Host = r.URL.Host

		proxy := httputil.NewSingleHostReverseProxy(targetUrl)

		// tolerate self-signed certificates
		proxy.Transport = &http.Transport{
			Proxy: http.ProxyFromEnvironment,
			DialContext: (&net.Dialer{
				Timeout:   30 * time.Second,
				KeepAlive: 30 * time.Second,
			}).DialContext,
			ForceAttemptHTTP2:     true,
			MaxIdleConns:          10,
			IdleConnTimeout:       60 * time.Second,
			TLSHandshakeTimeout:   10 * time.Second,
			ExpectContinueTimeout: 1 * time.Second,
			TLSClientConfig: &tls.Config{
				InsecureSkipVerify: self.InsecureSkipVerify,
			},
		}

		proxy.ModifyResponse = func(resp *http.Response) error {
			resp.Header.Add("Access-Control-Allow-Origin", "*")
			resp.Header.Add("Access-Control-Allow-Methods", "*")
			resp.Header.Add("Access-Control-Allow-Headers", "*")
			return nil
		}
		proxy.ServeHTTP(w, r)
	}
}

func proxyHandler(proxy *httputil.ReverseProxy, cmd *exec.Cmd) func(w http.ResponseWriter, r *http.Request) {
	return func(w http.ResponseWriter, r *http.Request) {
		r.Host = r.URL.Host
		proxy.ServeHTTP(w, r)
	}
}

func pingHandler(port int) func(w http.ResponseWriter, r *http.Request) {
	return func(w http.ResponseWriter, r *http.Request) {
		w.Header().Add("Access-Control-Allow-Origin", "*")
		w.Header().Add("Access-Control-Allow-Methods", "GET")
		var config config.Config
		conf := config.GetConfig()
		conf.Proxy.Port = port
		w.WriteHeader(http.StatusOK)
		json, _ := json.Marshal(conf)
		w.Write(json)
	}
}

func startRunner(cmd *exec.Cmd) {
	utils.Check(cmd.Start())
}

func stopRunner(cmd *exec.Cmd) {
	cmd.Process.Kill()
}

func getFreePort() int {
	port, err := freeport.GetFreePort()
	utils.Check(err)
	return port
}
