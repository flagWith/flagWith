package com.flagwith.flagwith.global.exception

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
