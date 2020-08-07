import org.w3c.files.File

val ZERO_POPULATION: Map<PopulationTier, Int> = PopulationTier.values().map { it to 0 }.toMap()

val ZERO_GOODS: Map<Goods, Float> = Goods.values().map { it to 0F }.toMap()

data class ReportGame(val name: String, val phases: List<ReportGamePhase>) {
  fun csvCollection(directory: File) {}
}

data class ReportGamePhase(
  val name: String,
  val islands: List<ReportGamePhaseIsland>
) {
  fun csvCollection(directory: File, prefix: String) {}
}

data class ReportGamePhaseIsland(
  val name: String,
  val population: Map<PopulationTier, Int>, // the total population for each tier on the island
  val consumption: Map<Goods, Float>, // the amount of goods of each type consumed by all buildings on the island
  val production: Map<Goods, Float>, // the amount of goods of each type produced by all buildings on the island
  val tradeRoutes: Map<Goods, Float>, // the amount of goods of each type acquired for this island via trade routes
  val buildings: Map<BuildingType, ReportGamePhaseIslandBuilding>, // the amount of goods produced or consumed by each building type on the island
  val balance: Map<Goods, Float>,
  val balanceAfterTrade: Map<Goods, Float>,
  val solutions: Map<BuildingType, ReportGamePhaseIslandBuilding>
) {
  fun csv(directory: File, prefix: String) {}
}

data class ReportGamePhaseIslandBuilding(
  val quantity: Int,
  val productivity: Float,
  val productionAndConsumption: Map<Goods, Float>,
  val modifyingItems: List<Any> = listOf()
)

class ReportGenerator {

  fun generateReport(game: Game): ReportGame = ReportGame(
    name = game.name,
    phases = game.phases.map { phase ->
      ReportGamePhase(
        name = phase.name,
        islands = phase.islands.map { island ->
          ReportGamePhaseIsland(
            name = island.name,
            population = calculateIslandPopulationTotals(island),
            consumption = calculateIslandConsumptionTotals(island),
            production = calculateIslandProductionTotals(island),
            tradeRoutes = calculateTradeRouteGoods(phase, island),
            buildings = calculateIslandBuildingReports(island),
            balance = calculateBalance(island),
            balanceAfterTrade = mapOf(),
            solutions = solveForGamePhaseIsland(game, phase, island)
          )
        }
      )
    }
  )

  private fun solveForGamePhaseIsland(
    game: Game,
    phase: GamePhase,
    island: GamePhaseIsland
  ): Map<BuildingType, ReportGamePhaseIslandBuilding> {
    // what does this island need? (including any over-production requests)
    // what does it get already from trade routes?
    // what do just the residences on this island require? use this as the basis for a new "needs list". For each commodity it contains:
    // - find the building type that produces the commodity
    //    -> (Coal and gold ore are produced by 2 building types, so check "unlock condition" against
    //       global game population stats to verify which building type to use; allow user to configure
    //       a preferred order for such cases, including trade routes/other island's resources, but
    //       try to keep the algorithm as generic as possible, obviously)
    // - establish the type's baseline productivity, given its island's items and calculation constraints
    // - work out the smallest quantity of buildings of that type to satisfy the demand + over-prod
    // - work out whether any productivity adjustment needs to be made to avoid massive over-prod
    // - mark the need as satisfied in the "needs list"
    // - establish what this building will need itself when working at this level and add it to the "needs list"
    // - recurse onto the next unsatisfied need in the "needs list"
    // transform the needs map into the report format and return it
    return mapOf()
  }

  data class NeedsListItem(
    val goods: Goods,
    val tpm: Float,
    var type: BuildingType?,
    var productivity: Float?,
    var islandItems: List<IslandItem> = listOf(), // any island items required
    var buildingItems: List<BuildingItem> = listOf(), // any building items required
    var tradeRoutes: List<GamePhaseTradeRoute> = listOf() // and trade routes required (or that satisfied this need)
  )

  private fun calculateTradeRouteGoods(phase: GamePhase, island: GamePhaseIsland): Map<Goods, Float> {
//    val relevant = phase.tradeRoutes.filter { route -> route.goods.any { it.value.contains(island) } }
//    println("Found relevant trade route(s) for island ${island.name}: $relevant")
//    return relevant.map { route ->
//      route.strategy.islandGoods(route, route.goods.flatMap { trade ->
//        trade.value.map { it to calculateBalance(it) }
//      }.toMap())
//    }.fold(ZERO_GOODS) { aggregateGoods, routeGoods ->
//      aggregateGoods.map { goods ->
//        println("Evaluating goods $goods")
//        val amount = routeGoods[island]?.get(goods.key) ?: 0F
//        println("Found $amount ${goods.key}")
//        val balance = goods.value + amount
//        println("Balance: $balance")
//        goods.key to balance
//      }.toMap()
//    }
    return mapOf()
  }

  private fun calculateBalance(island: GamePhaseIsland): Map<Goods, Float> {
    val production = calculateIslandProductionTotals(island)
    val consumption = calculateIslandConsumptionTotals(island)
    return ZERO_GOODS.map {
      it.key to ((production[it.key] ?: 0F) - (consumption[it.key] ?: 0F))
    }.toMap()
  }

