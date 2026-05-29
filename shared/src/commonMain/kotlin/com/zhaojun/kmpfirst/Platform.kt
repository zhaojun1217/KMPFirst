package com.zhaojun.kmpfirst

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform