package com.rjs.util.web

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod

class RestExchange<out REQ, RES>(val url: String = "",
								 val typeReference: ParameterizedTypeReference<RES>,
								 val httpMethod: HttpMethod = HttpMethod.GET,
								 val username: String = "",
								 val password: String = "",
								 val requestObject: REQ,
								 val uriParams: Map<String, String> = emptyMap()
)