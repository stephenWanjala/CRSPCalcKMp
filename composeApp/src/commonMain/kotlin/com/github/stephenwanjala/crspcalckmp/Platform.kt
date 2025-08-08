package com.github.stephenwanjala.crspcalckmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform