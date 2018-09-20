/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * This function wires up the variables used by GWT.
 * Leave this out and you get exception about null or undefined objects.
 *
 * The web worker can not use the UI, so $wnd.alert for example does not work. We still wire it up to protect us from exceptions.
 */
$stats = function() {
};
$self = self;
$wnd = self;
$doc = self;
$sessionId = null;
window = self;

/*
 * Here we have the template for the main function.
 * Between try and catch is all the generated GWT code.
 * We capture any exceptions, postMessage then posts them back to the main app.
 * The exceptions are gibberish due to minified code.
 * When debugging or adding features, best to protect your code by capturing any exceptions that might happen.
 */
function __MODULE_FUNC__() {
  var strongName;
  try {
    // __PERMUTATIONS_BEGIN__
    // Permutation logic
    // __PERMUTATIONS_END__
  } catch (e) {
    var errorMsg = {
      "worker bootstrap error" : e.message
    };
    self.postMessage(JSON.stringify(errorMsg));
    return;
  }


  importScripts(strongName + ".cache.js");
  gwtOnLoad(undefined, '__MODULE_NAME__', '', '');
}

__MODULE_FUNC__();
