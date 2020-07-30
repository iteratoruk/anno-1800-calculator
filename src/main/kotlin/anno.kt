import kotlin.math.roundToInt

// All data is taken from https://anno1800.fandom.com/wiki/

fun tpsToTpm(f: Float): Float = f * 60

enum class Goods(val category: GoodsCategory) {
  // Consumer Goods
  FISH(GoodsCategory.CONSUMER_GOODS),
  WORK_CLOTHES(GoodsCategory.CONSUMER_GOODS),
  SCHNAPPS(GoodsCategory.CONSUMER_GOODS),
  SAUSAGES(GoodsCategory.CONSUMER_GOODS),
  BREAD(GoodsCategory.CONSUMER_GOODS),
  SOAP(GoodsCategory.CONSUMER_GOODS),
  BEER(GoodsCategory.CONSUMER_GOODS),
  CANNED_FOOD(GoodsCategory.CONSUMER_GOODS),
  SEWING_MACHINES(GoodsCategory.CONSUMER_GOODS),
  RUM(GoodsCategory.CONSUMER_GOODS),
  FUR_COATS(GoodsCategory.CONSUMER_GOODS),
  GLASSES(GoodsCategory.CONSUMER_GOODS),
  COFFEE(GoodsCategory.CONSUMER_GOODS),
  PENNY_FARTHINGS(GoodsCategory.CONSUMER_GOODS),
  LIGHT_BULBS(GoodsCategory.CONSUMER_GOODS),
  POCKET_WATCHES(GoodsCategory.CONSUMER_GOODS),
  CHAMPAGNE(GoodsCategory.CONSUMER_GOODS),
  CIGARS(GoodsCategory.CONSUMER_GOODS),
  CHOCOLATE(GoodsCategory.CONSUMER_GOODS),
  JEWELLERY(GoodsCategory.CONSUMER_GOODS),
  GRAMOPHONES(GoodsCategory.CONSUMER_GOODS),
  STEAM_CARRIAGES(GoodsCategory.CONSUMER_GOODS),
  FRIED_PLANTAINS(GoodsCategory.CONSUMER_GOODS),
  PONCHOS(GoodsCategory.CONSUMER_GOODS),
  TORTILLAS(GoodsCategory.CONSUMER_GOODS),
  BOWLER_HATS(GoodsCategory.CONSUMER_GOODS),

  // Construction Materials
  TIMBER(GoodsCategory.CONSTRUCTION_MATERIALS),
  BRICKS(GoodsCategory.CONSTRUCTION_MATERIALS),
  SAILS(GoodsCategory.CONSTRUCTION_MATERIALS),
  STEEL_BEAMS(GoodsCategory.CONSTRUCTION_MATERIALS),
  WEAPONS(GoodsCategory.CONSTRUCTION_MATERIALS),
  WINDOWS(GoodsCategory.CONSTRUCTION_MATERIALS),
  REINFORCED_CONCRETE(GoodsCategory.CONSTRUCTION_MATERIALS),
  STEAM_MOTORS(GoodsCategory.CONSTRUCTION_MATERIALS),
  ADVANCED_WEAPONS(GoodsCategory.CONSTRUCTION_MATERIALS),

  // Raw Materials
  WOOD(GoodsCategory.RAW_MATERIALS),
  CLAY(GoodsCategory.RAW_MATERIALS),
  IRON(GoodsCategory.RAW_MATERIALS),
  COAL(GoodsCategory.RAW_MATERIALS),
  QUARTZ_SAND(GoodsCategory.RAW_MATERIALS),
  CEMENT(GoodsCategory.RAW_MATERIALS),
  COPPER(GoodsCategory.RAW_MATERIALS),
  ZINC(GoodsCategory.RAW_MATERIALS),
  SALTPETRE(GoodsCategory.RAW_MATERIALS),
  GOLD_ORE(GoodsCategory.RAW_MATERIALS),
  OIL(GoodsCategory.RAW_MATERIALS),
  GAS(GoodsCategory.RAW_MATERIALS),

