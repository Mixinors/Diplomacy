import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.any
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import com.mojang.brigadier.CommandDispatcher
import de.phyrone.brig.wrapper.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.LoggerFactory
import java.io.File

object Main {
	val LOGGER = LoggerFactory.getLogger("Diplomacy")

	val WORLD: World = loadWorld()
}

fun main(args: Array<String>) {
	embeddedServer(Netty, 443) {
		install(ContentNegotiation) {
			json()
		}

		routing {
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
		}
	}.start()

	GlobalScope.launch {
		val token = System.getenv("DIPLOMAT_DISCORD_TOKEN")

		val client = Kord(token)

		client.on<MessageCreateEvent> {
			if (message.author != null && message.author!!.id != client.selfId) {
				val dispatcher = CommandDispatcher<Unit>()

				Main.LOGGER.info("Replying to: [${message.content}].")

				dispatcher.run {
					literal("get") {
						literal("world") {
							executes {
								GlobalScope.launch {
									message.channel.createEmbed {
										title = "Diplomacy"
										description = "The statistics of the current Diplomacy world."

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
								GlobalScope.launch {
									message.channel.createEmbed {
										title = "Diplomacy"
										description = "The nations of the current Diplomacy world."

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
								GlobalScope.launch {
									message.channel.createEmbed {
										title = "Diplomacy: Provinces"
										description = "The provinces of the current Diplomacy world."

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
								GlobalScope.launch {
									message.channel.createEmbed {
										title = "Diplomacy"
										description = "The groups of the current Diplomacy world."

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

													GlobalScope.launch {
														val nation = Nation(name, code, owner)

														getWorld().addNation(nation).fold({
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "Successfully added a nation to the Diplomacy world."

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
																description = "Failed to add a nation to the Diplomacy world."

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
													GlobalScope.launch {
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
							argument("owner", StringArgument) {
								argument("name", StringArgument) {
									argument("type", StringArgument) {
										argument("center", BoolArgument) {
											executes {
												message.getAuthorAsMember()?.also { member ->
													if (member.getPermissions().contains(Permission.Administrator) || member.roles.any { it.name == "Diplomacy Master" }) {
														val ownerArgument = it.getArgument<String>("owner")
														val nameArgument = it.getArgument<String>("name")
														val typeArgument = it.getArgument<String>("type")
														val centerArgument = it.getArgument<Boolean>("center")

														GlobalScope.launch {
															getWorld().getNation(ownerArgument).fold({
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add a nation to the Diplomacy world."

																	field("Reason") {
																		"Nation with the given name not found in the Diplomacy world!"
																	}
																}
															}, { nation ->
																val province = Province(nation, nameArgument, Province.Type.valueOf(typeArgument), centerArgument)

																getWorld().addProvince(province).fold({
																	message.channel.createEmbed {
																		title = "Diplomacy"
																		description = "Successfully added a province to the Diplomacy world."

																		field("Details") {
																			"""
                                                                            	• Owner: ${ownerArgument}
                                                                            	• Name: ${nameArgument}
                                                                            	• Type: ${typeArgument}
                                                                            	• Center: ${centerArgument}
                                                                            """.trimIndent()
																		}
																	}

																	saveWorld()
																}, { warn ->
																	message.channel.createEmbed {
																		title = "Diplomacy"
																		description = "Failed to add a province to the Diplomacy world."

																		field("Reason") {
																			warn
																		}

																		field("Details") {
																			"""
                                                                            	• Owner: ${ownerArgument}
                                                                            	• Name: ${nameArgument}
                                                                            	• Type: ${typeArgument}
                                                                            	• Center: ${centerArgument}
                                                                            """.trimIndent()
																		}
																	}
																})
															})
														}
													} else {
														GlobalScope.launch {
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "You do not have permission to add a province."
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

						}

						literal("group") {
							argument("owner", StringArgument) {
								argument("nation", StringArgument) {
									argument("province", StringArgument) {
										argument("type", StringArgument) {
											executes {
												message.getAuthorAsMember()?.also { member ->
													if (member.getPermissions().contains(Permission.Administrator) || member.roles.any { it.name == "Diplomacy Master" }) {
														val ownerArgument = it.getArgument<String>("owner")
														val nationArgument = it.getArgument<String>("nation")
														val provinceArgument = it.getArgument<String>("province")
														val typeArgument = it.getArgument<String>("type")

														val ownerOption = getWorld().getNation(ownerArgument)
														val nationOption = getWorld().getNation(nationArgument)
														val provinceOption = getWorld().getProvince(provinceArgument)

														ownerOption.fold({
															GlobalScope.launch {
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add a group to the Diplomacy world."

																	field("Reason") {
																		"Owner nation not found in the Diplomacy world!"
																	}

																	field("Details") {
																		"""
                                                                           	• Owner: ${ownerArgument}
                                                                           	• Nation: ${nationArgument}
                                                                           	• Province: ${provinceArgument}
                                                                           	• Type: ${typeArgument}
                                                                        """.trimIndent()
																	}
																}
															}
														}, { owner ->
															nationOption.fold({
																GlobalScope.launch {
																	message.channel.createEmbed {
																		title = "Diplomacy"
																		description = "Failed to add a group to the Diplomacy world."

																		field("Reason") {
																			"Nation not found in the Diplomacy world!"
																		}

																		field("Details") {
																			"""
                                                                            	• Owner: ${ownerArgument}
                                                                            	• Nation: ${nationArgument}
                                                                            	• Province: ${provinceArgument}
                                                                            	• Type: ${typeArgument}
                                                                            """.trimIndent()
																		}
																	}
																}
															}, { nation ->
																provinceOption.fold({
																	GlobalScope.launch {
																		message.channel.createEmbed {
																			title = "Diplomacy"
																			description = "Failed to add a group to the Diplomacy world."

																			field("Reason") {
																				"Province not found in the Diplomacy world!"
																			}

																			field("Details") {
																				"""
                                                                                    • Owner: ${ownerArgument}
                                                                                    • Nation: ${nationArgument}
                                                                                    • Province: ${provinceArgument}
                                                                                    • Type: ${typeArgument}
                                                                                """.trimIndent()
																			}
																		}
																	}
																}, { province ->
																	val group = Group(owner, Location(nation, province), Group.Type.valueOf(typeArgument))

																	getWorld().addGroup(group).fold({
																		message.channel.createEmbed {
																			title = "Diplomacy"
																			description = "Successfully added a group to the Diplomacy world."

																			field("Details") {
																				"""
                                                                                    • Owner: ${ownerArgument}
                                                                                    • Nation: ${nationArgument}
                                                                                    • Province: ${provinceArgument}
                                                                                    • Type: ${typeArgument}
                                                                                """.trimIndent()
																			}
																		}

																		saveWorld()
																	}, { warn ->
																		message.channel.createEmbed {
																			title = "Diplomacy"
																			description = "Failed to add a group to the Diplomacy world."

																			field("Reason") {
																				warn
																			}

																			field("Details") {
																				"""
                                                                                    • Owner: ${ownerArgument}
                                                                                    • Nation: ${nationArgument}
                                                                                    • Province: ${provinceArgument}
                                                                                    • Type: ${typeArgument}
                                                                                """.trimIndent()
																			}
																		}
																	})
																})
															})
														})
													} else {
														GlobalScope.launch {
															message.channel.createEmbed {
																title = "Diplomacy"
																description = "You do not have permission to add a group."
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
						}

						literal("order") {
							argument("owner", StringArgument) {
								argument("nation", StringArgument) {
									argument("province", StringArgument) {
										argument("type", StringArgument) {
											executes {
												val ownerArgument = it.getArgument<String>("owner")
												val nationArgument = it.getArgument<String>("nation")
												val provinceArgument = it.getArgument<String>("province")
												val typeArgument = it.getArgument<String>("type")

												val ownerOption = getWorld().getNation(ownerArgument)
												val nationOption = getWorld().getNation(nationArgument)
												val provinceOption = getWorld().getProvince(provinceArgument)

												val fail: (String) -> Unit = { reason ->
													GlobalScope.launch {
														message.channel.createEmbed {
															title = "Diplomacy"
															description = "Failed to add an order to the Diplomacy world."

															field("Reason") {
																reason
															}

															field("Details") {
																"""
                                                                   • Owner: ${ownerArgument}
                                                                   • Nation: ${nationArgument}
                                                                   • Province: ${provinceArgument}
                                                                   • Type: ${typeArgument}
                                                                """.trimIndent()
															}
														}
													}
												}

												ownerOption.fold({
													fail.invoke("Owner nation not found in the Diplomacy world!")
												}, { owner ->
													nationOption.fold({
														fail.invoke("Location nation not found in the Diplomacy world!")
													}, { nation ->
														provinceOption.fold({
															fail.invoke("Location province not found in the Diplomacy world!")
														}, { province ->
															val order = Order(owner, Location(nation, province), Location.NONE, Order.Type.valueOf(typeArgument))

															getWorld().addOrder(order).fold({
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Successfully added an order to the Diplomacy world."

																	field("Details") {
																		"""
                                                                        	• Owner: ${ownerArgument}
                                                                        	• Nation: ${nationArgument}
                                                                        	• Province: ${provinceArgument}
                                                                        	• Type: ${typeArgument}
                                                                        """.trimIndent()
																	}
																}

																saveWorld()
															}, { warn ->
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add an order to the Diplomacy world."

																	field("Reason") {
																		warn
																	}

																	field("Details") {
																		"""
                                                                        	• Owner: ${ownerArgument}
                                                                        	• Nation: ${nationArgument}
                                                                        	• Province: ${provinceArgument}
                                                                        	• Type: ${typeArgument}
                                                                        """.trimIndent()
																	}
																}
															})
														})
													})
												})

												return@executes 0
											}
										}

										argument("target_nation", StringArgument) {
											argument("province_nation", StringArgument) {
												argument("type", StringArgument) {
													executes {
														val ownerArgument = it.getArgument<String>("owner")
														val nationArgument = it.getArgument<String>("nation")
														val provinceArgument = it.getArgument<String>("province")
														val typeArgument = it.getArgument<String>("type")
														val targetNationArgument = it.getArgument<String>("target_nation")
														val targetProvinceArgument = it.getArgument<String>("province_nation")

														val ownerOption = getWorld().getNation(ownerArgument)
														val nationOption = getWorld().getNation(nationArgument)
														val provinceOption = getWorld().getProvince(provinceArgument)
														val targetNationOption = getWorld().getNation(targetNationArgument)
														val targetProvinceOption = getWorld().getProvince(targetProvinceArgument)

														val fail: (String) -> Unit = { reason ->
															GlobalScope.launch {
																message.channel.createEmbed {
																	title = "Diplomacy"
																	description = "Failed to add an order to the Diplomacy world."

																	field("Reason") {
																		reason
																	}

																	field("Details") {
																		"""
                                                                   			• Owner: ${ownerArgument}
                                                                   			• Nation: ${nationArgument}
                                                                   			• Province: ${provinceArgument}
                                                                   			• Type: ${typeArgument}
																   			• Target nation: ${targetNationArgument}
																   			• Target province: ${targetProvinceArgument}
                                                                		""".trimIndent()
																	}
																}
															}
														}

														ownerOption.fold({
															fail.invoke("Owner nation not found in the Diplomacy world!")
														}, { owner ->
															nationOption.fold({
																fail.invoke("Location nation not found in the Diplomacy world!")
															}, { nation ->
																provinceOption.fold({
																	fail.invoke("Location province not found in the Diplomacy world!")
																}, { province ->
																	targetNationOption.fold({
																		fail.invoke("Target nation not found in the Diplomacy world!")
																	}, { targetNation ->
																		targetProvinceOption.fold({
																			fail.invoke("Target province not found in the Diplomacy world!")
																		}, { targetProvince ->
																			val order = Order(owner, Location(nation, province), Location(targetNation, targetProvince), Order.Type.valueOf(typeArgument))

																			getWorld().addOrder(order).fold({
																				message.channel.createEmbed {
																					title = "Diplomacy"
																					description = "Successfully added an order to the Diplomacy world."

																					field("Details") {
																						"""
                                                                        					• Owner: ${ownerArgument}
                                                                        					• Nation: ${nationArgument}
                                                                        					• Province: ${provinceArgument}
                                                                        					• Type: ${typeArgument}
																							• Target nation: ${targetNationArgument}
																   							• Target province: ${targetProvinceArgument}
                                                                        				""".trimIndent()
																					}
																				}

																				saveWorld()
																			}, { warn ->
																				message.channel.createEmbed {
																					title = "Diplomacy"
																					description = "Failed to add an order to the Diplomacy world."

																					field("Reason") {
																						warn
																					}

																					field("Details") {
																						"""
                                                                        					• Owner: ${ownerArgument}
                                                                        					• Nation: ${nationArgument}
                                                                        					• Province: ${provinceArgument}
                                                                        					• Type: ${typeArgument}
																							• Target nation: ${targetNationArgument}
																   							• Target province: ${targetProvinceArgument}
                                                                        				""".trimIndent()
																					}
																				}
																			})
																		})
																	})
																})
															})
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

					literal("help") {
						executes {
							GlobalScope.launch {
								message.channel.createEmbed {
									title = "Diplomacy"
									description = "Available commands:"

									field("help") {
										"Show available commands."
									}

									field("get <nations | provinces | groups | orders>") {
										"Shows world statistics for the given query."
									}

									field("add nation \"<name>\" \"<code>\" <owner>") {
										"Adds a nation to this world."
									}

									field("add province \"<owner>\" \"<name>\" \"<Sea | Land | Inland>\" <center>") {
										"Adds a province to this world."
									}

									field("add group \"<owner>\" \"<nation>\" \"<province>\" \"<type>\"") {
										"Adds a group to this world."
									}

									field("add order \"<owner>\" \"<nation>\" \"<province>\" \"<Hold | Move | Retreat | Disband>\" (\"<target nation>\" \"<target province>\")") {
										"""
                                            Add an order to this world.
                                            Hold does not require targets.
                                        """.trimIndent()
									}
								}
							}

							return@executes 0
						}
					}
				}

				dispatcher.execute(message.content, Unit)
			}
		}

		client.login()
	}
}

fun loadWorld() = File("world.json").inputStream().bufferedReader().use {
	Json.decodeFromString<World>(it.readText())
}

fun saveWorld() = File("world.json").outputStream().bufferedWriter().use {
	it.write(Json.encodeToString(Main.WORLD))
}

fun getWorld() = Main.WORLD