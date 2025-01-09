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
	"strconv"
	"strings"
)

func Length(message string, options string) *Validation {
	var minMaxValues = strings.Split(options, "-")

	var minLength, err1 = strconv.Atoi(minMaxValues[0])
	var maxLength, err2 = strconv.Atoi(minMaxValues[1])
	if err1 != nil || err2 != nil || maxLength < minLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Misconfigured length validation parameter (%s). Min and Max length required in the format: \"minLength-maxLength\".", options),
		}
	}
	if len(message) > maxLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message is longer than %d characters.", maxLength),
		}
	}

	if len(message) < minLength {
		return &Validation{
			Result: false,
			Reason: fmt.Sprintf("Commit message is shorter than %d characters.", minLength),
		}
	}

	return &Validation{
		Result: true,
	}
}
