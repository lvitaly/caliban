package caliban

import caliban.schema.Types.{ Argument, Field, Type, TypeKind }

object Rendering {

  def renderType(t: Type): String = t.kind match {
    case TypeKind.NON_NULL => ""
    case TypeKind.LIST     => ""
    case TypeKind.UNION =>
      s"""${renderDescription(t.description)}${renderKind(t.kind)} ${renderTypeName(t)} = ${t.subTypes
        .flatMap(_.name)
        .mkString(" | ")}"""
    case _ =>
      s"""
         |${renderDescription(t.description)}${renderKind(t.kind)} ${renderTypeName(t)} {
         |  ${t.fields.map(renderField).mkString("\n  ")}${t.values.mkString("\n  ")}
         |}
         |""".stripMargin
  }

  def renderKind(kind: TypeKind): String =
    kind match {
      case TypeKind.OBJECT       => "type"
      case TypeKind.UNION        => "union"
      case TypeKind.ENUM         => "enum"
      case TypeKind.INPUT_OBJECT => "input"
      case _                     => ""
    }

  def renderDescription(description: Option[String]): String = description match {
    case None        => ""
    case Some(value) => if (value.contains("\n")) s"""\"\"\"\n$value\"\"\"\n""" else s""""$value"\n"""
  }

  def renderField(field: Field): String =
    s"${field.name}${renderArguments(field.arguments)}: ${renderTypeName(field.`type`)}"

  def renderArguments(arguments: List[Argument]): String = arguments match {
    case Nil  => ""
    case list => s"(${list.map(a => s"${a.name}: ${renderTypeName(a.argumentType)}").mkString(", ")})"
  }

  def renderTypeName(fieldType: Type): String =
    fieldType.kind match {
      case TypeKind.NON_NULL => s"${fieldType.ofType.map(renderTypeName).getOrElse("null")}!"
      case TypeKind.LIST     => s"[${fieldType.ofType.map(renderTypeName).getOrElse("null")}]"
      case _                 => s"${fieldType.name.getOrElse("null")}"
    }
}
