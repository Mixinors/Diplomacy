import arrow.core.Option
import kotlinx.serialization.Serializable

@Serializable
class World {
    private val nations = mutableListOf<Nation>()
    private val provinces = mutableListOf<Province>()
    private val groups = mutableListOf<Group>()
    private val orders = mutableListOf<Order>()
    private val trades = mutableListOf<Trade>()

    fun getNation(name: String): Option<Nation> {
        return Option.fromNullable(nations.find { it.name == name })
    }

    fun getProvince(name: String): Option<Province> {
        return Option.fromNullable(provinces.find { it.name == name })
    }

    fun getGroup(location: Location, type: Group.Type): Option<Group> {
        return Option.fromNullable(groups.find { it.location == location && it.type == type })
    }

    fun getNations(): List<Nation> {
        return nations.toList()
    }

    fun getProvinces(): List<Province> {
        return provinces.toList()
    }

    fun getGroups(): List<Group> {
        return groups.toList()
    }

    fun getOrders(): List<Order> {
        return orders.toList()
    }
    
    fun getTrades(): List<Trade> {
        return trades.toList()
    }

    fun addNation(nation: Nation): Option<String> {
        return if (nations.contains(nation)) {
            "Attempted to add nation, when the world already contained it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            nations.add(nation)
            Option.empty()
        }
    }

    fun addProvince(province: Province): Option<String> {
        return if (provinces.contains(province)) {
            "Attempted to add province, when the world already contained it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            provinces.add(province)
            Option.empty()
        }
    }

    fun addGroup(group: Group): Option<String> {
        return if (groups.contains(group)) {
            "Attempted to add group, when the world already contained it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            groups.add(group)
            Option.empty()
        }
    }

    fun addOrder(order: Order): Option<String> {
        return if (orders.contains(order)) {
            "Attempted to add order, when the world already contained it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            orders.add(order)
            Option.empty()
        }
    }

    fun addTrade(trade: Trade): Option<String> {
        return if (trades.contains(trade)) {
            "Attempted to add trade, when the world already contained it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            trades.add(trade)
            Option.empty()
        }
    }

    fun removeNation(nation: Nation): Option<String> {
        return if (!nations.contains(nation)) {
            "Attempted to remove nation, when the world did not contain it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            nations.remove(nation)
            Option.empty()
        }
    }

    fun removeProvince(province: Province): Option<String> {
        return if (!provinces.contains(province)) {
            "Attempted to remove province, when the world did not contain it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            provinces.remove(province)
            Option.empty()
        }
    }

    fun removeGroup(group: Group): Option<String> {
        return if (!groups.contains(group)) {
            "Attempted to remove group, when the world did not contain it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            groups.remove(group)
            Option.empty()
        }
    }

    fun removeOrder(order: Order): Option<String> {
        return if (!orders.contains(order)) {
            "Attempted to remove order, when the world did not contain it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            orders.remove(order)
            Option.empty()
        }
    }

    fun removeTrade(trade: Trade): Option<String> {
        return if (!trades.contains(trade)) {
            "Attempted to remove trade, when the world did not contain it!".let { warn ->
                println(warn)
                Option.just(warn)
            }
        } else {
            trades.remove(trade)
            Option.empty()
        }
    }
}