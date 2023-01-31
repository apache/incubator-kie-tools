// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package test

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/stretchr/testify/assert"
)

// ServerHandler  ...
type ServerHandler struct {
	Path         string
	JSONResponse string
}

// MockKogitoSvcReplies ...
func MockKogitoSvcReplies(t *testing.T, handlers ...ServerHandler) *httptest.Server {
	h := http.NewServeMux()
	for _, handler := range handlers {
		path := handler.Path
		response := handler.JSONResponse
		h.HandleFunc(path, func(writer http.ResponseWriter, request *http.Request) {
			_, err := writer.Write([]byte(response))
			assert.NoError(t, err)
		})
	}

	return httptest.NewServer(h)
}
