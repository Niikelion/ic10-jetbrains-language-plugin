package com.niikelion.ic10_language.simulation.environment

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType

class Ic10EnvSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> =
        listOf(Ic10EnvSchemaProvider())
}

private class Ic10EnvSchemaProvider : JsonSchemaFileProvider {
    private val cachedSchema by lazy {
        JsonSchemaProviderFactory.getResourceFile(
            Ic10EnvSchemaProvider::class.java,
            "/schemas/ic10env.schema.json"
        )
    }

    override fun isAvailable(file: VirtualFile): Boolean =
        file.extension == "ic10env"

    override fun getName(): String = "IC10 Environment Config"

    override fun getSchemaFile(): VirtualFile? = cachedSchema

    override fun getSchemaType(): SchemaType = SchemaType.schema
}