  // Agricultural Products
  POTATOES(GoodsCategory.AGRICULTURAL_PRODUCTS),
  WOOL(GoodsCategory.AGRICULTURAL_PRODUCTS),
  PIGS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  GRAIN(GoodsCategory.AGRICULTURAL_PRODUCTS),
  HOPS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  BEEF(GoodsCategory.AGRICULTURAL_PRODUCTS),
  RED_PEPPERS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  FURS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  GRAPES(GoodsCategory.AGRICULTURAL_PRODUCTS),
  FISH_OIL(GoodsCategory.AGRICULTURAL_PRODUCTS),
  PLANTAINS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  SUGAR_CANE(GoodsCategory.AGRICULTURAL_PRODUCTS),
  COTTON(GoodsCategory.AGRICULTURAL_PRODUCTS),
  ALPACA_WOOL(GoodsCategory.AGRICULTURAL_PRODUCTS),
  CAOUTCHOUC(GoodsCategory.AGRICULTURAL_PRODUCTS),
  CORN(GoodsCategory.AGRICULTURAL_PRODUCTS),
  COFFEE_BEANS(GoodsCategory.AGRICULTURAL_PRODUCTS),
  TOBACCO(GoodsCategory.AGRICULTURAL_PRODUCTS),
  COCOA(GoodsCategory.AGRICULTURAL_PRODUCTS),
  PEARLS(GoodsCategory.AGRICULTURAL_PRODUCTS),

  // Intermediate Products
  FLOUR(GoodsCategory.INTERMEDIATE_PRODUCTS),
  TALLOW(GoodsCategory.INTERMEDIATE_PRODUCTS),
  STEEL(GoodsCategory.INTERMEDIATE_PRODUCTS),
  MALT(GoodsCategory.INTERMEDIATE_PRODUCTS),
  GLASS(GoodsCategory.INTERMEDIATE_PRODUCTS),
  GOULASH(GoodsCategory.INTERMEDIATE_PRODUCTS),
  BRASS(GoodsCategory.INTERMEDIATE_PRODUCTS),
  DYNAMITE(GoodsCategory.INTERMEDIATE_PRODUCTS),
  GOLD(GoodsCategory.INTERMEDIATE_PRODUCTS),
  FILAMENTS(GoodsCategory.INTERMEDIATE_PRODUCTS),
  WOOD_VENEERS(GoodsCategory.INTERMEDIATE_PRODUCTS),
  CHASSIS(GoodsCategory.INTERMEDIATE_PRODUCTS),
  COTTON_FABRIC(GoodsCategory.INTERMEDIATE_PRODUCTS),
  FELT(GoodsCategory.INTERMEDIATE_PRODUCTS),
  SUGAR(GoodsCategory.INTERMEDIATE_PRODUCTS)
}

enum class GoodsCategory {
  CONSUMER_GOODS, CONSTRUCTION_MATERIALS, RAW_MATERIALS, AGRICULTURAL_PRODUCTS, INTERMEDIATE_PRODUCTS
}

enum class PopulationTier {
  FARMER, WORKER, ARTISAN, ENGINEER, INVESTOR, JORNALERO, OBRERA, EXPLORER, TECHNICIAN
}

enum class InfluenceCategory {
  PROPAGANDA, TRADE, MILITARY, OPTIMISATION, CULTURE, EXPANSION, NONE
}

/*
These are mainly cultural sets that affect the fertility or consumption rates across an island but
might also include palace policies.
 */
enum class IslandItem {
  SUBALPINE_BOTANICAL_GARDEN;

  fun modifyProfile(type: BuildingType, profile: BuildingTypeProfile): BuildingTypeProfile = type.profile

  fun influences(type: BuildingType): Boolean = false
}

enum class PublicService {
  MARKETPLACE, PUB, CHURCH, SCHOOL, VARIETY_THEATRE, UNIVERSITY, ELECTRICITY, BANK, MEMBERS_CLUB,
  CHAPEL, BOXING_ARENA, CANTEEN, HEATER, POST_OFFICE
}

