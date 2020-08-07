import com.benasher44.uuid.uuid4

/**
 * Top-level container for all configuration relating to a specific game.
 */
data class Game(
  val name: String,
  val phases: List<GamePhase> = listOf()
) {
  val id: String = uuid4().toString()
}

/**
 * Representation of a particular "phase" in the game; e.g. "starting out" with 1 home island and
 * a few farmers, "first workers" etc.
 *
 * The idea here is that we should be able to produce and report on a complete development
 * strategy that sees population tiers upgraded, buildings unlocked, islands added and so forth.
 *
 * Additionally, by modelling distinct phases, it becomes possible to report on things like
 * construction material and time costs (which are effectively a diff between a phase and that which
 * precedes it).k
 */
data class GamePhase(
  val name: String,
  val islands: List<GamePhaseIsland> = listOf(),
  val tradeRoutes: List<GamePhaseTradeRoute> = listOf()
) {
  val id: String = uuid4().toString()
}

/**
 * Representation of a given island belonging to the user at a particular point in the game. It is
 * the main basis for consumption and production calculations.
 *
 * Additionally, you specify buildings that either exist or are proposed for the island. These are
 * arranged into groups for the purpose of specifying the influence or otherwise of items and public
 * services upon them.
 *
 * Specifying buildings types other than residences is optional but makes calculations of things
 * like construction costs possible. They are ignored when calculating solutions for production
 * which aim to satisfy the population present in residences within the terms of the given
 * calculation parameters. However, the output of production buildings is taken into account when
 * calculating the impact of trade routes. Therefore, they do indirectly impact the solution
 * calculations.
 */
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

/**
 * A set of constraints to use when calculating an optimal solution for a given building type.
 */
data class GamePhaseIslandItemCalculations(
  val type: BuildingType,
  val items: List<BuildingItem>,
  val electricity: Boolean
) {
  val id: String = uuid4().toString()
}

/**
 * A group of buildings on an island at a particular point in the game that may be under the
 * influence of any number of items or public services.
 */
data class GamePhaseIslandBuildingGroup(
  val name: String,
  val publicServices: Map<PublicService, Float> = mapOf(),
  val items: List<BuildingItem> = listOf(),
  val buildings: List<GamePhaseIslandBuilding> = listOf()
) {
  val id: String = uuid4().toString()
}

/**
 * A type of building that exists within a group in a particular quantity.
 */
data class GamePhaseIslandBuilding(
  val type: BuildingType,
  val quantity: Int,
  val productivityAdjustment: Int = 0
) {
  val id: String = uuid4().toString()
}

/**
 * An abstract representation of an exchange of goods between two or more islands that is in
 * operation at a particular point in the game. These are used when calculating production
 * solutions. The calculator reports will not attempt to satisfy needs on an island that are
 * already being met by trade routes.
 */
data class GamePhaseTradeRoute(
  val name: String,
  val exporters: List<GamePhaseIsland>,
  val importers: List<GamePhaseIsland>,
  val goods: List<Goods>,
  val ships: Map<ShipType, Int> = mapOf()
) {
  val id: String = uuid4().toString()

  fun balanceOfTrade(
    island: GamePhaseIsland,
    preTradeBalances: Map<GamePhaseIsland, Map<Goods, Float>>
  ): Map<Goods, Float> = TODO()
}

