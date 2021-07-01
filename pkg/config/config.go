package config

import (
	"embed"
	"log"

	"gopkg.in/yaml.v2"
)

//go:embed config.yaml
var f embed.FS

type Config struct {
	App struct {
		Version string `yaml:"version"`
	} `yaml:"app"`
	Proxy struct {
		IP   string `yaml:"ip"`
		Port int    `yaml:"port"`
	} `yaml:"proxy"`
	Modeler struct {
		Link string `yaml:"link"`
	} `yaml:"modeler"`
}

func (c *Config) GetConfig() *Config {

	yamlFile, err := f.ReadFile("config.yaml")
	if err != nil {
		log.Printf("yamlFile.Get err   #%v ", err)
	}
	err = yaml.Unmarshal(yamlFile, c)
	if err != nil {
		log.Fatalf("Unmarshal: %v", err)
	}

	return c
}
