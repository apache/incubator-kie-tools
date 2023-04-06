package main

import (
	"archive/zip"
	"bytes"
	"context"
	"errors"
	"fmt"
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

var GLOBAL__UPLOAD_CAPTURED = false
var MAX_UPLOADED_FILE_SIZE_IN_BYTES int64 = 200 << 20 // 200 MiB

var LOG_PREFIX = "[dev-deployments-upload-service] "

func main() {

	unzipAtArgString := ""
	portArgString := ""

	// Validate arguments
	if len(os.Args) != 5 {
		printUsage()
		os.Exit(1)
	} else if os.Args[1] == "--unzip-at" && os.Args[3] == "--port" {
		unzipAtArgString = os.Args[2]
		portArgString = os.Args[4]
	} else if os.Args[3] == "--unzip-at" && os.Args[1] == "--port" {
		unzipAtArgString = os.Args[4]
		portArgString = os.Args[2]
	} else {
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
		httpServerBgGroup.Go(func() error { return errors.New(fmt.Sprintf("Uploading '%s' failed.", filename)) })
	}

	http.HandleFunc("/upload", func(w http.ResponseWriter, req *http.Request) {
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
		uploadedFile, handler, err := req.FormFile("myFile") // FIXME: Is there a way to not need this?
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
				w.Write([]byte("500: Reading zipped file. " + zipFile.Name))

				shutdownWithErrorOnFileUpload(handler.Filename)
				return
			}

			// Check for Zip Slip (directory traversal)
			extractedZippedFilePath := filepath.Join(unzipAtPath, zipFile.Name)
			if !strings.HasPrefix(extractedZippedFilePath, filepath.Clean(unzipAtPath)+string(os.PathSeparator)) {
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Illegal zipped file path '%s'.\n", zipFile.Name)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("500: Illegal zipped file path. " + extractedZippedFilePath))

				shutdownWithErrorOnFileUpload(handler.Filename)
				return
			}

			// Always try and write the dirs where the files are going to be written to.
			if err := os.MkdirAll(filepath.Dir(extractedZippedFilePath), os.ModePerm); err != nil { // os.ModePerm == chmod 777
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Creating directory '%s' failed:\n", filepath.Dir(extractedZippedFilePath))
				fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("500: Creating directory failed. " + filepath.Dir(extractedZippedFilePath)))

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
					w.Write([]byte("500: Creating file failed. " + extractedZippedFilePath))

					shutdownWithErrorOnFileUpload(handler.Filename)
					return
				}
				if _, err := f.Write(unzippedFileBytes); err != nil {
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: Writing file '%s' failed:\n", extractedZippedFilePath)
					fmt.Fprintf(os.Stderr, LOG_PREFIX+"❌ ERROR: %+v\n", err)
					w.WriteHeader(http.StatusInternalServerError)
					w.Write([]byte("500: Writing file failed. " + extractedZippedFilePath))

					shutdownWithErrorOnFileUpload(handler.Filename)
					return
				}

				fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Wrote '%s'...\n", zipFile.Name)
				defer f.Close()
			}
		}

		// Return that we have successfully uploaded the zip file
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(fmt.Sprintf("200: Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath)))
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"✅ Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath)

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
		fmt.Fprintf(os.Stdout, LOG_PREFIX+"ℹ️  Uploaded zip will be unziped to '%s'.\n", unzipAtPath)
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
	fmt.Fprintf(os.Stderr, LOG_PREFIX+"USAGE: dev-deployments-upload-service --unzip-at [dir path] --port [port number]\n")
}
