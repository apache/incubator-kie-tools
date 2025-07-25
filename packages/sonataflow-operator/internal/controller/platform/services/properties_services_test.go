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

package services

import (
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"

	"github.com/magiconair/properties"
)

var (
	enabled  = true
	disabled = false
)

var _ = Describe("PlatformServiceHandler properties", func() {

	var _ = Context("for service properties", func() {

		var _ = Context("defining the application properties generated for the deployment of the", func() {

			DescribeTable("Job Service",
				func(plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
					js := NewJobServiceHandler(plfm)
					handler, err := NewServiceAppPropertyHandler(js)
					Expect(err).NotTo(HaveOccurred())
					p, err := properties.LoadString(handler.Build())
					Expect(err).NotTo(HaveOccurred())
					p.Sort()
					Expect(p).To(Equal(expectedProperties))
				},
				Entry("with an empty spec", generatePlatform(emptyJobServiceSpec(), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field undefined and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field undefined and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with enabled field set to false and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(nil), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field set to false and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(&disabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with enabled field set to true and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentDevProperties()),
				Entry("with enabled field set to true and with postgreSQL persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithPostgreSQLProperties()),
				Entry("with both services with enabled field set to true and with ephemeral persistence",
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default")),
					generateJobServiceDeploymentWithDataIndexAndEphemeralProperties()),
				Entry("with both services with enabled field set to true and postgreSQL persistence for both",
					generatePlatform(setJobServiceEnabledValue(&enabled), setDataIndexEnabledValue(&enabled), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema"), setDataIndexJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateJobServiceDeploymentWithDataIndexAndPostgreSQLProperties()),
			)

			DescribeTable("Data Index", func(plfm *operatorapi.SonataFlowPlatform, expectedProperties *properties.Properties) {
				di := NewDataIndexHandler(plfm)
				handler, err := NewServiceAppPropertyHandler(di)
				Expect(err).NotTo(HaveOccurred())
				p, err := properties.LoadString(handler.Build())
				Expect(err).NotTo(HaveOccurred())
				p.Sort()
				Expect(p).To(Equal(expectedProperties))
			},
				Entry("with ephemeral persistence", generatePlatform(emptyDataIndexServiceSpec(), setPlatformName("foo"), setPlatformNamespace("default")), generateDataIndexDeploymentProperties()),
				Entry("with postgreSQL persistence", generatePlatform(emptyDataIndexServiceSpec(), setPlatformName("foo"), setPlatformNamespace("default"), setJobServiceJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")),
					generateDataIndexDeploymentProperties()),
			)
		})

	})

})

func generateJobServiceDeploymentDevProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.service.url", "http://foo-jobs-service.default")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.management.JobServiceLeaderLivenessHealthCheck".enabled`, "true")
	p.Set("kogito.jobs-service.management.leader-check.expiration-in-seconds", "60")
	p.Sort()
	return p
}

func generateDataIndexDeploymentProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.service.url", "http://foo-data-index-service.default")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set("quarkus.smallrye-health.check.\"io.quarkus.kafka.client.health.KafkaHealthCheck\".enabled", "false")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithPostgreSQLProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.service.url", "http://foo-jobs-service.default")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.management.JobServiceLeaderLivenessHealthCheck".enabled`, "true")
	p.Set("kogito.jobs-service.management.leader-check.expiration-in-seconds", "60")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithDataIndexAndEphemeralProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.service.url", "http://foo-jobs-service.default")
	p.Set("kogito.jobs-service.http.job-status-change-events", "true")
	p.Set("mp.messaging.outgoing.kogito-job-service-job-status-events-http.url", "http://foo-data-index-service.default/jobs")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.management.JobServiceLeaderLivenessHealthCheck".enabled`, "true")
	p.Set("kogito.jobs-service.management.leader-check.expiration-in-seconds", "60")
	p.Sort()
	return p
}

func generateJobServiceDeploymentWithDataIndexAndPostgreSQLProperties() *properties.Properties {
	p := properties.NewProperties()
	p.Set("kogito.service.url", "http://foo-jobs-service.default")
	p.Set("kogito.jobs-service.http.job-status-change-events", "true")
	p.Set("mp.messaging.outgoing.kogito-job-service-job-status-events-http.url", "http://foo-data-index-service.default/jobs")
	p.Set("quarkus.devservices.enabled", "false")
	p.Set("quarkus.http.host", "0.0.0.0")
	p.Set("quarkus.http.port", "8080")
	p.Set("quarkus.kogito.devservices.enabled", "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`, "false")
	p.Set(`quarkus.smallrye-health.check."org.kie.kogito.jobs.service.management.JobServiceLeaderLivenessHealthCheck".enabled`, "true")
	p.Set("kogito.jobs-service.management.leader-check.expiration-in-seconds", "60")
	p.Sort()
	return p
}

type plfmOptionFn func(p *operatorapi.SonataFlowPlatform)

func generatePlatform(opts ...plfmOptionFn) *operatorapi.SonataFlowPlatform {
	plfm := &operatorapi.SonataFlowPlatform{}
	for _, f := range opts {
		f(plfm)
	}
	return plfm
}

func setJobServiceEnabledValue(v *bool) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.JobServiceServiceSpec{}
		}
		p.Spec.Services.JobService.Enabled = v
	}
}

func setDataIndexEnabledValue(v *bool) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.DataIndexServiceSpec{}
		}
		p.Spec.Services.DataIndex.Enabled = v
	}
}

func emptyDataIndexServiceSpec() plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.DataIndexServiceSpec{}
		}
	}
}

func emptyJobServiceSpec() plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.JobServiceServiceSpec{}
		}
	}
}

func setPlatformNamespace(namespace string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		p.Namespace = namespace
	}
}

func setPlatformName(name string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		p.Name = name
	}
}

func setJobServiceJDBC(jdbc string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.JobService == nil {
			p.Spec.Services.JobService = &operatorapi.JobServiceServiceSpec{}
		}
		if p.Spec.Services.JobService.Persistence == nil {
			p.Spec.Services.JobService.Persistence = &operatorapi.PersistenceOptionsSpec{}
		}
		if p.Spec.Services.JobService.Persistence.PostgreSQL == nil {
			p.Spec.Services.JobService.Persistence.PostgreSQL = &operatorapi.PersistencePostgreSQL{}
		}
		p.Spec.Services.JobService.Persistence.PostgreSQL.JdbcUrl = jdbc
	}
}

func setDataIndexJDBC(jdbc string) plfmOptionFn {
	return func(p *operatorapi.SonataFlowPlatform) {
		if p.Spec.Services == nil {
			p.Spec.Services = &operatorapi.ServicesPlatformSpec{}
		}
		if p.Spec.Services.DataIndex == nil {
			p.Spec.Services.DataIndex = &operatorapi.DataIndexServiceSpec{}
		}
		if p.Spec.Services.DataIndex.Persistence == nil {
			p.Spec.Services.DataIndex.Persistence = &operatorapi.PersistenceOptionsSpec{}
		}
		if p.Spec.Services.DataIndex.Persistence.PostgreSQL == nil {
			p.Spec.Services.DataIndex.Persistence.PostgreSQL = &operatorapi.PersistencePostgreSQL{}
		}
		p.Spec.Services.DataIndex.Persistence.PostgreSQL.JdbcUrl = jdbc
	}
}
