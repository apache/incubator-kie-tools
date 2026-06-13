/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.springboot.devconsole.forms;

import java.io.FileNotFoundException;

import org.jbpm.devconsole.commons.forms.FormsStorage;
import org.jbpm.devconsole.commons.forms.model.FormContent;
import org.jbpm.devconsole.commons.forms.model.FormFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API used by the Dev UI Forms pages. It exposes the same contract as the jBPM Quarkus Dev UI
 * extension so the Dev UI webapp works unchanged.
 */
@RestController
@RequestMapping("/forms")
public class FormsRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsRestController.class);

    private final FormsStorage storage;

    public FormsRestController(FormsStorage storage) {
        this.storage = storage;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFormsList(@RequestParam(name = "names", required = false) String names) {
        try {
            storage.refresh();
            return ResponseEntity.ok(storage.getFormInfoList(FormFilter.fromNamesParam(names)));
        } catch (Exception e) {
            LOGGER.warn("Error while getting forms list: ", e);
            return ResponseEntity.internalServerError().body("Unexpected error while getting forms list: " + e.getMessage());
        }
    }

    @GetMapping(value = "/count", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> formsCount() {
        try {
            return ResponseEntity.ok(String.valueOf(storage.getFormsCount()));
        } catch (Exception e) {
            LOGGER.error("Error while getting forms count: ", e);
            return ResponseEntity.internalServerError().body("Unexpected error while getting forms count: " + e.getMessage());
        }
    }

    @GetMapping(value = { "/{formName}", "/{formName}/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getFormContent(@PathVariable("formName") String formName) {
        try {
            return ResponseEntity.ok(storage.getFormContent(formName));
        } catch (FileNotFoundException e) {
            LOGGER.warn("Couldn't find form '{}'", formName);
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Error while getting content of form '{}': ", formName, e);
            return ResponseEntity.internalServerError().body("Unexpected error while getting form content: " + e.getMessage());
        }
    }

    @PostMapping(value = { "/{formName}", "/{formName}/" }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateFormContent(@PathVariable("formName") String formName, @RequestBody FormContent formContent) {
        try {
            storage.updateFormContent(formName, formContent);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.warn("Error while updating content of form '{}': ", formName, e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