enum class BuildingType(val profile: BuildingTypeProfile = BuildingTypeProfile()) {
  // Residence types
  FARMER_RESIDENCE(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.FISH, tpsToTpm(0.0004166667F), mapOf(PopulationTier.FARMER to 50)),
      BuildingTypeConsumption(Goods.WORK_CLOTHES, tpsToTpm(0.000512821F), mapOf(PopulationTier.FARMER to 150)),
      BuildingTypeConsumption(Goods.SCHNAPPS, tpsToTpm(0.000555556F), mapOf(PopulationTier.FARMER to 100), true)
    ),
    population = BuildingTypePopulation(
      contribution = { island, group, building ->
        mapOf(PopulationTier.FARMER to (
          publicServiceInflux(island, group, FARMER_RESIDENCE, PublicService.MARKETPLACE, 5) +
          basicNeedsInflux(island, FARMER_RESIDENCE, 10, 3) +
          basicNeedsInflux(island, FARMER_RESIDENCE, 19, 2)) * building.quantity)
      }
    )
  )),
  WORKER_RESIDENCE(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.FISH, tpsToTpm(0.0008333334F)),
      BuildingTypeConsumption(Goods.WORK_CLOTHES, tpsToTpm(0.001025642F)),
      BuildingTypeConsumption(Goods.SCHNAPPS, tpsToTpm(0.001111112F), mapOf(), true),
      BuildingTypeConsumption(Goods.SAUSAGES, tpsToTpm(0.000333334F), mapOf(PopulationTier.WORKER to 1)),
      BuildingTypeConsumption(Goods.BREAD, tpsToTpm(0.00030303F), mapOf(PopulationTier.WORKER to 150)),
      BuildingTypeConsumption(Goods.SOAP, tpsToTpm(0.000138889F), mapOf(PopulationTier.WORKER to 300)),
      BuildingTypeConsumption(Goods.BEER, tpsToTpm(0.00025641F), mapOf(PopulationTier.WORKER to 500), true)
    ),
    population = BuildingTypePopulation(
      contribution = { island, group, building ->
        mapOf(PopulationTier.WORKER to (
          publicServiceInflux(island, group, WORKER_RESIDENCE, PublicService.MARKETPLACE, 5) +
          basicNeedsInflux(island, WORKER_RESIDENCE, 0, 3) +
          basicNeedsInflux(island, WORKER_RESIDENCE, 0, 2) +
          basicNeedsInflux(island, WORKER_RESIDENCE, 1, 3) +
          basicNeedsInflux(island, WORKER_RESIDENCE, 12, 3) +
          basicNeedsInflux(island, WORKER_RESIDENCE, 19, 2) +
          publicServiceInflux(island, group, WORKER_RESIDENCE, PublicService.SCHOOL, 2, 42)) * building.quantity)
      }
    )
  )),
  ARTISAN_RESIDENCE(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.SAUSAGES, tpsToTpm(0.000666667F)),
      BuildingTypeConsumption(Goods.BREAD, tpsToTpm(0.000606061F)),
      BuildingTypeConsumption(Goods.SOAP, tpsToTpm(0.000277778F)),
      BuildingTypeConsumption(Goods.BEER, tpsToTpm(0.000512821F), mapOf(), true),
      BuildingTypeConsumption(Goods.CANNED_FOOD, tpsToTpm(0.00017094F), mapOf(PopulationTier.ARTISAN to 1)),
      BuildingTypeConsumption(Goods.SEWING_MACHINES, tpsToTpm(0.00047619F), mapOf(PopulationTier.ARTISAN to 250)),
      BuildingTypeConsumption(Goods.RUM, tpsToTpm(0.000952381F), mapOf(PopulationTier.ARTISAN to 500), true),
      BuildingTypeConsumption(Goods.FUR_COATS, tpsToTpm(0.000444444F), mapOf(PopulationTier.ARTISAN to 900))
    ),
    population = BuildingTypePopulation(
      contribution = { island, group, building ->
        mapOf(PopulationTier.ARTISAN to (
          basicNeedsInflux(island, ARTISAN_RESIDENCE, 0, 16) + // sausages, bread, soap
          publicServiceInflux(island, group, ARTISAN_RESIDENCE, PublicService.SCHOOL, 4) +
          basicNeedsInflux(island, ARTISAN_RESIDENCE, 1, 4) + // canned food
          basicNeedsInflux(island, ARTISAN_RESIDENCE, 11, 2) + // sewing machines
          basicNeedsInflux(island, ARTISAN_RESIDENCE, 35, 2) + // fur coats
          publicServiceInflux(island, group, ARTISAN_RESIDENCE, PublicService.UNIVERSITY, 2, 54)) * building.quantity)
      }
    )
  )),
  ENGINEER_RESIDENCE(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.CANNED_FOOD, tpsToTpm(0.00034188F)),
      BuildingTypeConsumption(Goods.SEWING_MACHINES, tpsToTpm(0.000952381F)),
      BuildingTypeConsumption(Goods.FUR_COATS, tpsToTpm(0.000888889F)),
      BuildingTypeConsumption(Goods.RUM, tpsToTpm(0.001904762F), mapOf(), true),
      BuildingTypeConsumption(Goods.GLASSES, tpsToTpm(0.000148148F), mapOf(PopulationTier.ENGINEER to 1)),
      BuildingTypeConsumption(Goods.PENNY_FARTHINGS, tpsToTpm(0.000416667F), mapOf(PopulationTier.ENGINEER to 500), true),
      BuildingTypeConsumption(Goods.COFFEE, tpsToTpm(0.000784314F), mapOf(PopulationTier.ENGINEER to 1000)),
      BuildingTypeConsumption(Goods.POCKET_WATCHES, tpsToTpm(0.000130719F), mapOf(PopulationTier.ENGINEER to 1000), true),
      BuildingTypeConsumption(Goods.LIGHT_BULBS, tpsToTpm(0.000208333F), mapOf(PopulationTier.ENGINEER to 1750))
    ),
    population = BuildingTypePopulation(
      contribution = { island, group, building ->
        mapOf(PopulationTier.ENGINEER to (
          basicNeedsInflux(island, ENGINEER_RESIDENCE, 0, 24) + // canned food, sewing machines, fur coats
          publicServiceInflux(island, group, ENGINEER_RESIDENCE, PublicService.UNIVERSITY, 6) +
          basicNeedsInflux(island, ENGINEER_RESIDENCE, 1, 4) + // glasses
          basicNeedsInflux(island, ENGINEER_RESIDENCE, 30, 2) + // coffee
          publicServiceInflux(island, group, ENGINEER_RESIDENCE, PublicService.ELECTRICITY, 2, 49) + // electricity
          basicNeedsInflux(island, ENGINEER_RESIDENCE, 49, 2)) * building.quantity) // light bulbs
      }
    )
  )),
  INVESTOR_RESIDENCE(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.GLASSES, tpsToTpm(0.000296296F)),
      BuildingTypeConsumption(Goods.COFFEE, tpsToTpm(0.001568627F)),
      BuildingTypeConsumption(Goods.LIGHT_BULBS, tpsToTpm(0.000416667F)),
      BuildingTypeConsumption(Goods.PENNY_FARTHINGS, tpsToTpm(0.000833333F), mapOf(), true),
      BuildingTypeConsumption(Goods.POCKET_WATCHES, tpsToTpm(0.000261438F), mapOf(), true),
      BuildingTypeConsumption(Goods.CHAMPAGNE, tpsToTpm(0.000392F), mapOf(PopulationTier.INVESTOR to 1)),
      BuildingTypeConsumption(Goods.CIGARS, tpsToTpm(0.00037037F), mapOf(PopulationTier.INVESTOR to 750)),
      BuildingTypeConsumption(Goods.CHOCOLATE, tpsToTpm(0.000888889F), mapOf(PopulationTier.INVESTOR to 1750)),
      BuildingTypeConsumption(Goods.JEWELLERY, tpsToTpm(0.000350877F), mapOf(PopulationTier.INVESTOR to 1750), true),
      BuildingTypeConsumption(Goods.GRAMOPHONES, tpsToTpm(0.0000877F), mapOf(PopulationTier.INVESTOR to 3000), true),
      BuildingTypeConsumption(Goods.STEAM_CARRIAGES, tpsToTpm(0.000111111F), mapOf(PopulationTier.INVESTOR to 5000))
    ),
    population = BuildingTypePopulation(
      contribution = { island, group, building ->
        mapOf(PopulationTier.INVESTOR to (
          basicNeedsInflux(island, INVESTOR_RESIDENCE, 0, 32) + // glasses, coffee, light bulbs
          publicServiceInflux(island, group, INVESTOR_RESIDENCE, PublicService.ELECTRICITY, 8) +
          basicNeedsInflux(island, INVESTOR_RESIDENCE, 1, 2) + // champagne
          basicNeedsInflux(island, INVESTOR_RESIDENCE, 18, 2) + // cigars
          basicNeedsInflux(island, INVESTOR_RESIDENCE, 40, 2) + // chocolate
          basicNeedsInflux(island, INVESTOR_RESIDENCE, 109, 4)) * building.quantity) // steam carriages
      }
    )
  )),
  JORNALERO_RESIDENCE,
  OBRERA_RESIDENCE,
  EXPLORER_SHELTER,
  TECHNICIAN_SHELTER,

  // Public Service types
  MARKETPLACE,
  PUB,
  FIRE_STATION,

  // Production Building types
  FISHERY(BuildingTypeProfile(
    production = listOf(
      BuildingTypeProduction(Goods.FISH, 2F)
    )
  )),
  LUMBERJACK,
  SAWMILL(BuildingTypeProfile(
    consumption = listOf(
      BuildingTypeConsumption(Goods.WOOD, 4F)
    ),
    production = listOf(
      BuildingTypeProduction(Goods.TIMBER, 4F)
    )
  )),
  SHEEP_FARM,
  POTATO_FARM,
  FRAMEWORK_KNITTERS,
  SCHNAPPS_DISTILLERY;

  companion object {
    fun publicServiceInflux(island: GamePhaseIsland, group: GamePhaseIslandBuildingGroup, type: BuildingType, service: PublicService, i: Int, minResidenceCount: Int = 0): Int =
      if (island.countBuildings(type) >= minResidenceCount) (i * group.publicServices.getOrElse(service) { 0F }).roundToInt()
      else 0

    fun basicNeedsInflux(island: GamePhaseIsland, type: BuildingType, minResidenceCount: Int, i: Int): Int =
      if (island.meetBasicNeeds && island.countBuildings(type) >= minResidenceCount) i
      else 0
  }
}

