package com.vinceglb.paraspace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform