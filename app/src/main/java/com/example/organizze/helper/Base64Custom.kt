package com.example.organizze.helper

import android.util.Base64

class Base64Custom {
    companion object {
        fun encodeBase64(text: String) =
            Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)
                .replace("(\\n|\\r)", "").trim()

        fun decodeBase64(text: String) = String(Base64.decode(text, Base64.DEFAULT))
    }
}