/*
All buildings have a profile that consists of the following:
1. Consumption: they may consume any number of different types of goods. For each type consumed, it
   does so at a particular rate, specified in tonnes per minute (TPM). In the case of residences,
   this consumption may only apply if a population threshold is met or luxury needs are being met
   on a given island

2. Production: they may produce any number of different types of goods. For each type produced, it
   does so at a particular rate, specified in TPM.

3. Population: they may contribute to the overall population of the island or draw on its workforce.
   Where they contribute to the population, as in the case of residences, they may do so to a
   variable degree depending on the conditions that prevail upon a given island. They may only be
   available if certain population exists on at least 1 island within the game as a whole.

4. Economic: they may contribute to overall income via tax, the level of which might depend on
   local conditions, or they may draw from it in the form of a maintenance cost. They also have a
   construction cost and may require influence to build. Other "economic" factors include the area
   required and the negative or positive beauty of the building and/or its modules.
 */
data class BuildingTypeProfile(
  val consumption: List<BuildingTypeConsumption> = listOf(),
  val production: List<BuildingTypeProduction> = listOf(),
  val population: BuildingTypePopulation = BuildingTypePopulation(),
  val economics: BuildingTypeEconomics = BuildingTypeEconomics()
)

data class BuildingTypeConsumption(
  val goods: Goods,
  val rate: Float,
  val population: Map<PopulationTier, Int> = mapOf(),
  val luxuryNeed: Boolean = false
)

