package io.github.jahrim.wartremover

import kotlinx.serialization.Serializable

/** Configuration for the parameters of the WartRemover plugin for the scala compiler. */
@Serializable
data class WartRemoverCompilerConfiguration(
    private val warts: Map<String, TraverserType>,
) : CompilerConfiguration {
    override fun toCompilerOptions(): List<String> =
        this.warts
            .filter { it.value != TraverserType.Ignore }
            .map {
                when (it.value) {
                    TraverserType.Error -> errorTraverser(it.key)
                    TraverserType.Warning -> warningTraverser(it.key)
                    else -> throw NotImplementedError("The traverser type for {$it} has not been implemented yet.")
                }
            }

    companion object {
        /**
         * @param wart the specified wart name
         * @return the traverser name for an error traverser for the specified wart.
         */
        private fun errorTraverser(wart: String): String = traverser("traverser", wart)

        /**
         * @param wart the specified wart name
         * @return the traverser name for a warning traverser for the specified wart.
         */
        private fun warningTraverser(wart: String): String = traverser("only-warn-traverser", wart)

        /**
         * @param wart the specified wart name
         * @param type the name of the specified traverser type
         * @return the traverser name for a traverser of the specified type for the specified wart.
         */
        private fun traverser(type: String, wart: String): String = "-P:wartremover:$type:org.wartremover.warts.$wart"
    }

    /** AST traverser type for WartRemover. */
    enum class TraverserType {
        /** Treat warts as errors. */ Error,

        /** Treat warts as warnings. */ Warning,

        /** Ignore warts. */ Ignore,
    }
}
