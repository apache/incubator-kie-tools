/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.service;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class PlaceManagerActivityServiceImpl implements PlaceManagerActivityService {

    private static final Logger logger = LoggerFactory.getLogger( PlaceManagerActivityServiceImpl.class );

    private List<String> availableActivities;

    @PostConstruct
    public void init() {
    }

    @Override
    public void initActivities( List<String> activities ) {
        this.availableActivities = activities;
    }

    @Override
    public List<String> getAllActivities() {
        return this.availableActivities;
    }
}
