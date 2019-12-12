// Copyright 2019 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package e2e

import (
	"fmt"
	"time"
)

func waitFor(display string, timeout time.Duration, interval time.Duration, condition func() (bool, error)) error {
	fmt.Print("Wait for " + display)

	timeoutC := time.After(timeout)
	tick := time.NewTicker(interval)

	for {
		select {
		case <-timeoutC:
			return fmt.Errorf("Timeout waiting for %s", display)
		case <-tick.C:
			ok, err := condition()
			if err != nil || ok {
				tick.Stop()
				fmt.Println("")
				if err != nil {
					return err
				} else if ok {
					return nil
				}
			}
			fmt.Print(".")
		}
	}
}
