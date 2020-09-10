import arrow.core.Option
import com.mojang.brigadier.Message
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException

val NationArgument = ArgumentType {
	Main.WORLD.getNation(it.readQuotedString()).fold({
		throw CommandSyntaxException(InvalidNationException) {
			"Invalid nation name!"
		}
	}, { nation ->
		nation
	})
}

val ProvinceArgument = ArgumentType {
	Main.WORLD.getProvince(it.readQuotedString()).fold({
		throw CommandSyntaxException(InvalidProvinceException) {
			"Invalid province name!"
		}
	}, { nation ->
		nation
	})
}

val ProvinceTypeArgument = ArgumentType { 
	Option.fromNullable(Province.Type.values().find { type -> type.toString() == it.readQuotedString() }).fold({
		throw CommandSyntaxException(InvalidProvinceTypeException) {
			"Invalid province type!"
		}
	}, { type ->
		type
	})
}

val GroupTypeArgument = ArgumentType {
	Option.fromNullable(Group.Type.values().find { type -> type.toString() == it.readQuotedString() }).fold({
		throw CommandSyntaxException(InvalidGroupTypeException) {
			"Invalid group type!"
		}
	}, { type ->
		type
	})
}

val OrderTypeArgument = ArgumentType {
	Option.fromNullable(Order.Type.values().find { type -> type.toString() == it.readQuotedString() }).fold({
		throw CommandSyntaxException(InvalidOrderTypeException) {
			"Invalid order type!"
		}
	}, { type ->
		type
	})
}

val InvalidNationException = object : CommandExceptionType {}
val InvalidProvinceException = object : CommandExceptionType {}
val InvalidProvinceTypeException = object : CommandExceptionType {}
val InvalidGroupTypeException = object : CommandExceptionType {}
val InvalidOrderTypeException = object : CommandExceptionType{}