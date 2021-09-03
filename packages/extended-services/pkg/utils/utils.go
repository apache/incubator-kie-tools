package utils

import (
	"log"
	"os"
	"path/filepath"
)

func GetBaseDir() string {
	env := os.Getenv("ENV")
	if env != "dev" {
		return filepath.Dir(os.Args[0])
	} else {
		return "./"
	}
}

func Check(err error) {
	if err != nil {
		log.Fatal(err)
		panic(err)
	}
}
