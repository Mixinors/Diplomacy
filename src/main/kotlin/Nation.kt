import kotlinx.serialization.Serializable

@Serializable
data class Nation(val name: String, val code: String, val owner: Int) {
    companion object {
        val NONE = Nation("none", "none", -1)
    }
}