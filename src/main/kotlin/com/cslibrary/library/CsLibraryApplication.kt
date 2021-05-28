package com.cslibrary.library

import com.cslibrary.library.config.AdminConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AdminConfig::class)
class CsLibrayApplication

fun main(args: Array<String>) {
	runApplication<CsLibrayApplication>(*args)
}
