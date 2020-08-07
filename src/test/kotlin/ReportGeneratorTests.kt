import kotlin.test.Test
import kotlin.test.assertEquals

class ReportGeneratorTests {

  val generator = ReportGenerator()

  @Test
  fun farmerPopulationPerResidenceShouldBeZeroGivenNeitherMarketNorBasicNeeds() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = false,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 270
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION, islandReport.population)
  }

  @Test
  fun farmerPopulationPerResidenceShouldBeFiveGivenLessThan10Residences() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = false,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 9
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION + (PopulationTier.FARMER to (5 * 9)), islandReport.population)
  }

  @Test
  fun farmerPopulationPerResidenceShouldBeThreeGiven10ResidencesWithBasicNeedsButNoMarket() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 10
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION + (PopulationTier.FARMER to (3 * 10)), islandReport.population)
  }

  @Test
  fun farmerPopulationPerResidenceShouldBeFiveGiven19ResidencesWithBasicNeedsButNoMarket() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 19
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION + (PopulationTier.FARMER to (5 * 19)), islandReport.population)
  }

  @Test
  fun farmerPopulationPerResidenceShouldBeFiveGiven19ResidencesWithoutBasicNeedsButWithMarket() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = false,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 19
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION + (PopulationTier.FARMER to (5 * 19)), islandReport.population)
  }

  @Test
  fun farmerPopulationPerResidenceShouldBeTenGiven19ResidencesWithBasicNeedsAndMarket() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 19
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(ZERO_POPULATION + (PopulationTier.FARMER to (10 * 19)), islandReport.population)
  }

  @Test
  fun shouldCalculateMultiTierPopulation() {
    // given
    val game = Game(
      name = "My More Advanced Game",
      phases = listOf(GamePhase(
        name = "Main island investors",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(
                PublicService.MARKETPLACE to 1F,
                PublicService.SCHOOL to 1F,
                PublicService.UNIVERSITY to 1F,
                PublicService.ELECTRICITY to 1F
              ),
              buildings = listOf(
                // we'll put each tier except farmers on their upgrade threshold
                GamePhaseIslandBuilding(BuildingType.FARMER_RESIDENCE, 283), // the rest of a 540 res city
                GamePhaseIslandBuilding(BuildingType.WORKER_RESIDENCE, 42), // school unlocked
                GamePhaseIslandBuilding(BuildingType.ARTISAN_RESIDENCE, 54), // university unlocked
                GamePhaseIslandBuilding(BuildingType.ENGINEER_RESIDENCE, 49), // light bulbs unlocked
                GamePhaseIslandBuilding(BuildingType.INVESTOR_RESIDENCE, 109) // steam carriages unlocked
              )
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    val expected = ZERO_POPULATION + mapOf(
      PopulationTier.FARMER to (10 * 283),
      PopulationTier.WORKER to (20 * 42),
      PopulationTier.ARTISAN to (30 * 54),
      PopulationTier.ENGINEER to (40 * 49),
      PopulationTier.INVESTOR to (50 * 109)
    )
    assertEquals(expected, islandReport.population)
  }

  @Test
  fun shouldProducesEstimatesForSimpleSinglePhaseGame() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          meetLuxuryNeeds = true,
          overProductions = mapOf(
            // ask for over-production of goods so that calc result allows for stock build-up
            Goods.FISH to 0.2F,
            Goods.WORK_CLOTHES to 0.2F,
            Goods.SCHNAPPS to 0.2F
          ),
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              // I have a bunch of unoptimised farmer residences. I want 270 of them to begin with.
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F, PublicService.PUB to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 270
              ))
            ),
            GamePhaseIslandBuildingGroup(
              // Let's specify some public services, so that their costs are account for
              // note that I specify their influence separately on the residence group
              name = "Public Services",
              buildings = listOf(
                GamePhaseIslandBuilding(BuildingType.MARKETPLACE, 2),
                GamePhaseIslandBuilding(BuildingType.PUB, 2),
                GamePhaseIslandBuilding(BuildingType.FIRE_STATION, 2)
              )
            ),
            GamePhaseIslandBuildingGroup(
              name = "Production Buildings",
              buildings = listOf(
                // gonna say I want 8 sawmills to produce the construction materials
                // otherwise time estimates would be infinite as I have no other need for timber
                GamePhaseIslandBuilding(BuildingType.SAWMILL, 8),
                GamePhaseIslandBuilding(BuildingType.FISHERY, 4, -11)
              )
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(2700, islandReport.population[PopulationTier.FARMER])
    assertEquals((0.0004166667F * 60) * 270, islandReport.consumption[Goods.FISH])
    assertEquals((0.000512821F * 60) * 270, islandReport.consumption[Goods.WORK_CLOTHES])
    assertEquals(4 * (2F * 0.89F), islandReport.production[Goods.FISH])
    assertEquals(4 * 8F, islandReport.production[Goods.TIMBER])
    assertEquals(4, islandReport.buildings[BuildingType.FISHERY]!!.quantity)
    assertEquals(0.89F, islandReport.buildings[BuildingType.FISHERY]!!.productivity)
    assertEquals(32F, islandReport.buildings[BuildingType.SAWMILL]!!.productionAndConsumption[Goods.TIMBER])
    assertEquals(-32F, islandReport.buildings[BuildingType.SAWMILL]!!.productionAndConsumption[Goods.WOOD])
    assertEquals(-32F, islandReport.balance[Goods.WOOD])
    assertEquals(32F, islandReport.balance[Goods.TIMBER])
    assertEquals((4 * (2F * 0.89F)) - ((0.0004166667F * 60) * 270), islandReport.balance[Goods.FISH])
    // having reported on the status quo, it should now produce some solutions
    // these should make use of the enhancements permitted by game.itemCalculations where necessary
    // and they must ensure that any necessary over-production of goods is accounted for
//    assertEquals(ReportGamePhaseIslandBuilding(
//      quantity = 4,
//      productivity = 0.89F,
//      productionAndConsumption = mapOf(Goods.FISH to (4 * (2F * 0.89F)))
//    ), islandReport.solutions[BuildingType.FISHERY])
  }

  @Test
  fun farmerPopulationSplitIntoTwoGroupsStillConsumeTheSameAmountOfFish() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 135
              ))
            ),
            GamePhaseIslandBuildingGroup(
              name = "More Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 135
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(270 * 0.0004166667F * 60, islandReport.consumption[Goods.FISH])
  }

  @Test
  fun farmersDonNotEatFishUntilThereAre50OfThemOnAnIsland() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 9
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(0F, islandReport.consumption[Goods.FISH])
  }

  @Test
  fun farmersDonNotWearClothesUntilThereAre150OfThemOnAnIsland() {
    // given
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(GamePhaseIsland(
          name = "My starting island",
          meetBasicNeeds = true,
          buildingGroups = listOf(
            GamePhaseIslandBuildingGroup(
              name = "Residences",
              publicServices = mapOf(PublicService.MARKETPLACE to 1F),
              buildings = listOf(GamePhaseIslandBuilding(
                type = BuildingType.FARMER_RESIDENCE,
                quantity = 18
              ))
            )
          )
        ))
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandReport = phaseReport.islands[0]
    assertEquals(0F, islandReport.consumption[Goods.WORK_CLOTHES])
  }

  @Test
  fun shouldAccountForTradeRoutesWhenProducingEstimatesInGameWithTwoOrMoreIslands() {
    // given
    val a = GamePhaseIsland(
      name = "Island A",
      meetBasicNeeds = true,
      buildingGroups = listOf(
        GamePhaseIslandBuildingGroup(
          name = "Residences",
          publicServices = mapOf(PublicService.MARKETPLACE to 1F),
          buildings = listOf(
            GamePhaseIslandBuilding(BuildingType.FARMER_RESIDENCE, 320),
            // this island does not have enough fish!
            GamePhaseIslandBuilding(BuildingType.FISHERY, 2)
          )
        )
      )
    )
    val b = GamePhaseIsland(
      name = "Island B",
      meetBasicNeeds = true,
      buildingGroups = listOf(
        GamePhaseIslandBuildingGroup(
          name = "Residences",
          publicServices = mapOf(PublicService.MARKETPLACE to 1F),
          buildings = listOf(
            GamePhaseIslandBuilding(BuildingType.FARMER_RESIDENCE, 320),
            // this island has too many fish!
            GamePhaseIslandBuilding(BuildingType.FISHERY, 6)
          )
        )
      )
    )
    val game = Game(
      name = "My Simple Game",
      phases = listOf(GamePhase(
        name = "Main island farmers",
        islands = listOf(a),
        tradeRoutes = listOf(
          GamePhaseTradeRoute(
            name = "Fish from B to A",
            goods = mapOf(Goods.FISH to listOf(b, a)),
            strategy = GamePhaseIslandTradeRouteStrategy.EXCESS,
            ships = mapOf(ShipType.SCHOONER to 1)
          )
        )
      )))
    // when
    val report = generator.generateReport(game)
    // then
    val phaseReport = report.phases[0]
    val islandAReport = phaseReport.islands[0]
    val islandBReport = phaseReport.islands[1]
    val fishProducedOnIslandA = 4F
    val fishProducedOnIslandB = 12F
    val fishConsumedOnIslandsAandB = 320 * 0.0004166667F * 60;
    val surplusOnB = fishProducedOnIslandB - fishConsumedOnIslandsAandB
    assertEquals(fishProducedOnIslandA, islandAReport.production[Goods.FISH])
    assertEquals(surplusOnB, islandAReport.tradeRoutes[Goods.FISH])
    assertEquals(-surplusOnB, islandBReport.tradeRoutes[Goods.FISH])
    assertEquals((fishProducedOnIslandA + surplusOnB) - fishConsumedOnIslandsAandB, islandAReport.balanceAfterTrade[Goods.FISH])
    assertEquals((fishProducedOnIslandB - surplusOnB) - fishConsumedOnIslandsAandB, islandAReport.balanceAfterTrade[Goods.FISH])
  }
}