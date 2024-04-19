/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package framework

import (
	"context"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/machinebox/graphql"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
)

const graphQLNoAuthToken = ""

// WaitForSuccessfulGraphQLRequest waits for an GraphQL request to be successful
func WaitForSuccessfulGraphQLRequest(namespace, uri, path, query string, timeoutInMin int, response interface{}, analyzeResponse func(response interface{}) (bool, error)) error {
	return WaitForSuccessfulGraphQLRequestUsingAccessToken(namespace, uri, path, query, graphQLNoAuthToken, timeoutInMin, response, analyzeResponse)
}

// WaitForSuccessfulGraphQLRequestUsingPagination waits for a GraphQL request with pagination to be successful.
// You can provide 2 functions:
//
// 1. After each response. This is called after every page is queried. Useful for checking the content of each page
// or appending results for final checks after all pages are retrieved.
//
// 2. After full request. This is called once after all pages are queried. Useful for final checks on all results.
//
// If more than one page is to be retrieved, include the `$offset` variable in the query which will be used for pagination.
func WaitForSuccessfulGraphQLRequestUsingPagination(namespace, uri, path, query string, timeoutInMin, pageSize, totalSize int, response interface{}, afterEachResponse func(response interface{}) (bool, error), afterFullRequest func() (bool, error)) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("GraphQL query %s on path '%s' using '%s' access token to be successful", query, path, graphQLNoAuthToken), timeoutInMin,
		func() (bool, error) {
			totalPages := totalSize / pageSize
			if totalSize%pageSize != 0 {
				totalPages++
			}
			for queried := 0; queried < totalSize; {
				query := strings.ReplaceAll(query, "$offset", strconv.Itoa(queried))
				GetLogger(namespace).Info("Querying", "page no.", queried/pageSize+1, "of", totalPages, "query", query)
				err := ExecuteGraphQLRequestWithLoggingOption(namespace, uri, path, query, graphQLNoAuthToken, response, false)
				if err != nil {
					return false, err
				}
				if afterEachResponse != nil {
					if success, err := afterEachResponse(response); !success || err != nil {
						return success, err
					}
				}
				queried = queried + pageSize
			}

			if afterFullRequest != nil {
				return afterFullRequest()
			}

			return true, nil
		})
}

// WaitForSuccessfulGraphQLRequestUsingAccessToken waits for an GraphQL request using access token to be successful
func WaitForSuccessfulGraphQLRequestUsingAccessToken(namespace, uri, path, query, accessToken string, timeoutInMin int, response interface{}, analyzeResponse func(response interface{}) (bool, error)) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("GraphQL query %s on path '%s' using '%s' access token to be successful", query, path, accessToken), timeoutInMin,
		func() (bool, error) {
			success, err := IsGraphQLRequestSuccessful(namespace, uri, path, query, accessToken, response)
			if err != nil {
				return false, err
			}

			if analyzeResponse != nil {
				return analyzeResponse(response)
			}

			return success, nil
		})
}

// WaitForFailingGraphQLRequest waits for an GraphQL request to be fail
func WaitForFailingGraphQLRequest(namespace, uri, path, query string, timeoutInMin int) error {
	return WaitForFailingGraphQLRequestUsingAccessToken(namespace, uri, path, query, graphQLNoAuthToken, timeoutInMin)
}

// WaitForFailingGraphQLRequestUsingAccessToken waits for an GraphQL request using access token to be fail
func WaitForFailingGraphQLRequestUsingAccessToken(namespace, uri, path, query, accessToken string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("GraphQL query %s on path '%s' using '%s' access token to be failing", query, path, accessToken), timeoutInMin,
		func() (bool, error) {
			success, err := IsGraphQLRequestSuccessful(namespace, uri, path, query, accessToken, nil)
			return !success || err != nil, nil
		})
}

// ExecuteGraphQLRequestWithLogging executes a GraphQL query
func ExecuteGraphQLRequestWithLogging(namespace, uri, path, query, bearerToken string, response interface{}) error {
	return ExecuteGraphQLRequestWithLoggingOption(namespace, uri, path, query, bearerToken, response, true)
}

// ExecuteGraphQLRequestWithLoggingOption executes a GraphQL query with possibility of logging each request
func ExecuteGraphQLRequestWithLoggingOption(namespace, uri, path, query, bearerToken string, response interface{}, logResponse bool) error {
	// create a client (safe to share across requests)
	client := graphql.NewClient(fmt.Sprintf("%s/%s", uri, path), graphql.WithHTTPClient(&http.Client{Timeout: (time.Duration(10*config.GetLoadFactor()) * time.Second)}))
	req := graphql.NewRequest(query)
	req.Header.Set("Cache-Control", "no-cache")
	addGraphqlAuthentication(req, bearerToken)
	ctx := context.Background()
	if err := client.Run(ctx, req, response); err != nil {
		return err
	}
	GetLogger(namespace).Info("GraphQL response received successfully")
	if logResponse {
		GetLogger(namespace).Info("GraphQL", "response", response)

	}
	return nil
}

// IsGraphQLRequestSuccessful makes and checks whether a GraphQL query is successful
func IsGraphQLRequestSuccessful(namespace, uri, path, query, bearerToken string, response interface{}) (bool, error) {
	err := ExecuteGraphQLRequestWithLogging(namespace, uri, path, query, bearerToken, response)
	if err != nil {
		return false, err
	}
	return true, nil
}

func addGraphqlAuthentication(request *graphql.Request, bearerToken string) {
	if len(bearerToken) > 0 {
		// Bearer authentication
		request.Header.Add("Authorization", "Bearer "+bearerToken)
	}
}
