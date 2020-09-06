import kotlinx.serialization.Serializable

@Serializable
data class Location(val nation: Nation, val province: Province) {
    companion object {
        val NONE = Location(Nation.NONE, Province.NONE)
    }
}