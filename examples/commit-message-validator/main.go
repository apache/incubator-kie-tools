package main

import (
	"io/ioutil"
	"net/http"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"

	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg"
	"github.com/kiegroup/kie-tools/examples/commit-message-validator/pkg/metadata"
)

func main() {
	router := gin.Default()

	router.Use(cors.Default())

	router.POST("/validate", func(context *gin.Context) {
		body, err := ioutil.ReadAll(context.Request.Body)
		if err != nil {
			context.String(http.StatusBadRequest, "Wrong input")
		}
		context.JSON(http.StatusOK, pkg.Validate(string(body)))
	})

	router.Run(":" + metadata.Port)
}
