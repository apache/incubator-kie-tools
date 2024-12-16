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

package main

import (
	"io/ioutil"
	"net/http"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"

	"github.com/apache/incubator-kie-tools/examples/kie-sandbox-commit-message-validation-service/pkg"
	"github.com/apache/incubator-kie-tools/examples/kie-sandbox-commit-message-validation-service/pkg/metadata"
)

func main() {
	router := gin.Default()

	router.Use(cors.Default())

	router.POST("/validate", func(context *gin.Context) {
		body, err := ioutil.ReadAll(context.Request.Body)
		if err != nil {
			context.String(http.StatusBadRequest, "Wrong input")
		}
		context.JSON(http.StatusOK, pkg.Validate(string(body)))
	})

	router.Run(":" + metadata.Port)
}
