import java.lang.reflect.Executable

fun checkType(cls : Class<*>) =
    !cls.isArray && !cls.isPrimitive

fun getArgTypes(cls : Class<*>) : Set<Class<*>> {
    val result = mutableSetOf<Class<*>>()

    fun addArgs(e : Executable) {
        for (t in e.parameters)
            result.add(t.type)
    }

    for (m in cls.declaredMethods)
    {
        addArgs(m)
        result.add(m.returnType)
    }

    for (m in cls.declaredConstructors)
        addArgs(m)

    return result.filter { checkType(it) }.toSet()
}

fun getBaseTypes(cls : Class<*>) : Set<Class<*>> {
    return cls.interfaces.union(listOf(cls.superclass).filter { it != null })
}

fun getUsedTypes(cls : Class<*>) = getArgTypes(cls).union(getBaseTypes(cls))

fun getAllTypes(cls : Class<*>) : List<Class<*>> {
    val visited = mutableSetOf<Class<*>>()

    val q = mutableListOf(cls)

    while(!q.isEmpty()) {
        val curr = q[0]
        q.removeAt(0)
        visited.add(curr)

        val toAdd = getUsedTypes(curr).filter { !visited.contains(it) }
        q.addAll(toAdd)
    }

    return visited.toList()
}

fun getPackageNames(cls : Class<*>) =
    cls.`package`.name.split(".")


fun getCPPName(cls : Class<*>) =
    if (cls.isPrimitive)
        "j${cls.typeName}"
    else
        "jptr"


fun main(args : Array<String>) {
    val cls = String::class.java

    val usedTypes = getAllTypes(cls)

    usedTypes.sortedBy{ it.typeName }.forEach {
        println("${it.typeName}: ${getPackageNames(it)}")
    }

    println("Total: ${usedTypes.size}")
}
