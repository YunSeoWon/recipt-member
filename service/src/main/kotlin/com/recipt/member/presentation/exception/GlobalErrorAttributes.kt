package com.recipt.member.presentation.exception

import com.recipt.core.exception.ReciptException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.context.MessageSource
import org.springframework.core.annotation.MergedAnnotations
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.attributeOrNull
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class GlobalErrorAttributes(
    private val messageSource: MessageSource
) : DefaultErrorAttributes() {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val ERROR_ATTRIBUTE = "RECIPT_ERROR"
        private val LOCALE = Locale.KOREA
    }

    override fun getErrorAttributes (
        request: ServerRequest,
        options: ErrorAttributeOptions
    ): Map<String, Any> {
        val error = getError(request)
        val errorStatus = determineHttpStatus(error)

        logger.error("ERROR: " , error)

        val message = (error as? ReciptException)?.let {  messageSource.getMessage(it.errorCode.code, null, LOCALE) }
            ?: error.message?: "no message"

        return mapOf(
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            "path" to request.path(),
            "status" to errorStatus.value(),
            "error" to errorStatus.reasonPhrase,
            "message" to message,
            "requestId" to request.exchange().request.id
        )
    }

    override fun getError(request: ServerRequest): Throwable {
        return request.attributeOrNull(ERROR_ATTRIBUTE) as? Throwable
            ?: throw IllegalStateException("Missing exception attribute in ServerWebExchange")
    }

    override fun storeErrorInformation(
        error: Throwable?,
        exchange: ServerWebExchange
    ) {
        exchange.attributes.putIfAbsent(ERROR_ATTRIBUTE, error)
    }

    private fun determineHttpStatus(error: Throwable): HttpStatus {
        val responseStatusAnnotation = MergedAnnotations
            .from(error.javaClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)[ResponseStatus::class.java]

        return if (error is ResponseStatusException) {
            error.status
        } else responseStatusAnnotation.getValue("code", HttpStatus::class.java)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}