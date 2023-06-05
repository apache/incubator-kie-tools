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

package pkg

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
	"github.com/kiegroup/kie-tools/packages/extended-services/pkg/metadata"
	"github.com/phayes/freeport"
)

type Proxy struct {
	View               *Systray
	Started            bool
	URL                string
	Port               string
	RunnerPort         string
	InsecureSkipVerify bool

	cmd             *exec.Cmd
	jitexecutorPath string
	server          *http.Server
}

func NewProxy(port string, jitexecutor []byte) *Proxy {
	return &Proxy{
		jitexecutorPath:    createJitExecutor(jitexecutor),
		Started:            false,
		Port:               port,
		InsecureSkipVerify: false,
	}
}

func (p *Proxy) Start() {
	port, err := freeport.GetFreePort()
	if err != nil {
		log.Fatal(err)
	}
	p.RunnerPort = strconv.Itoa(port)
	p.URL = "http://127.0.0.1:" + p.RunnerPort

	p.cmd = exec.Command(p.jitexecutorPath, "-Dquarkus.http.port="+p.RunnerPort, "-Dquarkus.http.cors=true", "-Dquarkus.http.cors.origins=/.*/")
	stdout, _ := p.cmd.StdoutPipe()
	go func() {
		scanner := bufio.NewScanner(stdout)
		for scanner.Scan() {
			msg := scanner.Text()
			fmt.Printf("msg: %s\n", msg)
		}
	}()

	err = p.cmd.Start()
	if err != nil {
		log.Fatal(err)
	}

	router := mux.NewRouter()
	router.PathPrefix("/cors-proxy").HandlerFunc(p.corsProxyHandler())
	router.PathPrefix("/ping").HandlerFunc(p.pingHandler())
	router.PathPrefix("/").HandlerFunc(p.jitExecutorHandler())

	addr := metadata.Ip + ":" + p.Port

	p.server = &http.Server{
		Handler:      router,
		Addr:         addr,
		WriteTimeout: 15 * time.Second,
		ReadTimeout:  15 * time.Second,
	}

	fmt.Printf("Server started: %s \n", addr)

	go p.server.ListenAndServe()
	go p.GracefulShutdown()

	p.Refresh()
}

func (p *Proxy) GracefulShutdown() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	<-c
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	log.Println("Signal detected, shutting down...")
	p.Stop()
	p.server.Shutdown(ctx)
	os.Exit(0)
}

func (p *Proxy) Stop() {
	log.Println("Shutting down")

	p.cmd.Process.Kill()

	ctx, cancel := context.WithTimeout(context.TODO(), time.Second*15)
	defer cancel()

	if err := p.server.Shutdown(ctx); err != nil {
		log.Fatalf("Server Shutdown Failed:%+v", err)
	}
	log.Println("Shutdown complete")

	p.RunnerPort = "0"
	p.Started = false
	p.View.Refresh()
}

func (p *Proxy) Refresh() {
	p.View.SetLoading()

	for !p.Started {
		resp, err := http.Get(p.URL)
		if err != nil {
			fmt.Println(err.Error())
		} else {
			if resp.StatusCode == 200 {
				p.Started = true
			} else {
				fmt.Println(strconv.Itoa(resp.StatusCode) + " -> " + resp.Status)
			}
		}
		time.Sleep(1 * time.Second)
	}

	p.View.Refresh()
}

func (p *Proxy) corsProxyHandler() func(rw http.ResponseWriter, req *http.Request) {
	return func(rw http.ResponseWriter, req *http.Request) {
		if req.Method == "OPTIONS" {
			rw.Header().Set("Access-Control-Allow-Origin", "*")
			rw.Header().Set("Access-Control-Allow-Methods", "*")
			rw.Header().Set("Access-Control-Allow-Headers", "*")
			return
		}

		targetUrl, err := url.Parse(req.Header.Get("Target-Url"))
		if err != nil {
			log.Fatal(err)
		}
		emptyUrl, _ := url.Parse("")
		req.URL = emptyUrl
		req.Host = req.URL.Host

		req.Header.Del("Origin")

		proxy := httputil.NewSingleHostReverseProxy(targetUrl)

		// tolerate p-signed certificates
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
				InsecureSkipVerify: p.InsecureSkipVerify,
			},
		}

		proxy.ModifyResponse = func(resp *http.Response) error {
			resp.Header.Set("Access-Control-Allow-Origin", "*")
			resp.Header.Set("Access-Control-Allow-Methods", "*")
			resp.Header.Set("Access-Control-Allow-Headers", "*")
			return nil
		}
		proxy.ServeHTTP(rw, req)
	}
}

func (p *Proxy) jitExecutorHandler() func(rw http.ResponseWriter, req *http.Request) {
	return func(rw http.ResponseWriter, req *http.Request) {
		if req.Method == "OPTIONS" {
			rw.Header().Set("Access-Control-Allow-Origin", "*")
			rw.Header().Set("Access-Control-Allow-Methods", "*")
			rw.Header().Set("Access-Control-Allow-Headers", "*")
			return
		}

		target, err := url.Parse(p.URL)
		if err != nil {
			log.Fatal(err)
		}
		proxy := httputil.NewSingleHostReverseProxy(target)

		req.Host = req.URL.Host
		proxy.ServeHTTP(rw, req)
	}
}

func (p *Proxy) pingHandler() func(rw http.ResponseWriter, req *http.Request) {
	return func(rw http.ResponseWriter, req *http.Request) {
		rw.Header().Set("Access-Control-Allow-Origin", "*")
		rw.Header().Set("Access-Control-Allow-Methods", "GET")

		conf := GetPingResponse(p.InsecureSkipVerify, p.Started)
		rw.WriteHeader(http.StatusOK)
		json, _ := json.Marshal(conf)
		_, err := rw.Write(json)
		if err != nil {
			log.Fatal(err)
		}
	}
}

func createJitExecutor(jitexecutor []byte) string {
	cacheDir, err := os.UserCacheDir()
	if err != nil {
		log.Fatal(err)
	}

	cachePath := filepath.Join(cacheDir, "org.kie.kogito")
	if _, err := os.Stat(cachePath); os.IsNotExist(err) {
		if err = os.Mkdir(cachePath, os.ModePerm); err != nil {
			log.Fatal(err)
		}
	}

	var jitExecutorPath string
	if runtime.GOOS == "windows" {
		jitExecutorPath = filepath.Join(cachePath, "runner.exe")
	} else {
		jitExecutorPath = filepath.Join(cachePath, "runner")
	}

	_, err = os.Stat(jitExecutorPath)
	if err == nil {
		os.Remove(jitExecutorPath)
	}

	f, err := os.Create(jitExecutorPath)
	if err != nil {
		log.Fatal(err)
	}
	f.Chmod(0777)

	_, err = f.Write(jitexecutor)
	if err != nil {
		log.Fatal(err)
	}

	f.Close()
	return jitExecutorPath
}
