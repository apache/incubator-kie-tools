package common

import (
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
)

func FindFilesWithExtensions(directoryPath string, extensions []string) ([]string, error) {
	filePaths := []string{}

	_, err := os.Stat(directoryPath)
	if os.IsNotExist(err) {
		return filePaths, nil
	} else if err != nil {
		return nil, fmt.Errorf("failed to access directory: %s", err)
	}

	files, err := os.ReadDir(directoryPath)
	if err != nil {
		return nil, fmt.Errorf("failed to read directory: %s", err)
	}

	for _, file := range files {
		if file.IsDir() {
			continue
		}

		fileExt := filepath.Ext(file.Name())
		for _, ext := range extensions {
			if strings.EqualFold(fileExt, ext) {
				filePath := filepath.Join(directoryPath, file.Name())
				filePaths = append(filePaths, filePath)
				break
			}
		}
	}

	return filePaths, nil
}

func MustGetFile(filepath string) (io.Reader, error) {
	file, err := os.OpenFile(filepath, os.O_RDONLY, os.ModePerm)
	if err != nil {
		return nil, fmt.Errorf("failed to read file: %s", err)
	}
	return file, nil
}
