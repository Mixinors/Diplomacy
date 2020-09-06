import kotlinx.serialization.Serializable

@Serializable
data class Province(val owner: Nation, val name: String, val type: Type, val center: Boolean = false) {
    @Serializable
    enum class Type {
        Sea,
        Land,
        Inland,
        None,
    }

    companion object {
        val NONE = Province(Nation.NONE, "none", Type.None)
    }
}