/**
 * Trade route strategies are an abstraction to facilitate calculation of their impact on the
 * economic situation within the game. They are not intended to be a completely accurate
 * representation of the actual trade routes you might have in a game in order to implement these
 * strategies.
 *
 * The reasoning for this is simple. In order to do estimates and so forth about goods and their
 * production and consumption, we always need to talk about them in a common unit (which is tonnes
 * per minute). But a ship which picks up 20 of a commodity from island A to deliver to island B
 * may be providing any amount of TPM, depending on the distance between islands A and B, the wind
 * conditions, how busy the harbour is etc.
 *
 * Currently, I have implemented 3 strategies, because these are the approaches I tend to use:
 *
 * 1. Excess: This is where all of the surplus that can be carried from the first island to the
 *            second island are carried there. The amount available is assumed to be the total
 *            excess of any specified good available at the first island and it is added to the
 *            second island. Any subsequent islands get nothing. Consequently, if you want to model
 *            a game scenario where you have a trade route that picks up from island A, drops off
 *            the goods at island B, then picks up another type of goods to deliver to island C, you
 *            will need to define 2 or more of these "excess" trade route strategies as being in
 *            operation. When I use this strategy in the game, I apply a minimum stock level at the
 *            pick-up island (A) and sell the commodity from island B (because that is where any
 *            over-production accumulates).
 *
 * 2. Share:  This is where the total excess of the specified goods from all of the islands in the
 *            trade route chain is divided evenly between them. Personally, I use this quite a lot
 *            and achieve it by setting a min stock level for the good on each island. Then I both
 *            load and unload a full ship load of a good at each island. Because the ship will not
 *            take more that minimum stock levels permit, you end up with minimum stock being
 *            maintained across all the islands so long as there is sufficient production. One
 *            island in the chain is usually a production centre and over-production will accumulate
 *            there. If you set the sell threshold to >= 1 ship load above min stock, then you can
 *            also safely sell the commodity from the production centre island. The other islands
 *            will always be just a little below the minimum stock level; how much depends on how
 *            many ships you have running the route and the distance between the islands. Influence
 *            permitting, I like to use 1 ship per island in the chain in a staggered rotation.
 *
 * 3. Excess then share: this is where the excess is taken from the first island and then shared
 *            between the rest of the islands in the chain. I tend to use this for things like rum,
 *            coffee, and cigars in the New World and (vice-versa) beer and sewing machines in the
 *            Old World. In reality, I implement it as 2 or more trade routes: 1 using the excess
 *            strategy (to amass rum at a port) and 1 using the share strategy (to divvy it up
 *            among the other islands that want it). I model the "combined" strategy here so that
 *            I can easily perform the accounting as a single function.
 *
 * Note that none of these strategies will accurately calculate the economic impact of any in-game
 * trade route you may have where you drop off specific quantities of a commodity at an island or
 * purchase specific quantities from a neutral trader. They are too difficult to work out (for me
 * at least!) For example, even if you knew the production level of a neutral trader in TPM (and,
 * therefore, how much surplus they had), their stock is available to all players, so contended.
 * You may turn up at their harbour just after another player and get nothing or you may get 150.
 *
 * In theory, that kind of stuff could be given very approximate estimates if the user told us about
 * factors such as the distance between islands and the number of other players in the game. But the
 * value in doing that would be minimal, IMO. Whereas understanding the impact of the trade routes
 * you do have 100% control over is extremely useful.
 *
 * Also note that the strategies take no account of ship capacity. I assume that you provide enough
 * to carry all the excess/share resources. Ships are configured on a trade route to contribute to
 * calculations concerning costs, including maintenance, influence, and construction materials/time.
 *
 * Another consequence of the assumption that all the excess of a given commodity is taken by a
 * trade route is that they DO NOT support having more than one route taking a particular resource
 * from an island. Again, this is for reasons of time and distance etc. Whatever strategy you are
 * using, any given island should ONLY EVER be involved in 1 trade route that deals with a particular
 * commodity. If you break that rule, you will get inaccurate results. This is because an excess
 * will be getting allocated multiple times when it only exists once.
 */
enum class GamePhaseIslandTradeRouteStrategy {
  EXCESS,
  SHARE,
  EXCESS_THEN_SHARE;

  // using the balances of the islands, return the amounts deposited into or withdrawn from the
  // given trade route for each island
  fun islandGoods(
    route: GamePhaseTradeRoute,
    balances: Map<GamePhaseIsland, Map<Goods, Float>>
  ): Map<GamePhaseIsland, Map<Goods, Float>> {
    // find the exporters of goods
//    val exporters = route.goods.filter {
//      // ignore routes that do not have any islands configured
//      it.value.isEmpty()
//    }.map {
//      // TODO extract this to strategy to override how we identify exporters
//      it.key to listOf(it.value.first())
//    }.toMap()
//    // find the importers of goods
//    val importers = route.goods.filter {
//      // ignore any routes that do not have at least 1 destination island
//      it.value.size < 2
//    }.map {
//      // TODO extract this to strategy to override how we identify importers
//      it.key to listOf(it.value[1])
//    }.toMap()
//    val exportedGoods = exporters.map { exporter ->
//
//    }
    // now
    TODO()
  }
}
