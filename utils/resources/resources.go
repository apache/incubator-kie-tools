// Copyright 2022 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package resources

import (
	"context"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
	"strings"

	"github.com/pkg/errors"
	k8serrors "k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/container-builder/client"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
)

// ResourceCustomizer can be used to inject code that changes the objects before they are created.
type ResourceCustomizer func(object ctrl.Object) ctrl.Object

// IdentityResourceCustomizer is a ResourceCustomizer that does nothing.
var IdentityResourceCustomizer = func(object ctrl.Object) ctrl.Object {
	return object
}

func ResourcesOrCollect(ctx context.Context, c client.Client, namespace string, collection *kubernetes.Collection,
	force bool, customizer ResourceCustomizer, names ...string) error {
	for _, name := range names {
		if err := ResourceOrCollect(ctx, c, namespace, collection, force, customizer, name); err != nil {
			return err
		}
	}
	return nil
}

func ResourceOrCollect(ctx context.Context, c client.Client, namespace string, collection *kubernetes.Collection,
	force bool, customizer ResourceCustomizer, name string) error {

	content, err := ResourceAsString(name)
	if err != nil {
		return err
	}

	obj, err := kubernetes.LoadResourceFromYaml(c.GetScheme(), content)
	if err != nil {
		return err
	}

	return ObjectOrCollect(ctx, c, namespace, collection, force, customizer(obj))
}

func ObjectOrCollect(ctx context.Context, c client.Client, namespace string, collection *kubernetes.Collection, force bool, obj ctrl.Object) error {
	if collection != nil {
		// Adding to the collection before setting the namespace
		collection.Add(obj)
		return nil
	}

	obj.SetNamespace(namespace)

	if obj.GetObjectKind().GroupVersionKind().Kind == "PersistentVolumeClaim" {
		if err := c.Create(ctx, obj); err != nil && !k8serrors.IsAlreadyExists(err) {
			return err
		}
	}

	if force {
		if _, err := kubernetes.ReplaceResource(ctx, c, obj); err != nil {
			return err
		}
		// For some resources, also reset the status
		if obj.GetObjectKind().GroupVersionKind().Kind == v08.KogitoServerlessPlatformKind {
			if err := c.Status().Update(ctx, obj); err != nil {
				return err
			}
		}
		return nil
	}

	// Just try to create them
	return c.Create(ctx, obj)
}

// ResourceAsString returns the named resource content as string.
func ResourceAsString(name string) (string, error) {
	data, err := Resource(name)
	return string(data), err
}

// Resource provides an easy way to access to embedded assets.
func Resource(name string) ([]byte, error) {
	name = strings.Trim(name, " ")
	if !strings.HasPrefix(name, "/") {
		name = "/" + name
	}

	file, err := openAsset(name)
	if err != nil {
		return nil, errors.Wrapf(err, "cannot access resource file %s", name)
	}

	data, err := ioutil.ReadAll(file)
	if err != nil {
		_ = file.Close()
		return nil, errors.Wrapf(err, "cannot access resource file %s", name)
	}

	return data, file.Close()
}

// DirExists tells if a directory exists and can be listed for files.
func DirExists(dirName string) bool {
	if _, err := openAsset(dirName); err != nil {
		return false
	}
	return true
}

// WithPrefix lists all file names that begins with the give path prefix
// If pathPrefix is a path of directories then be sure to end it with a '/'.
func WithPrefix(pathPrefix string) ([]string, error) {
	dirPath := filepath.Dir(pathPrefix)

	paths, err := Resources(dirPath)
	if err != nil {
		return nil, err
	}

	var res []string
	for i := range paths {
		path := filepath.ToSlash(paths[i])
		if result, _ := filepath.Match(pathPrefix+"*", path); result {
			res = append(res, path)
		}
	}

	return res, nil
}

// Resources lists all file names in the given path (starts with '/').
func Resources(dirName string) ([]string, error) {
	dir, err := openAsset(dirName)
	if err != nil {
		if os.IsNotExist(err) {
			return nil, nil
		}

		return nil, errors.Wrapf(err, "error while listing resource files %s", dirName)
	}

	info, err := dir.Stat()
	if err != nil {
		return nil, dir.Close()
	}
	if !info.IsDir() {
		CloseQuietly(dir)
		return nil, errors.Wrapf(err, "location %s is not a directory", dirName)
	}

	files, err := dir.Readdir(-1)
	if err != nil {
		CloseQuietly(dir)
		return nil, errors.Wrapf(err, "error while listing files on directory %s", dirName)
	}

	var res []string
	for _, f := range files {
		if !f.IsDir() {
			res = append(res, filepath.Join(dirName, f.Name()))
		}
	}

	return res, dir.Close()
}

func openAsset(path string) (http.File, error) {
	return Open(filepath.ToSlash(path))
}
