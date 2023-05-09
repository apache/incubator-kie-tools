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
