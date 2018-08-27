package com.rjs.util.web

import org.springframework.http.HttpStatus

class WebServiceException(val status: HttpStatus, message: String, cause: Throwable? = null): RemoteServiceException(message, cause)