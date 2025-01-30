package com.example.apps1

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.module.GlideModule
import java.io.InputStream


class GlideModuleWithHeaders : GlideModule {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(String::class.java, InputStream::class.java, HeaderLoaderFactory())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        TODO("Not yet implemented")
    }
}