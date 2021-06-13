package ar.edu.microprocesador

class BusinessException(message: String) : RuntimeException(message) {}

class SystemException(message: String) : RuntimeException(message) {}