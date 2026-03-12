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

package kubernetes

import (
	"context"

	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// FindJob returns a Job given the namespace and name, nil if not exists.
func FindJob(ctx context.Context, cli client.Client, namespace, name string) (*batchv1.Job, error) {
	job := &batchv1.Job{}
	err := cli.Get(ctx, client.ObjectKey{
		Namespace: namespace,
		Name:      name,
	}, job)
	if err != nil {
		if k8serrors.IsNotFound(err) {
			return nil, nil
		}
		return nil, err
	}
	return job, nil
}

func FindJobs(ctx context.Context, cli client.Client, namespace string) (*batchv1.JobList, error) {
	jobList := &batchv1.JobList{}
	if err := cli.List(ctx, jobList, client.InNamespace(namespace)); err != nil {
		return nil, err
	}
	return jobList, nil
}

// JobHasFinished returns a pair (bool1, bool2) indicating first if the Job has finished, and lastly if it has finished
// successfully.
// bool1 == true, when finished.
// boo2 == true, when finished successfully, false in other case.
func JobHasFinished(job *batchv1.Job) (bool, bool) {
	for _, c := range job.Status.Conditions {
		if (c.Type == batchv1.JobComplete || c.Type == batchv1.JobFailed) && c.Status == corev1.ConditionTrue {
			return true, c.Type == batchv1.JobComplete
		}
	}
	return false, false
}
