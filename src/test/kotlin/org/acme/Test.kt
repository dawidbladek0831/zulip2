package org.acme

import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.acme.repository.model.ChildEntity
import org.acme.repository.model.ParentEntity
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@QuarkusTest
internal class Test {

    @Inject
    lateinit var sf: Mutiny.SessionFactory

    @Test
    fun test1() {
        sf.withTransaction { session -> session.persist(exampleEntity) }.await().indefinitely()

        val result = sf.withSession { session -> session.find(ParentEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(result.id, 1L)
        Assertions.assertEquals(result.child.id, 1L)
    }

    val exampleEntity = ParentEntity(
        name = "parent",
        child = ChildEntity(
            name = "child"
        )
    )
}