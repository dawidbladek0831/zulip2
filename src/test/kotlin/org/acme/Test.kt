package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.persistence.criteria.CriteriaDelete
import org.acme.repository.model.ChildEntity
import org.acme.repository.model.OptionalChildEntity
import org.acme.repository.model.ParentEntity
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

@QuarkusTest
internal class Test {

    @Inject
    lateinit var sf: Mutiny.SessionFactory

    @Test
    fun save() {
        sf.withTransaction { session -> session.persist(exampleEntity) }.await().indefinitely()

        val result = sf.withSession { session -> session.find(ParentEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(result.id, 1L)
    }

    @Test
    fun saveAndRemoveOptionalChild() {
        sf.withTransaction { session -> session.persist(exampleEntity) }.await().indefinitely()

        sf.removeBy(entityClass = OptionalChildEntity::class, attributeName = "id", value = 1L).await().indefinitely()

        val result = sf.withSession { session -> session.find(ParentEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(result.id, 1L)
        Assertions.assertNull(result.optionalChild)
    }

    val exampleEntity = ParentEntity(
        name = "parent",
        child = ChildEntity(
            name = "child"
        ),
        optionalChild = OptionalChildEntity(
            name = "optionalChild"
        )
    )

    fun <T : Any, V> Mutiny.SessionFactory.removeBy(
        entityClass: KClass<T>,
        attributeName: String,
        value: V
    ): Uni<Int> {
        return withTransaction { session ->
            val cb = session.criteriaBuilder
            val deleteQuery = cb.createCriteriaDelete(entityClass.java) as CriteriaDelete<T>
            val root = deleteQuery.from(entityClass.java)
            val predicate = cb.equal(root.get<V>(attributeName), value)
            session.createMutationQuery(deleteQuery.where(predicate)).executeUpdate()
        }
    }
}