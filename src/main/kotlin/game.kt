import com.benasher44.uuid.uuid4

data class Game(
  val name: String,
  val phases: List<GamePhase> = listOf()
) {
  val id: String = uuid4().toString()
}

data class GamePhase(
  val name: String,
  val islands: List<GamePhaseIsland> = listOf(),
  val tradeRoutes: List<GamePhaseTradeRoute> = listOf()
) {
  val id: String = uuid4().toString()
}

data class GamePhaseIsland(
  val name: String,
  val meetBasicNeeds: Boolean = true,
  val meetLuxuryNeeds: Boolean = true,
  val items: List<IslandItem> = listOf(),
  val overProductions: Map<Goods, Float> = mapOf(),
  val buildingGroups: List<GamePhaseIslandBuildingGroup> = listOf(),
  val itemCalculations: List<GamePhaseIslandItemCalculations> = listOf()
) {
  val id: String = uuid4().toString()

  fun countBuildings(type: BuildingType): Int = buildingGroups.flatMap { it.buildings }
    .filter { it.type == type }
    .map { it.quantity }
    .sum()

  // a group must always exist for any building
  fun findBuildingGroup(building: GamePhaseIslandBuilding): GamePhaseIslandBuildingGroup =
    buildingGroups.find { it.buildings.contains(building) }!!
}

data class GamePhaseIslandItemCalculations(
  val type: BuildingType,
  val items: List<BuildingItem>,
  val electricity: Boolean
) {
  val id: String = uuid4().toString()
}

data class GamePhaseIslandBuildingGroup(
  val name: String,
  val publicServices: Map<PublicService, Float> = mapOf(),
  val items: List<BuildingItem> = listOf(),
  val buildings: List<GamePhaseIslandBuilding> = listOf()
) {
  val id: String = uuid4().toString()
}

data class GamePhaseIslandBuilding(
  val type: BuildingType,
  val quantity: Int,
  val productivityAdjustment: Int = 0
) {
  val id: String = uuid4().toString()
}

data class GamePhaseTradeRoute(
  val name: String,
  val islands: List<GamePhaseIsland> = listOf(),
  val strategy: GamePhaseIslandTradeRouteStrategy = GamePhaseIslandTradeRouteStrategy.EXCESS,
  val ships: Map<ShipType, Int> = mapOf()
) {
  val id: String = uuid4().toString()
}

enum class GamePhaseIslandTradeRouteStrategy {
  EXCESS, SHARE
}
