/*
MIT License

Copyright (c) 2023 Cesario Jahrim Gabriele

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
