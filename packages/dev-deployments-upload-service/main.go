package main

import (
    "archive/zip"
    "bytes"
    "fmt"
    "io/ioutil"
    "net/http"
	"os"
	"strconv"
	"path/filepath"
	"strings"
	"time"
)

var GLOBAL__UPLOAD_CAPTURED = false;
var MAX_UPLOADED_FILE_SIZE_IN_BYTES int64 = 200 << 20; // 200 MiB

var SHUTDOWN_CHANNEL = make(chan int);

var LOG_PREFIX = "[dev-deployments-upload-service] ";

func main() {

	unzipAtArgString := "";
	portArgString := ""

	// Validate arguments
	if (len(os.Args) != 5) {
		printUsage();
		os.Exit(1);
	} else if (os.Args[1] == "--unzip-at" && os.Args[3] == "--port") {
		unzipAtArgString = os.Args[2];
		portArgString = os.Args[4];
	} else if (os.Args[3] == "--unzip-at" && os.Args[1] == "--port") {
		unzipAtArgString = os.Args[4];
		portArgString = os.Args[2];
	} else {
		printUsage();
		os.Exit(1);
	}

	// Validate --port
	port, err := strconv.Atoi(portArgString);
	if (err != nil) {
		fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: '%s' is not a valid port number.\n", portArgString)
		os.Exit(1);
	}
	
	unzipAtPath := unzipAtArgString
	// Validate --unzip-at
	if _, err := os.Stat(unzipAtArgString); err == nil {
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Found directory '%s'.\n", unzipAtArgString)
	} else if err := os.MkdirAll(unzipAtArgString, os.ModePerm); err != nil {  // os.ModePerm == chmod 777
		fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Creating directory '%s' failed:\n", unzipAtArgString)
		fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
		os.Exit(1);
	} else {
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Created directory '%s'.\n", unzipAtArgString)
	}



    http.HandleFunc("/upload", func (w http.ResponseWriter, req *http.Request) {
		if (GLOBAL__UPLOAD_CAPTURED) {
			fmt.Fprintf(os.Stdout, LOG_PREFIX + "Upload arrived, but another arrived earlier. Server should be in the process of gracefully shutting down.\n")
			w.WriteHeader(http.StatusConflict)
		    w.Write([]byte("409: Another upload arrived first. Can't accept this one. Server is in the process of shutting down."));
			return;
		}

		GLOBAL__UPLOAD_CAPTURED = true;

		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Upload arrived...\n")
	
		req.ParseMultipartForm(MAX_UPLOADED_FILE_SIZE_IN_BYTES)

		// FormFile returns the first file for the given key `myFile`
		// it also returns the FileHeader so we can get the Filename,
		// the Header and the size of the file
		uploadedFile, handler, err := req.FormFile("myFile") //FIXME: Is there a way to not need this?
		if (err != nil) {
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Reading uploaded file failed:\n")
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Reading uploaded file failed."));

			signalShutdown(1)
			return;
		}
		defer uploadedFile.Close()

		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Uploaded File: %+v\n", handler.Filename)
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "File Size: %+v\n", handler.Size)
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "MIME Header: %+v\n", handler.Header)
	
		// Read all the contents of the uploaded file into a byte array
		uploadedZipBytes, err := ioutil.ReadAll(uploadedFile)
		if (err != nil) {
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Reading bytes of uploaded zip file failed:\n")
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Reading bytes of uploaded zip file failed."));

			signalShutdown(1)
			return;
		}

		// Extract zip contents to `extractedZippedFilePath`
		zipReader, err := zip.NewReader(bytes.NewReader(uploadedZipBytes), int64(len(uploadedZipBytes)))
		if (err != nil) {
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Creating zip reader failed.\n");
			fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte("500: Creating zip reader failed."));

			signalShutdown(1)
			return;
		}
		for _, zipFile := range zipReader.File {
			fmt.Fprintf(os.Stdout, LOG_PREFIX + "Reading zipped file '%s'...\n", zipFile.Name)
			unzippedFileBytes, err := readZipFile(zipFile)
			if err != nil {
				fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Reading zipped file '%s' failed:\n", zipFile.Name);
				fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
				w.WriteHeader(http.StatusInternalServerError)
			    w.Write([]byte("500: Reading zipped file. " + zipFile.Name));

				signalShutdown(1)
				return;
			}

			// Check for Zip Slip (directory traversal)
			extractedZippedFilePath := filepath.Join(unzipAtPath, zipFile.Name)
			if (!strings.HasPrefix(extractedZippedFilePath, filepath.Clean(unzipAtPath) + string(os.PathSeparator))) {
				fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Illegal zipped file path '%s'.\n", zipFile.Name);
				w.WriteHeader(http.StatusInternalServerError)
			    w.Write([]byte("500: Illegal zipped file path. " + extractedZippedFilePath));

				signalShutdown(1)
				return;
			}

			// Always try and write the dirs where the files are going to be written to.
			if err := os.MkdirAll(filepath.Dir(extractedZippedFilePath), os.ModePerm); err != nil {  // os.ModePerm == chmod 777
				fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Creating directory '%s' failed:\n", filepath.Dir(extractedZippedFilePath))
				fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
				w.WriteHeader(http.StatusInternalServerError)
			    w.Write([]byte("500: Creating directory failed. " + filepath.Dir(extractedZippedFilePath)));

				signalShutdown(1)
				return;
			}

			// Only write the file if it's not a dir
			if (!zipFile.FileInfo().IsDir()) {
				fmt.Fprintf(os.Stdout, LOG_PREFIX + "Writing zipped file '%s'...\n", zipFile.Name)
				f, err := os.Create(extractedZippedFilePath)
				if (err != nil) {
					fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Creating file '%s' failed:\n", extractedZippedFilePath);
					fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
					w.WriteHeader(http.StatusInternalServerError)
			   		w.Write([]byte("500: Creating file failed. " + extractedZippedFilePath));

					signalShutdown(1)
					return;
				}
				f.Write(unzippedFileBytes);
				defer f.Close();
			}
		}

		// Return that we have successfully uploaded the zip file
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(fmt.Sprintf("200: Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath)))
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath);
		
		// End the program gracefully.
		signalShutdown(0)
		return;
	});


	fmt.Fprintf(os.Stdout, LOG_PREFIX + "Starting HTTP server...\n");
	fmt.Fprintf(os.Stdout, LOG_PREFIX + "Running at port %d.\n", port);
	fmt.Fprintf(os.Stdout, LOG_PREFIX + "Uploaded zip will be unziped to '%s'.\n", unzipAtPath);
	fmt.Fprintf(os.Stdout, LOG_PREFIX + "Waiting for upload to arrive...\n");
	fmt.Fprintf(os.Stdout, LOG_PREFIX + "-------------------------------\n");

	httpServer := http.Server { Addr: ":" + portArgString, 	}

	// Start a new thread waiting for the shutdown signal.
	go func() {
		signaledExitCode := <- SHUTDOWN_CHANNEL
		fmt.Fprintf(os.Stdout, LOG_PREFIX + "Signal captured. Gracefully shutting down HTTP Server with code '%d'.\n", signaledExitCode);

		// FIXME: Alright, this is not ideal, but it does give some extra time for the first upload request to have its connection closed,
		// and give the client a nice 200. Ideally, we should:
		// 1. Stop accepting new requests.
		// 2. Wait for the existing requests to be completed, making their connections closed.
		// 3. os.Exit(signaledExitCode)
		// 4. Done.
		time.Sleep(2 * time.Second)
		os.Exit(signaledExitCode)
	}()

	// 
	if err := httpServer.ListenAndServe(); err != http.ErrServerClosed {
		fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: Starting HTTP server failed::\n");
		fmt.Fprintf(os.Stderr, LOG_PREFIX + "ERROR: %+v\n", err)
		os.Exit(1);
	}
}

func readZipFile(zf *zip.File) ([]byte, error) {
    f, err := zf.Open()
    if (err != nil) {
        return nil, err
    }

    defer f.Close()
    return ioutil.ReadAll(f);
}

func printUsage() {
	fmt.Fprintf(os.Stderr, LOG_PREFIX + "USAGE: dev-deployments-upload-service --unzip-at [dir path] --port [port number]\n")
}

func signalShutdown(exitCode int) {
	fmt.Fprintf(os.Stdout, LOG_PREFIX + "Signaling the HTTP Server to gracefully shutdown with code '%d'...\n", exitCode);
	SHUTDOWN_CHANNEL <- exitCode
}
