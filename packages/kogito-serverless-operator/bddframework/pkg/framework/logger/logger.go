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

package logger

import (
	"io"
	"os"
	"time"

	"github.com/go-logr/logr"
	"github.com/go-logr/zapr"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	logf "sigs.k8s.io/controller-runtime/pkg/log"
	logzap "sigs.k8s.io/controller-runtime/pkg/log/zap"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/env"
)

var (
	defaultOutput = os.Stdout
)

// Opts describe logger options
type Opts struct {
	// Verbose will increase logging
	Verbose bool
	// Output specifies where to log
	Output io.Writer
}

// Logger shared logger struct
type Logger struct {
	logr.Logger
}

// Debug alternative for info format with DEBUG named and correct log level
func (l Logger) Debug(message string, keysAndValues ...interface{}) {
	l.Logger.WithName("DEBUG").V(1).Info(message, keysAndValues...)
}

// Warn alternative for info format with sprintf and WARN named.
func (l Logger) Warn(message string, keysAndValues ...interface{}) {
	l.Logger.WithName("WARNING").V(0).Info(message, keysAndValues...)
}

// GetLoggerWithOptions returns a custom named logger with given options
func GetLoggerWithOptions(name string, options *Opts) Logger {
	if options == nil {
		options = getDefaultOpts()
	} else if options.Output == nil {
		options.Output = defaultOutput
	}
	return getLogger(name, options)
}

func getDefaultOpts() *Opts {
	return &Opts{
		Verbose: env.GetBoolOSEnv("DEBUG"),
		Output:  defaultOutput,
	}
}

func getLogger(name string, options *Opts) Logger {
	// Set log level... override default w/ command-line variable if set.
	// The logger instantiated here can be changed to any logger
	// implementing the logr.Logger interface. This logger will
	// be propagated through the whole operator, generating
	// uniform and structured logs.
	logger := Logger{
		createLogger(options).WithName(name),
	}
	return logger
}

func createLogger(options *Opts) (logger Logger) {
	log := Logger{
		Logger: createZAPLogger(options),
	}

	logf.SetLogger(log.Logger)
	return log
}

// createZAPLogger is a Logger implementation.
// If development is true, a Zap development config will be used,
// otherwise a Zap production config will be used
// (stacktraces on errors, sampling).
func createZAPLogger(options *Opts) logr.Logger {
	// this basically mimics New<type>Config, but with a custom sink
	sink := zapcore.AddSync(options.Output)

	var enc zapcore.Encoder
	var lvl zap.AtomicLevel
	var opts []zap.Option

	if options.Verbose {
		encCfg := zap.NewDevelopmentEncoderConfig()
		enc = zapcore.NewConsoleEncoder(encCfg)
		lvl = zap.NewAtomicLevelAt(zap.DebugLevel)
		opts = append(opts, zap.Development(), zap.AddStacktrace(zap.ErrorLevel))
	} else {
		encCfg := zap.NewProductionEncoderConfig()
		encCfg.TimeKey = "T"
		encCfg.EncodeTime = zapcore.ISO8601TimeEncoder
		enc = zapcore.NewJSONEncoder(encCfg)
		lvl = zap.NewAtomicLevelAt(zap.InfoLevel)
		opts = append(opts, zap.WrapCore(func(core zapcore.Core) zapcore.Core {
			return zapcore.NewSamplerWithOptions(core, time.Second, 100, 100)
		}))
	}
	opts = append(opts, zap.AddCallerSkip(1), zap.ErrorOutput(sink))
	log := zap.New(zapcore.NewCore(&logzap.KubeAwareEncoder{Encoder: enc, Verbose: options.Verbose}, sink, lvl))
	log = log.WithOptions(opts...)
	return zapr.NewLogger(log)
}
