package com.example.apps1
import com.bumptech.glide.load.model.*
import java.io.InputStream

class HeaderLoaderFactory : ModelLoaderFactory<String, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, InputStream> {
        return HeaderLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }

    override fun teardown() {
        // Tidak ada resource yang perlu dibersihkan
    }
}
