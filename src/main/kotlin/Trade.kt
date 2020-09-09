import kotlinx.serialization.Serializable

@Serializable
data class Trade(val from: Location, val to: Location)