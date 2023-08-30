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

package common

import (
	"fmt"
	"net/http"
	"time"
)

func ReadyCheck(healthCheckURL string, pollInterval time.Duration, portMapping string, openDevUI bool) {
	ready := make(chan bool)

	go PollReadyCheckURL(healthCheckURL, pollInterval, ready)
	select {
	case <-ready:
		fmt.Println("\n✅ SonataFlow project is up and running")
		if openDevUI {
			OpenBrowserURL(fmt.Sprintf("http://localhost:%s/q/dev", portMapping))
		}
	case <-time.After(10 * time.Minute):
		fmt.Printf("Timeout reached. Server at %s is not ready.", healthCheckURL)
	}
}

func PollReadyCheckURL(healthCheckURL string, interval time.Duration, ready chan<- bool) {
	client := http.Client{
		Timeout: 5 * time.Second,
	}

	for {
		resp, err := client.Get(healthCheckURL)
		if err == nil && resp.StatusCode == http.StatusOK {
			if resp.StatusCode == http.StatusOK {
				resp.Body.Close() // close the response body right after checking status
				ready <- true
				return
			}
			resp.Body.Close()
		}
		time.Sleep(interval)
	}
}