  private fun calculateIslandBuildingReports(
    island: GamePhaseIsland
  ): Map<BuildingType, ReportGamePhaseIslandBuilding> = island.buildingGroups
    .flatMap { it.buildings }
    .groupBy { it.type }
    .map { entry ->
      entry.key to ReportGamePhaseIslandBuilding(
      quantity = entry.value.map { it.quantity }.sum(),
      productivity = (entry.value.map { (100 + it.productivityAdjustment) * it.quantity }.sum().toFloat() / entry.value.map { it.quantity }.sum().toFloat()) / 100F,
      productionAndConsumption = entry.value.fold(ZERO_GOODS) { goods, building ->
        // allow items to modify the building profile
        val profile = modifyBuildingTypeProfile(island, island.findBuildingGroup(building), building, true)
        // calculate production across buildings of this type on the island
        val production = profile.production.map {
          it.goods to (it.rate * building.quantity)
        }.toMap()
        // calculate consumption across buildings of this type on the island
        // configured consumption values for building types are expressed as positive floats for the
        // sake of clarity and simplicity elsewhere; therefore, they are negated here
        val consumption = profile.consumption.map {
          it.goods to -(it.rate * building.quantity)
        }.toMap()
        goods.map { entry ->
          entry.key to (entry.value + (consumption[entry.key] ?: 0F) + (production[entry.key] ?: 0F))
        }.toMap()
      })
    }.toMap()

  private fun calculateIslandPopulationTotals(island: GamePhaseIsland): Map<PopulationTier, Int> =
    island.buildingGroups.fold(ZERO_POPULATION) { p1, group ->
      group.buildings.fold(p1) { p2, building ->
        // allow items to modify the building profile
        val profile = modifyBuildingTypeProfile(island, group, building)
        // calculate the building's individual contribution to the island population
        val contribution = profile.population.contribution(island, group, building)
        // add the contribution to the aggregate population map
        p2.map { entry ->
          entry.key to (entry.value + (contribution[entry.key] ?: 0))
        }.toMap()
      }
    }

  private fun calculateIslandConsumptionTotals(island: GamePhaseIsland): Map<Goods, Float> =
    island.buildingGroups.fold(ZERO_GOODS) { c1, group ->
      group.buildings.fold(c1) { c2, building ->
        // allow items to modify the building profile
        val profile = modifyBuildingTypeProfile(island, group, building, true)
        // calculate the building's individual consumption
        val consumption = profile.consumption.map {
          it.goods to (it.rate * building.quantity)
        }.toMap()
        c2.map { entry ->
          entry.key to (entry.value + (consumption[entry.key] ?: 0F))
        }.toMap()
      }
    }

  private fun calculateIslandProductionTotals(island: GamePhaseIsland): Map<Goods, Float> =
    island.buildingGroups.fold(ZERO_GOODS) { c1, group ->
      group.buildings.fold(c1) { c2, building ->
        // allow items applied to the island or group to modify the building profile
        val profile = modifyBuildingTypeProfile(island, group, building)
        // calculate the building's individual production
        val production = profile.production.map {
          it.goods to (it.rate * building.quantity)
        }.toMap()
        c2.map { entry ->
          entry.key to (entry.value + (production[entry.key] ?: 0F))
        }.toMap()
      }
    }

  private fun modifyBuildingTypeProfile(
    island: GamePhaseIsland,
    group: GamePhaseIslandBuildingGroup,
    building: GamePhaseIslandBuilding,
    filterUntriggeredNeeds: Boolean = false // avoid infinite recursion: only filter needs when calculating consumption
  ): BuildingTypeProfile {
    // filter out any consumption needs that have not yet been triggered due to insufficient population
    val populationUpdated =
      if (filterUntriggeredNeeds) filterUntriggeredNeeds(island, building)
      else building.type.profile
    // apply any productivity adjustments
    val productivityUpdated = applyProductivityAdjustment(building, populationUpdated)
    // apply any island-wide items, such as cultural sets, to the profile
    val islandUpdated = island.items.fold(productivityUpdated) { profile, i1 ->
      i1.modifyProfile(building.type, profile)
    }
    // now allow building group items to modify the profile
    return group.items.fold(islandUpdated) { profile, item ->
      item.modifyProfile(building.type, profile)
    }
  }

  // we take the building and a potentially already updated profile for it
  private fun applyProductivityAdjustment(
    building: GamePhaseIslandBuilding,
    profile: BuildingTypeProfile
  ): BuildingTypeProfile {
    return profile.copy(
      production = profile.production.map {
        it.copy(rate = it.rate * ((100F + building.productivityAdjustment) / 100F))
      }
    )
  }

  private fun filterUntriggeredNeeds(
    island: GamePhaseIsland,
    building: GamePhaseIslandBuilding
  ): BuildingTypeProfile {
    val population = calculateIslandPopulationTotals(island)
    return building.type.profile.copy(
      consumption = building.type.profile.consumption.filter { consumption ->
        consumption.population.none { it.value >= (population[it.key] ?: 0) }
      }
    )
  }
}
