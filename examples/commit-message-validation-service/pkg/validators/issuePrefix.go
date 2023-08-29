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

package validators

import (
	"fmt"
	"regexp"
	"strings"
)

func IssuePrefix(message string, prefix string) *Validation {
	if prefix == "" {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Misconfigured IssuePrefix validation parameter (%s). Prefix required, replacing numbers with \"*\", examples: \"JIRA-*\", \"#*\", \"kie-issues#*\"", prefix),
		}
	}

	var regex = "^" + strings.ReplaceAll(prefix, "*", "[0-9]+")

	match, _ := regexp.MatchString(regex, message)

	if !match {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message missing or wrong desired prefix: \"%s\".", prefix),
		}
	}

	return &Validation{
		Result: true,
	}
}
