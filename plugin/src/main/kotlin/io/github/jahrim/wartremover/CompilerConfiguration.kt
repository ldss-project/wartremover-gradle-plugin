package io.github.jahrim.wartremover

/** Configuration for the parameters of the scala compiler. */
@FunctionalInterface
fun interface CompilerConfiguration {
    /** @return a list of parameters for the configuration of the scala compiler */
    fun toCompilerOptions(): List<String>

    /**
     * @param other the specified configuration
     * @return a new configuration that combines this configuration with the specified configuration
     */
    operator fun plus(other: CompilerConfiguration): CompilerConfiguration =
        CompilerConfiguration { this.toCompilerOptions() + other.toCompilerOptions() }
}
