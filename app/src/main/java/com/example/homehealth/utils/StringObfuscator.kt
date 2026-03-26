package com.example.homehealth.utils

object StringObfuscator {
    private val KEY = byteArrayOf(0x4A, 0x3F, 0x2B, 0x1C, 0x5D, 0x6E, 0x7F, 0x8A.toByte())

    fun decrypt(encrypted: ByteArray): String {
        val decrypted = ByteArray(encrypted.size)
        for (i in encrypted.indices) {
            decrypted[i] = (encrypted[i].toInt() xor KEY[i % KEY.size].toInt()).toByte()
        }
        return String(decrypted, Charsets.UTF_8)
    }
}