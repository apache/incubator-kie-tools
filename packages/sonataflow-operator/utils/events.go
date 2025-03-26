// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package utils

import (
	"context"
	"fmt"
	"net/http"

	cloudevents "github.com/cloudevents/sdk-go/v2"
	cehttp "github.com/cloudevents/sdk-go/v2/protocol/http"
)

// SendCloudEvent Sends a cloud event to the given url using the http protocol binding. By default, events are sent in
// binary mode.
func SendCloudEvent(event *cloudevents.Event, url string) error {
	return SendCloudEventWithContext(event, context.TODO(), url)
}

// SendCloudEventWithContext Sends a cloud event to the given url using the http protocol binding. By default, events
// are sent in binary mode.
func SendCloudEventWithContext(event *cloudevents.Event, ctx context.Context, url string) error {
	targetCtx := cloudevents.ContextWithTarget(ctx, url)
	p, err := cloudevents.NewHTTP()
	if err != nil {
		return err
	}
	c, err := cloudevents.NewClient(p, cloudevents.WithTimeNow(), cloudevents.WithUUIDs())
	if err != nil {
		return err
	}
	res := c.Send(targetCtx, *event)
	if cloudevents.IsUndelivered(res) {
		return fmt.Errorf("failed to send cloud event to url: %s, err: %s", url, res.Error())
	} else {
		var httpResult *cehttp.Result
		if cloudevents.ResultAs(res, &httpResult) {
			if !resultOK(httpResult) {
				return fmt.Errorf("failed to send cloud event to url: %s, err: %s", url, httpResult.Error())
			}
		} else {
			return fmt.Errorf("failed to send cloud event to url: %s, Send did not return an HTTP response: %s", url, res)
		}
	}
	return nil
}

func resultOK(httpResult *cehttp.Result) bool {
	return httpResult.StatusCode == http.StatusOK || httpResult.StatusCode == http.StatusAccepted
}