data class BuildingTypeProduction(
  val goods: Goods,
  val rate: Float
)

data class BuildingTypePopulation(
  val workforce: Map<PopulationTier, Int> = mapOf(),
  val unlockCondition: Map<PopulationTier, Int> = mapOf(),
  val contribution: (GamePhaseIsland, GamePhaseIslandBuildingGroup, GamePhaseIslandBuilding) -> Map<PopulationTier, Int> = { _, _, _ -> mapOf() }
)

data class BuildingTypeEconomics(
  val constructionCost: Int = 0,
  val constructionMaterials: Map<Goods, Int> = mapOf(),
  val area: Int = 0,
  val beauty: Int = 0,
  val maintenance: Int = 0,
  val influence: Pair<InfluenceCategory, Int> = Pair(InfluenceCategory.NONE, 0),
  val maxModules: Int = 0,
  val moduleCost: Int = 0,
  val moduleSize: Int = 0,
  val moduleInfluenceCost: Pair<InfluenceCategory, Int> = Pair(InfluenceCategory.NONE, 0)
)

/*
A building item is any specialist, piece of machinery etc that can be equipped in a Town Hall,
Trade Union, or Harbour Master's office. It can modify the profile of any number of buildings
that exist within the group to which it is applied in any way.

They may also themselves produce independently as part of that group; e.g. by outputting a good once
every N minutes.
 */
