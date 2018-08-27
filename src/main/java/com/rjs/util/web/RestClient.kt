package com.rjs.util.web

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.util.logging.Level
import java.util.logging.Logger

class RestClient {
	val logger = Logger.getLogger(RestClient::class.java.name)

	fun <T> exchange(
			httpMethod: HttpMethod,
			url: String,
			username: String = "",
			password: String = "",
			requestObject: String = "",
			typeReference: ParameterizedTypeReference<T>,
			uriParameters: Map<String, String> = mapOf()): ResponseEntity<T> {
		logger.info("Performing ${httpMethod.name.toLowerCase()} method to: $url")
		val httpUtil = HttpUtil()
		val httpEntity = httpUtil.createHttpEntity(requestObject, username, password, MediaType.APPLICATION_JSON)

		return execute(url, httpMethod, httpEntity, typeReference, uriParameters)
	}

	fun <R, T> exchange(restExchange: RestExchange<R, T>): ResponseEntity<T> {
		val httpUtil = HttpUtil()
		val httpEntity = httpUtil.createHttpEntity(restExchange.requestObject, restExchange.username,
				restExchange.password, MediaType.APPLICATION_JSON)
		return execute(restExchange.url, restExchange.httpMethod, httpEntity, restExchange.typeReference,
				restExchange.uriParams)
	}

	private fun <R, T> execute(url: String,
							   httpMethod: HttpMethod,
							   httpEntity: HttpEntity<R>,
							   typeReference: ParameterizedTypeReference<T>,
							   uriParameters: Map<String, String>): ResponseEntity<T> {
		val restTemplate = RestTemplate()
		val errorHandler = WebServiceErrorHandler(url)
		restTemplate.errorHandler = errorHandler

		try {
			return if (!uriParameters.isEmpty()) {
				restTemplate.exchange(url, httpMethod, httpEntity, typeReference, uriParameters)
			}
			else {
				restTemplate.exchange(url, httpMethod, httpEntity, typeReference)
			}
		}
		catch (e: RestClientException) {
			val status = HttpStatus.SERVICE_UNAVAILABLE
			val message = "Web service error calling $url: ${status.reasonPhrase} ($status)\n" +
					"Cause: ${e.message}"
			logger.log(Level.SEVERE, message, e)

			throw WebServiceException(status, message, e)
		}
	}
}