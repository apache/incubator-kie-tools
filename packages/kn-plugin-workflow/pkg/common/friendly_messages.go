/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
	"unicode"
)

// Accepts a operation string, and returns an array of predefined messages.
// Taking the "creating" operation:
// []string{
// 	" Creating...",
// 	" Still creating project",
// 	" Still creating project",
// 	" Yes, still creating project",
// 	" Don't give up on me",
// 	" Still creating project",
// 	" This is taking a while",
// }
func GetFriendlyMessages(operation string) []string {
	return []string{
		getStartMessage(operation),
		getStillMessage(operation),
		getStillMessage(operation),
		getStillConfirmationMessage(operation),
		getDontGiveUpMessage(),
		getStillMessage(operation),
		getTakingAWhileMessage(),
	}
}

func getStartMessage(operation string) string {
	operationRunes := []rune(operation)
	return fmt.Sprintf(" %s...", string(append([]rune{unicode.ToUpper(operationRunes[0])}, operationRunes[1:]...)))
}

func getStillMessage(operation string) string {
	return fmt.Sprintf(" Still %s", operation)
}

func getStillConfirmationMessage(operation string) string {
	return fmt.Sprintf(" Yes, still %s", operation)
}

func getDontGiveUpMessage() string {
	return " Don't give up on me"
}

func getTakingAWhileMessage() string {
	return " This is taking a while"
}