enum class BuildingItem {
  COSTUME_DESIGNER,
  DRAUGHTSWOMAN;

  fun modifyProfile(type: BuildingType, profile: BuildingTypeProfile): BuildingTypeProfile = type.profile

  fun influences(type: BuildingType): Boolean = false
}

enum class ShipType(
  val materials: Map<Goods, Int> = mapOf(),
  val influence: Int = 0,
  val maintenance: Int = 0,
  val constructionTime: Int = 0
) {
  SCHOONER(
    materials = mapOf(Goods.TIMBER to 20, Goods.SAILS to 10),
    influence = 1,
    maintenance = 15,
    constructionTime = 3
  ),
  GUNBOAT(
    materials = mapOf(Goods.TIMBER to 10, Goods.SAILS to 20, Goods.WEAPONS to 7),
    influence = 2,
    maintenance = 25,
    constructionTime = 3
  ),
  FRIGATE(
    materials = mapOf(Goods.TIMBER to 40, Goods.SAILS to 20, Goods.WEAPONS to 15),
    influence = 4,
    maintenance = 100,
    constructionTime = 6
  ),
  CLIPPER(
    materials = mapOf(Goods.TIMBER to 40, Goods.SAILS to 30),
    influence = 2,
    maintenance = 175,
    constructionTime = 6
  ),
  SHIP_OF_THE_LINE(
    materials = mapOf(Goods.TIMBER to 60, Goods.SAILS to 30, Goods.WEAPONS to 30),
    influence = 8,
    maintenance = 250,
    constructionTime = 8
  ),
  CARGO_SHIP(
    materials = mapOf(Goods.STEEL_BEAMS to 20, Goods.STEAM_MOTORS to 20),
    influence = 3,
    maintenance = 500,
    constructionTime = 9
  ),
  BATTLE_CRUISER(
    materials = mapOf(Goods.STEEL_BEAMS to 60, Goods.STEAM_MOTORS to 20, Goods.ADVANCED_WEAPONS to 25),
    influence = 12,
    maintenance = 850,
    constructionTime = 15
  ),
  MONITOR(
    materials = mapOf(Goods.STEEL_BEAMS to 40, Goods.STEAM_MOTORS to 10, Goods.ADVANCED_WEAPONS to 10),
    influence = 6,
    maintenance = 300,
    constructionTime = 12
  ),
  OIL_TANKER(
    materials = mapOf(Goods.STEEL_BEAMS to 20, Goods.STEAM_MOTORS to 20),
    influence = 5,
    maintenance = 500,
    constructionTime = 9
  ),
  AIRSHIP(
    materials = mapOf(Goods.TIMBER to 25, Goods.SAILS to 50, Goods.STEAM_MOTORS to 25, Goods.GAS to 40),
    influence = 4,
    maintenance = 1600,
    constructionTime = 15
  )
}
