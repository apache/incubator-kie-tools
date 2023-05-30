/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package common

import (
	"fmt"
	"net/http"
	"time"
)

func ReadyCheck(healthCheckURL string, pollInterval time.Duration, portMapping string) {
	ready := make(chan bool)

	go pollReadyCheckURL(healthCheckURL, pollInterval, ready)

	select {
	case <-ready:
		fmt.Println("✅ Kogito Serverless Workflow project is up and running")
		OpenBrowserURL(fmt.Sprintf("http://localhost:%s/q/dev", portMapping))
	case <-time.After(10 * time.Minute):
		fmt.Printf("Timeout reached. Server at %s is not ready.", healthCheckURL)
	}
}

func pollReadyCheckURL(healthCheckURL string, interval time.Duration, ready chan<- bool) {
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
