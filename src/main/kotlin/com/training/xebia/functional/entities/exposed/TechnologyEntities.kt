/*
package com.training.xebia.functional.entities.exposed

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insertAndGetId

*/
/*object TechnologyEntities : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 150)
    val keywords = text("keywords")
    override val primaryKey = PrimaryKey(id, name = "PK_TechnologyEntities_Id") // PK_TechnologyEntities_Id is optional here
}*//*


object TechnologyEntities : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 150)
    val keywords = text("keywords")
}

class TechnologyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TechnologyEntity>(TechnologyEntities) {
        const val SEPARATOR = ","
    }

    var name by TechnologyEntities.name
    var description by TechnologyEntities.description
    var keywords by TechnologyEntities.keywords.transform(
        toColumn = { it.joinToString(SEPARATOR) },
        toReal = { it.takeIf { it.trim().isNotEmpty() }?.split(SEPARATOR) ?: emptyList() }
    )
}

*/
/*val technologyId = TechnologyEntities.insertAndGetId {
    it[name] = element.name
    it[description] = element.description
    it[keywords] = adapter.toString(element.keywords)
}*//*


*/
