package main

import (
	"archive/zip"
	"bytes"
	"context"
	"fmt"
	"html"
	"io/ioutil"
	"net/http"
	"os"
	"os/signal"
	"path/filepath"
	"strconv"
	"strings"
	"syscall"

	"golang.org/x/sync/errgroup"
)

type Args struct {
	UNZIP_AT  string
	PORT      string
	API_KEY   string
	ROOT_PATH string
}

var ENV_VARS = Args{
	UNZIP_AT:  "DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR",
	PORT:      "DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT",
	API_KEY:   "DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY",
	ROOT_PATH: "DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH",
}

var GLOBAL__UPLOAD_CAPTURED = false
var MAX_UPLOADED_FILE_SIZE_IN_BYTES int64 = 200 << 20 // 200 MiB

var LOG_PREFIX = "[dev-deployment-upload-service] "

func main() {

	unzipAtArgString := os.Getenv(ENV_VARS.UNZIP_AT)
	portArgString := os.Getenv(ENV_VARS.PORT)
	apiKeyArgString := os.Getenv(ENV_VARS.API_KEY)
	rootPathArgString := os.Getenv(ENV_VARS.ROOT_PATH)

	// Validate arguments
	if len(os.Args) > 1 {
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: No positional arguments allowed.\n")
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"\n")
		printUsage()
		os.Exit(1)
	} else if len(unzipAtArgString) <= 0 || len(portArgString) <= 0 || len(apiKeyArgString) <= 0 {
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Missing env var arguments.\n")
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"\n")
		printUsage()
		os.Exit(1)
	}

	// Validate --port
	port, err := strconv.Atoi(portArgString)
	if err != nil {
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: '%s' is not a valid port number.\n", portArgString)
		os.Exit(1)
	}

	unzipAtPath := unzipAtArgString
	// Validate --unzip-at
	if _, err := os.Stat(unzipAtArgString); err == nil {
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Found directory '%s'.\n", unzipAtArgString)
	} else if err := os.MkdirAll(unzipAtArgString, os.ModePerm); err != nil { // os.ModePerm == chmod 777
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Creating directory '%s' failed:\n", unzipAtArgString)
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
		os.Exit(1)
	} else {
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Created directory '%s'.\n", unzipAtArgString)
	}

	// All validations passed. Start the program.

	httpServerBgContext, cancelHttpServerBgContext := context.WithCancel(context.Background())
	httpServerBgGroup, httpServerBgGroupContext := errgroup.WithContext(httpServerBgContext)
	shutdownWithErrorOnFileUpload := func(filename string) {
		httpServerBgGroup.Go(func() error { return fmt.Errorf("uploading '%s' failed", filename) })
	}

	rootPath := rootPathArgString

	uploadStatusPath := "/upload-status"

	if len(rootPathArgString) > 1 {
		uploadStatusPath = fmt.Sprintf("/%s/upload-status", rootPath)
	}

	http.HandleFunc(uploadStatusPath, func(w http.ResponseWriter, req *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("READY"))
	})

	uploadPath := "/upload"

	if len(rootPathArgString) > 1 {
		uploadPath = fmt.Sprintf("/%s/upload", rootPath)
	}

	http.HandleFunc(uploadPath, func(w http.ResponseWriter, req *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")

		if req.Method == "OPTIONS" {
			w.WriteHeader(http.StatusNoContent)
			return
		}

		apiKey := req.URL.Query().Get("apiKey")
		if apiKey != apiKeyArgString {
			fmt.Fprintf(os.Stdout, LOG_PREFIX+"⚠️  Attempted to upload with the wrong API Key: '%s'.\n", apiKey)
			w.WriteHeader(http.StatusUnauthorized)
			w.Write([]byte("401: Unauthorized."))
			return
		}

		if GLOBAL__UPLOAD_CAPTURED {
			fmt.Fprintf(os.Stdout, LOG_PREFIX+"⚠️  Upload arrived, but another arrived earlier. Server should be in the process of gracefully shutting down.\n")
			w.WriteHeader(http.StatusConflict)
			w.Write([]byte("409: Another upload arrived first. Can't accept this one. Server is in the process of shutting down."))
			return
		}

		GLOBAL__UPLOAD_CAPTURED = true

		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Upload arrived...\n")

		req.ParseMultipartForm(MAX_UPLOADED_FILE_SIZE_IN_BYTES)

		// FormFile returns the first file for the given key `myFile`
		// it also returns the FileHeader so we can get the Filename,
		// the Header and the size of the file
		uploadedFile, handler, err := req.FormFile("myFile") // TODO: Is there a way to not need this?
		if err != nil {
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Reading uploaded file failed:\n")
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Reading uploaded file failed."))

			shutdownWithErrorOnFileUpload(handler.Filename)
			return
		}
		defer uploadedFile.Close()

		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Uploaded File: %+v\n", handler.Filename)
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  File Size: %+v\n", handler.Size)
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  MIME Header: %+v\n", handler.Header)

		// Read all the contents of the uploaded file into a byte array
		uploadedZipBytes, err := ioutil.ReadAll(uploadedFile)
		if err != nil {
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Reading bytes of uploaded zip file failed:\n")
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Reading bytes of uploaded zip file failed."))

			shutdownWithErrorOnFileUpload(handler.Filename)
			return
		}

		// Extract zip contents to `extractedZippedFilePath`
		zipReader, err := zip.NewReader(bytes.NewReader(uploadedZipBytes), int64(len(uploadedZipBytes)))
		if err != nil {
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Creating zip reader failed.\n")
			fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Creating zip reader failed."))

			shutdownWithErrorOnFileUpload(handler.Filename)
			return
		}

		for _, zipFile := range zipReader.File {
			unzippedFileBytes, err := readZipFile(zipFile)
			if err != nil {
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Reading zipped file '%s' failed:\n", zipFile.Name)
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("500: Reading zipped file. " + html.EscapeString(zipFile.Name)))

				shutdownWithErrorOnFileUpload(handler.Filename)
				return
			}

			// Check for Zip Slip (directory traversal)
			extractedZippedFilePath := filepath.Join(unzipAtPath, zipFile.Name)
			if !strings.HasPrefix(extractedZippedFilePath, filepath.Clean(unzipAtPath)+string(os.PathSeparator)) {
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Illegal zipped file path '%s'.\n", zipFile.Name)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("500: Illegal zipped file path. " + html.EscapeString(extractedZippedFilePath)))

				shutdownWithErrorOnFileUpload(handler.Filename)
				return
			}

			// Always try and write the dirs where the files are going to be written to.
			if err := os.MkdirAll(filepath.Dir(extractedZippedFilePath), os.ModePerm); err != nil { // os.ModePerm == chmod 777
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Creating directory '%s' failed:\n", filepath.Dir(extractedZippedFilePath))
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("500: Creating directory failed. " + html.EscapeString(filepath.Dir(extractedZippedFilePath))))

				shutdownWithErrorOnFileUpload(handler.Filename)
				return
			}

			// Only write the file if it's not a dir
			if !zipFile.FileInfo().IsDir() {
				f, err := os.Create(extractedZippedFilePath)
				if err != nil {
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Creating file '%s' failed:\n", extractedZippedFilePath)
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
					w.WriteHeader(http.StatusInternalServerError)
					w.Write([]byte("500: Creating file failed. " + html.EscapeString(extractedZippedFilePath)))

					shutdownWithErrorOnFileUpload(handler.Filename)
					return
				}
				if _, err := f.Write(unzippedFileBytes); err != nil {
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Writing file '%s' failed:\n", extractedZippedFilePath)
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
					w.WriteHeader(http.StatusInternalServerError)
					w.Write([]byte("500: Writing file failed. " + html.EscapeString(extractedZippedFilePath)))

					shutdownWithErrorOnFileUpload(handler.Filename)
					return
				}

				fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Wrote '%s'...\n", extractedZippedFilePath)
				defer f.Close()
			}
		}

		// Return that we have successfully uploaded the zip file
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(fmt.Sprintf("200: Successfully extracted '%s' to '%s'.\n", html.EscapeString(handler.Filename), html.EscapeString(unzipAtPath))))
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Successfully extracted '%s' to '%s'.\n", html.EscapeString(handler.Filename), html.EscapeString(unzipAtPath))

		// End the program gracefully.
		cancelHttpServerBgContext()
	})

	httpServer := http.Server{Addr: ":" + portArgString}

	// Goroutine for listening for Ctrl + C signal for graceful shutdown.
	go func() {
		c := make(chan os.Signal, 1) // We need to reserve to buffer size 1, so the notifier are not blocked
		signal.Notify(c, os.Interrupt, syscall.SIGTERM)
		<-c // Wait for signal to come
		fmt.Fprintf(os.Stdout, "\n")
		cancelHttpServerBgContext()
	}()

	// Goroutine for running the HTTP server.
	httpServerBgGroup.Go(func() error {
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Starting HTTP server...\n")
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Running at port %d.\n", port)
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  The uploaded zip will be extracted to %s.\n", unzipAtPath)
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  The root path is set to %s.\n", rootPathArgString)
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Waiting for upload to arrive...\n")
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"--------------------------------------------------------\n")
		return httpServer.ListenAndServe()
	})

	// Goroutine for shutting down the HTTP server...
	httpServerBgGroup.Go(func() error {
		<-httpServerBgGroupContext.Done()
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Shutting down HTTP server...\n")
		return httpServer.Shutdown(context.Background()) // Will return `http.ErrServerClosed` is everything goes well.
	})

	// Waiting until bgGroup finishes, printing errors or not.
	if err := httpServerBgGroup.Wait(); err == nil || err == http.ErrServerClosed {
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Shutdown completed successfully.\n")
		os.Exit(0)
	} else {
		fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ Shutdown complete, but exiting with code 1, as the following error occurred:\n")
		fmt.Fprintf(os.Stderr, "%+v\n", err)
		os.Exit(1)
	}
}

