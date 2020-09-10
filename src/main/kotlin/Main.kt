import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.any
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import de.phyrone.brig.wrapper.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import java.util.logging.LogManager

object Main {
	val LOGGER = LogManager.getLogManager().getLogger("Diplomacy")

	val WORLD: World = loadWorld()
}

fun main(args: Array<String>) {
	embeddedServer(Netty, 443) {
		install(ContentNegotiation) {
			json()
		}

		routing {
			static("diplomat") {
				resource("diplomat.html")
				resource("diplomat.css")
				resource("diplomat.js")
				resource("to_words.js")
			}

			get("/world") {
				call.respond(Json.encodeToJsonElement(Main.WORLD))
			}

			get("/nations") {
				call.respond(Json.encodeToJsonElement(Main.WORLD.getNations()))
			}

			get("/provinces") {
				call.respond(Json.encodeToJsonElement(Main.WORLD.getProvinces()))
			}

			get("/groups") {
				call.respond(Json.encodeToJsonElement(Main.WORLD.getGroups()))
			}

			get("/orders") {
				call.respond(Json.encodeToJsonElement(Main.WORLD.getOrders()))
			}

			get("/trades") {
				call.respond(Json.encodeToJsonElement(Main.WORLD.getTrades()))
			}

			post("/nations") {
				call.receive<Nation>().also { nation ->
					getWorld().addNation(nation).fold({
						call.respond(HttpStatusCode.Accepted)
					}, { warn ->
						call.respond(HttpStatusCode.Conflict, warn)
					})
				}
			}

			post("/provinces") {
				call.receive<Province>().also { province ->
					getWorld().addProvince(province).fold({
						call.respond(HttpStatusCode.Accepted)
					}, { warn ->
						call.respond(HttpStatusCode.Conflict, warn)
					})
				}
			}

			post("/groups") {
				call.receive<Group>().also { group ->
					getWorld().addGroup(group).fold({
						call.respond(HttpStatusCode.Accepted)
					}, { warn ->
						call.respond(HttpStatusCode.Conflict, warn)
					})
				}
			}

			post("/orders") {
				call.receive<Order>().also { order ->
					getWorld().addOrder(order).fold({
						call.respond(HttpStatusCode.Accepted)
					}, { warn ->
						call.respond(HttpStatusCode.Conflict, warn)
					})
				}
			}

			post("/trades") {
				call.receive<Trade>().also { trade ->
					getWorld().addTrade(trade).fold({
						call.respond(HttpStatusCode.Accepted)
					}, { warn ->
						call.respond(HttpStatusCode.Conflict, warn)
					})
				}
			}
		}
	}.start()

	runBlocking {
		val token = System.getenv("DIPLOMAT_DISCORD_TOKEN")

		val client = Kord(token)

		client.on<MessageCreateEvent> {
			if (message.author != null && message.author!!.id != client.selfId) {
				val dispatcher = CommandDispatcher<Unit>()

				println("Replying to: [${message.content}].")

				dispatcher.run {
					literal("diplomat") {
						literal("get") {
							literal("world") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy"
											description = "The statistics of this world."

											field("Nations") {
												"${getWorld().getNations().size}"
											}

											field("Provinces") {
												"${getWorld().getProvinces().size}"
											}

											field("Groups") {
												"${getWorld().getGroups().size}"
											}

											field("Orders") {
												"${getWorld().getOrders().size}"
											}
										}
									}

									return@executes 0
								}
							}

							literal("nations") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy"
											description = "The nations of this world."

											field("Nations") {
												if (getWorld().getNations().isEmpty()) {
													"No nations in this world!"
												} else {
													getWorld().getNations().joinToString("\n") {
														"• [**${it.name}**, ${it.code}], <@${it.owner}>"
													}
												}
											}
										}
									}

									return@executes 0
								}
							}

							literal("provinces") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy: Provinces"
											description = "The provinces of this world."

											field("Provinces") {
												if (getWorld().getProvinces().isEmpty()) {
													"No provinces in this world!"
												} else {
													getWorld().getProvinces().sortedBy { it.owner.name }.groupBy { it.owner.name }.map { province ->
														"• [**${province.key}**] ${province.value.joinToString(", ") { it.name }}"
													}.joinToString("\n")
												}

											}
										}
									}

									return@executes 0
								}
							}

							literal("groups") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy"
											description = "The groups of this world."

											field("Groups") {
												if (getWorld().getGroups().isEmpty()) {
													"No groups in this world!"
												} else {
													getWorld().getGroups().sortedBy { it.owner.name }.joinToString("\n") {
														"• [**${it.location.nation.name}**, **${it.location.province.name}**], ${it.owner.name}, ${it.type}"
													}
												}
											}
										}
									}

									return@executes 0
								}
							}

							literal("orders") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy"
											description = "The orders of this world."

											field("Orders") {
												if (getWorld().getOrders().isEmpty()) {
													"No orders in this world!"
												} else {
													getWorld().getOrders().sortedBy { it.owner.name }.joinToString("\n") {
														if (it.target == Location.NONE) {
															"• [**${it.type}**], ${it.location.province.name}, ${it.location.nation.name}"
														} else {
															"• [**${it.type}**], [${it.location.province.name}, ${it.location.nation.name}] to [${it.target.nation.name}, ${it.target.province.name}]"
														}
													}
												}
											}
										}
									}

									return@executes 0
								}
							}

							literal("provinces") {
								executes {
									launch {
										message.channel.createEmbed {
											title = "Diplomacy"
											description = "The trades of this world."

											field("Trades") {
												if (getWorld().getOrders().isEmpty()) {
													"No trades in this world!"
												} else {
													getWorld().getTrades().sortedBy { it.from.nation.name }.joinToString("\n") {
														"• [**${it.from.nation.name}**, **${it.from.province.name}**] to [**${it.to.nation.name}**, **${it.to.province.name}**"
													}
												}
											}
										}
									}

									return@executes 0
								}
							}
						}

						literal("add") {
							literal("nation") {
								argument("name", StringArgument) {
									argument("code", StringArgument) {
										argument("owner", LongArgument) {
											executes {
												message.getAuthorAsMember()?.also { member ->
													if (member.getPermissions().contains(Permission.Administrator) || member.roles.any { it.name == "Diplomacy Master" }) {
														val name = it.getArgument<String>("name")
														val code = it.getArgument<String>("code")
														val owner = it.getArgument<Long>("owner")

														launch {
															val nation = Nation(name, code, owner)

															getWorld().addNation(nation).fold({
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Successfully added a nation to this world."

																	field("Details") {
																		"""
																		• Name: ${name}
																		• Code: ${code}
																		• Owner: <@${owner}>
																	""".trimIndent()
																	}
																}

																saveWorld()
															}, { warn ->
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add a nation to this world."

																	field("Reason") {
																		warn
																	}

																	field("Details") {
																		"""
																		• Name: ${name}
																		• Code: ${code}
																		• Owner: <@${owner}>
																	""".trimIndent()
																	}
																}
															})
														}
													} else {
														launch {
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "You do not have permission to add a nation."
															}
														}
													}
												}

												return@executes 0
											}
										}
									}
								}
							}

							literal("province") {
								argument("owner", NationArgument) {
									argument("name", StringArgument) {
										argument("type", ProvinceTypeArgument) {
											argument("center", BoolArgument) {
												executes {
													val owner = it.getArgument<Nation>("owner")
													val name = it.getArgument<String>("name")
													val type = it.getArgument<Province.Type>("type")
													val center = it.getArgument<Boolean>("center")

													val province = Province(owner, name, type, center)

													getWorld().addProvince(province).fold({
														message.channel.createEmbed {
															title = "Diplomacy"
															description = "Successfully added a province to this world."

															field("Details") {
																"""
                                                            	• Owner: ${owner.name}
                                                            	• Name: ${name}
                                                            	• Type: ${type}
                                                            	• Center: ${if (center) "Yes" else "No"}
                                                            """.trimIndent()
															}
														}

														saveWorld()
													}, { warn ->
														message.channel.createEmbed {
															title = "Diplomacy"
															description = "Failed to add a province to this world."

															field("Reason") {
																warn
															}

															field("Details") {
																"""
                                                            	• Owner: ${owner.name}
                                                            	• Name: ${name}
                                                            	• Type: ${type}
                                                            	• Center: ${if (center) "Yes" else "No"}
                                                            """.trimIndent()
															}
														}
													})

													return@executes 0
												}
											}
										}
									}
								}

							}

							literal("group") {
								argument("owner", NationArgument) {
									argument("nation", NationArgument) {
										argument("province", ProvinceArgument) {
											argument("type", GroupTypeArgument) {
												executes {
													val owner = it.getArgument<Nation>("owner")
													val nation = it.getArgument<Nation>("nation")
													val province = it.getArgument<Province>("province")
													val type = it.getArgument<Group.Type>("type")

													val group = Group(owner, Location(nation, province), type)

													getWorld().addGroup(group).fold({
														message.channel.createEmbed {
															title = "Diplomacy"
															description = "Successfully added a group to this world."

															field("Details") {
																"""
                                                            	• Owner: ${owner.name}
                                                            	• Nation: ${nation.name}
                                                            	• Province: ${province.name}
                                                            	• Type: ${type}
                                                            """.trimIndent()
															}

															saveWorld()
														}
													}, { warn ->
														launch {
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "Failed to add a group to this world."
																field("Reason") {
																	warn
																}
																field("Details") {
																	"""
                                                                      	• Owner: ${owner.name}
                                                                      	• Nation: ${nation.name}
                                                                      	• Province: ${province.name}
                                                                      	• Type: ${type}
                                                                    """.trimIndent()
																}
															}
														}
													})

													return@executes 0
												}
											}
										}
									}
								}
							}

							literal("order") {
								argument("owner", NationArgument) {
									argument("nation", NationArgument) {
										argument("province", ProvinceArgument) {
											argument("order_type", OrderTypeArgument) {
												argument("group_type", GroupTypeArgument) {
													executes {
														val owner = it.getArgument<Nation>("owner")
														val nation = it.getArgument<Nation>("nation")
														val province = it.getArgument<Province>("province")
														val orderType = it.getArgument<Order.Type>("order_type")
														val groupType = it.getArgument<Group.Type>("group_type")

														val order = Order(owner, Location(nation, province), Location.NONE, orderType, groupType)

														getWorld().addOrder(order).fold({
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "Successfully added an order to this world."

																field("Details") {
																	"""
                                                                	• Owner: ${owner.name}
                                                                	• Nation: ${nation.name}
                                                                	• Province: ${province.name}
                                                                	• Order type: ${orderType}
																	• Group type: ${groupType}
                                                                """.trimIndent()
																}
															}

															saveWorld()
														}, { warn ->
															launch {
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add an order to this world."

																	field("Reason") {
																		warn
																	}

																	field("Details") {
																		"""
                                                                    	• Owner: ${owner.name}
                                                                    	• Nation: ${nation.name}
                                                                    	• Province: ${province.name}
                                                                    	• Order type: ${orderType}
																		• Group type: ${groupType}
                                                                    """.trimIndent()
																	}
																}
															}
														})

														return@executes 0
													}
												}
											}

											argument("group_type", StringArgument) {
												argument("target_nation", NationArgument) {
													argument("target_province", ProvinceArgument) {
														argument("order_type", StringArgument) {
															executes {
																val owner = it.getArgument<Nation>("owner")
																val nation = it.getArgument<Nation>("nation")
																val province = it.getArgument<Province>("province")
																val orderType = it.getArgument<Order.Type>("order_type")
																val groupType = it.getArgument<Group.Type>("group_type")
																val targetNation = it.getArgument<Nation>("target_nation")
																val targetProvince = it.getArgument<Province>("target_province")

																val order = Order(owner, Location(nation, province), Location(targetNation, targetProvince), orderType, groupType)

																getWorld().addOrder(order).fold({
																	message.channel.createEmbed {
																		title = "Diplomacy"
																		description = "Successfully added an order to this world."

																		field("Details") {
																			"""
                                                                   			• Owner: ${owner.name}
                                                                   			• Nation: ${nation.name}
                                                                   			• Province: ${province.name}
                                                                   			• Order type: ${orderType}
                                                                   			• Group type: ${groupType}
																   			• Target nation: ${targetNation.name}
																   			• Target province: ${targetProvince.name}
                                                                        """.trimIndent()
																		}
																	}

																	saveWorld()
																}, { warn ->
																	launch {
																		message.channel.createEmbed {
																			title = "Diplomacy"
																			description = "Failed to add an order to this world."

																			field("Reason") {
																				warn
																			}

																			field("Details") {
																				"""
                                                                   				• Owner: ${owner.name}
                                                                   				• Nation: ${nation.name}
                                                                   				• Province: ${province.name}
                                                                   				• Order type: ${orderType}
                                                                   				• Group type: ${groupType}
																   				• Target nation: ${targetNation.name}
																   				• Target province: ${targetProvince.name}
                                                                			""".trimIndent()
																			}
																		}
																	}
																})
																return@executes 0
															}
														}
													}
												}
											}
										}
									}
								}
							}

							literal("trade") {
								argument("origin_nation", NationArgument) {
									argument("origin_province", ProvinceArgument) {
										argument("target_nation", NationArgument) {
											argument("target_province", ProvinceArgument) {
												executes {
													val originNation = it.getArgument<Nation>("origin_nation")
													val originProvince = it.getArgument<Province>("origin_province")

													val targetNation = it.getArgument<Nation>("target_nation")
													val targetProvince = it.getArgument<Province>("target_province")

													val trade = Trade(Location(originNation, originProvince), Location(targetNation, targetProvince))

													getWorld().addTrade(trade).fold({
														message.channel.createEmbed {
															title = "Diplomacy"
															description = "Successfully added a trade to this world."

															field("Details") {
																"""
                                                            	• Nation from: ${originNation.name}
                                                            	• Province from: ${originProvince.name}
                                                            	• Nation to: ${targetNation.name}
                                                            	• Province to: ${targetProvince.name}
                                                            """.trimIndent()
															}
														}

														saveWorld()
													}, { warn ->
														launch {
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "Failed to add a trade to this world."

																field("Reason") {
																	warn
																}

																field("Details") {
																	"""
                                                                	• Nation from: ${originNation.name}
                                                                	• Province from: ${originProvince.name}
                                                                	• Nation to: ${targetNation.name}
                                                                	• Province to: ${targetProvince.name}
                                                                """.trimIndent()
																}
															}
														}
													})

													return@executes 0
												}
											}
										}
									}
								}
							}
						}

						literal("turn") {
							argument("phase", StringArgument) {
								executes {
									val phase = it.getArgument<String>("phase")

									when (phase) {
										"draft" -> {
											message.channel.createEmbed {
												title = "Diplomacy"
												description = "Rulers of this world, it is time to draft your orders!"
											}
										}

										"execute" -> {
											message.channel.createEmbed {
												title = "Diplomacy"
												description = "Rulers of this world, your orders will now be executed."
											}

											val world = getWorld()

											message.channel.createEmbed {
												title = "Diplomacy"
												description = "Executing ${Order.Type.Hold} orders."
											}

											world.getOrders().filter { it.type == Order.Type.Hold }.forEach {
												message.channel.createEmbed {
													field("Details") {
														"""
                                                    	• Owner: ${it.owner.name}
                                                    	• Nation: ${it.location.nation.name}
                                                    	• Province: ${it.location.province.name}
                                                    """.trimIndent()
													}
												}

												if (it.owner != it.location.nation) {
													message.channel.createEmbed {
														title = "Execution failure!"
														description = "${it.owner.name} attempted to hold troops in ${it.location.nation}'s territory!"
													}
													return@forEach
												} else {
													message.channel.createEmbed {
														title = "Execution success!"
														description = "${it.owner.name} held troops in itss territory!"
													}
												}
											}

											message.channel.createEmbed {
												title = "Diplomacy"
												description = "Executing ${Order.Type.Move} orders."
											}

											world.getOrders().filter { it.type == Order.Type.Move }.forEach {
												message.channel.createEmbed {
													field("Execution details") {
														"""
                                                    	• Owner: ${it.owner.name}
                                                    	• Nation: ${it.location.nation.name}
                                                    	• Province: ${it.location.province.name}
														• Target nation: ${it.target.nation.name}
														• Target province: ${it.target.province.name}
                                                    """.trimIndent()
													}
												}

												if (it.owner != it.location.nation) {
													message.channel.createEmbed {
														title = "Execution failure"
														description = "${it.owner.name} attempted to move a group in anotehr nation's territory!"
													}
													return@forEach
												} else {
													val order = it
													val groupOption = world.getGroup(it.location, it.groupType)

													groupOption.fold({
														message.channel.createEmbed {
															title = "Execution failure"
															description = "${it.owner.name} attempted to move a group that does not exist!"
														}
													}, {
														val existing = world.getGroups().filter { it.location == order.target }
														var required = existing.count()
														world.getOrders().filter {
															it.type == Order.Type.Support
														}.forEach {
															if (it.target == order.target) {
																required += 1
															}
															if (it.target == order.target) {
																required -= 1
															}
														}

														if (required <= 0) {
															message.channel.createEmbed {
																title = "Execution success"
																description = "${it.owner.name} moved a group to another province with a sweeping victory!"
															}

															existing.forEach {
																world.removeGroup(it)
															}
														} else {
															message.channel.createEmbed {
																title = "Execution success"
																description = "${it.owner.name} failed to move a group to another province!"
															}
														}

														world.removeOrder(order)
													})
												}
											}
										}

										"retreat" -> {

										}
										"disband" -> {

										}

									}

									return@executes 0
								}
							}
						}
					}
				}

				if (message.content.startsWith("diplomat")) {
					try {
						dispatcher.execute(message.content, Unit)
					} catch (exception: CommandSyntaxException) {
						message.channel.createEmbed {
							title = "Invalid syntax"
							description = exception.message
						}
					}
				}
			}
		}

		client.login()
	}
}

fun loadWorld() = File("world.json").let {
	if (it.exists()) {
		it.inputStream().bufferedReader().use {
			Json.decodeFromString(it.readText())
		}
	} else {
		World()
	}
}

fun saveWorld() = File("world.json").also {
	if (it.exists() || it.createNewFile()) {
		it.outputStream().bufferedWriter().use {
			it.write(Json.encodeToString(Main.WORLD))
		}
	}
}

fun getWorld() = Main.WORLD