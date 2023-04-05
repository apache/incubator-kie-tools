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
)

func printUsage() {
	fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] USAGE: dev-deployments-upload-service --unzip-at [dir path] --port [port number]\n")
}

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
		fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: '%s' is not a valid port number.\n", portArgString)
		os.Exit(1);
	}
	
	unzipAtPath := unzipAtArgString
	// Validate --unzip-at
	if _, err := os.Stat(unzipAtArgString); err == nil {
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Found directory '%s'.\n", unzipAtArgString)
	} else if err := os.MkdirAll(unzipAtArgString, os.ModePerm); err != nil {  // os.ModePerm == chmod 777
		fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Creating directory '%s' failed:\n", unzipAtArgString)
		fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
		os.Exit(1);
	} else {
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Created directory '%s'.\n", unzipAtArgString)
	}

    http.HandleFunc("/upload", func (w http.ResponseWriter, req *http.Request) {
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Upload arrived...\n")
	
		// Parse the multipart form, 200 << 20 specifies a maximum upload of 200 MiB files.
		req.ParseMultipartForm(200 << 20) //FIXME: Extract this to a constant

		// FormFile returns the first file for the given key `myFile`
		// it also returns the FileHeader so we can get the Filename,
		// the Header and the size of the file
		uploadedFile, handler, err := req.FormFile("myFile") //FIXME: Is there a way to not need this?
		if (err != nil) {
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Reading uploaded file failed:\n")
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
			os.Exit(1)
		}
		defer uploadedFile.Close()

		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Uploaded File: %+v\n", handler.Filename)
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] File Size: %+v\n", handler.Size)
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] MIME Header: %+v\n", handler.Header)
	
		// Read all the contents of the uploaded file into a byte array
		uploadedZipBytes, err := ioutil.ReadAll(uploadedFile)
		if (err != nil) {
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Reading bytes of uploaded file failed:\n")
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
			os.Exit(1);
		}

		// Extract zip contents to `extractedZippedFilePath`
		zipReader, err := zip.NewReader(bytes.NewReader(uploadedZipBytes), int64(len(uploadedZipBytes)))
		if (err != nil) {
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Creating zip reader failed.\n");
			fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
			os.Exit(1);
		}
		for _, zipFile := range zipReader.File {
			fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Reading zipped file '%s'...\n", zipFile.Name)
			unzippedFileBytes, err := readZipFile(zipFile)
			if err != nil {
				fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Reading zipped file '%s' failed:\n", zipFile.Name);
				fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
				os.Exit(1);
			}

			// Check for Zip Slip (directory traversal)
			extractedZippedFilePath := filepath.Join(unzipAtPath, zipFile.Name)
			if (!strings.HasPrefix(extractedZippedFilePath, filepath.Clean(unzipAtPath) + string(os.PathSeparator))) {
				fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Illegal zipped file path '%s'.\n", zipFile.Name);
				os.Exit(1);
			}

			// Always try and write the dirs where the files are going to be written to.
			if err := os.MkdirAll(filepath.Dir(extractedZippedFilePath), os.ModePerm); err != nil {  // os.ModePerm == chmod 777
				fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Creating directory '%s' failed:\n", filepath.Dir(extractedZippedFilePath))
				fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
				os.Exit(1);
			}

			// Only write the file if it's not a dir
			if (!zipFile.FileInfo().IsDir()) {
				fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Writing zipped file '%s'...\n", zipFile.Name)
				f, err := os.Create(extractedZippedFilePath)
				if (err != nil) {
					fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: Creating file '%s' failed:\n", extractedZippedFilePath);
					fmt.Fprintf(os.Stderr, "[dev-deployments-upload-service] ERROR: %+v\n", err)
					os.Exit(1);
				}
				f.Write(unzippedFileBytes);
				defer f.Close();
			}
		}

		// Return that we have successfully uploaded the zip file
		fmt.Fprintf(w, "[dev-deployments-upload-service] Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath);
		if f, ok := w.(http.Flusher); ok {
			f.Flush()
		}

		// End the program gracefully.
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Successfully extracted '%s' to '%s'.\n", handler.Filename, unzipAtPath);
		fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Done.");

		// FIXME: Gracefully shutdown the HTTP Server before exiting.
		//        We need to make sure that the HTTP request is correctly closed so we get a nice 200 on the frontend.
		os.Exit(0);
	})


	fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Starting HTTP server...\n");
	fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Running at port %d.\n", port);
	fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Uploaded zip will be unziped to '%s'.\n", unzipAtPath);
	fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] Waiting for upload to arrive...\n");
	fmt.Fprintf(os.Stdout, "[dev-deployments-upload-service] -------------------------------\n");
    http.ListenAndServe(":" + portArgString, nil)
}

func readZipFile(zf *zip.File) ([]byte, error) {
    f, err := zf.Open()
    if (err != nil) {
        return nil, err
    }

    defer f.Close()
    return ioutil.ReadAll(f);
}