func readZipFile(zf *zip.File) ([]byte, error) {
	f, err := zf.Open()
	if err != nil {
		return nil, err
	}

	defer f.Close()
	return ioutil.ReadAll(f)
}

func printUsage() {
	fmt.Fprintf(os.Stderr, LOG_PREFIX+"USAGE: `dev-deployment-upload-service`. Arguments are passed using env vars:\n")
	fmt.Fprintf(os.Stderr, LOG_PREFIX+fmt.Sprintf("- %s:\t Required. Where the uploaded zip will be extracted to. If it doesn't exist, it will be created.\n", ENV_VARS.UNZIP_AT))
	fmt.Fprintf(os.Stderr, LOG_PREFIX+fmt.Sprintf("- %s:\t\t\t Required. Port where the HTTP Server will run at. The /upload endpoint will be made available.\n", ENV_VARS.PORT))
	fmt.Fprintf(os.Stderr, LOG_PREFIX+fmt.Sprintf("- %s:\t\t Required. Allowed API Key used as a queryParam at the /upload endpoint.\n", ENV_VARS.API_KEY))
	fmt.Fprintf(os.Stderr, LOG_PREFIX+"\n")
	fmt.Fprintf(os.Stderr, LOG_PREFIX+"Example:\n")
	fmt.Fprintf(os.Stderr, LOG_PREFIX+"curl -X POST http://localhost:[port]/upload?apiKey=[apiKey]\n") // TODO: This is not right..
}
