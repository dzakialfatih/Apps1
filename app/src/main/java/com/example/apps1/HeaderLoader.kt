package com.example.apps1
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import java.io.InputStream

class HeaderLoader(context: ModelLoader<GlideUrl, InputStream>) : BaseGlideUrlLoader<String>(context) {

    companion object {
        private const val AUTHORIZATION = "10a18f33fe3b1a28:c597e88d38af5cfb78e08b232a25f063"

        fun getUrlWithHeaders(url: String): GlideUrl {
            return GlideUrl(url, LazyHeaders.Builder()
                .addHeader("x-api-key", AUTHORIZATION)
                .build()
            )
        }
    }


    override fun getUrl(model: String?, width: Int, height: Int, options: Options?): String {
        TODO("Not yet implemented")
    }

    override fun handles(model: String): Boolean {
        TODO("Not yet implemented")
    }
}
