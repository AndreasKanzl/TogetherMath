package org.togethermath

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TogetherMathApplication

fun main(args: Array<String>) {
    runApplication<TogetherMathApplication>(*args)
}
