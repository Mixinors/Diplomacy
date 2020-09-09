import arrow.core.Option
import kotlinx.serialization.Serializable

@Serializable
data class Order(val owner: Nation, val location: Location, val target: Location = Location.NONE, val type: Type, val groupType: Group.Type) {
    @Serializable
    enum class Type {
        Hold,
        Move,
        Support,
        Disband,
    }
}

@Serializable
data class Group(val owner: Nation, val location: Location, val type: Type, val strength: Int = 1, val cost: Int = 1) {
    @Serializable
    enum class Type {
        Army,
        Fleet,
        Wing,
    }
}