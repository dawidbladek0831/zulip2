package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.persistence.criteria.CriteriaDelete
import org.hibernate.reactive.mutiny.Mutiny
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

@QuarkusTest
internal class Test {

    @Inject
    lateinit var sf: Mutiny.SessionFactory

    @Test
    fun shouldSave() {
        val entity = PostEntity(
            name = "parent",
            child = DetailsEntity(
                name = "child"
            ),
            optionalChild = OptionalDetailsEntity(
                name = "optionalChild"
            ),
            comments = mutableListOf(
                CommentEntity(name = "comment"),
                CommentEntity(name = "comment2")
            ),
            reactions = mutableListOf(
                ReactionEntity(name = "reaction"),
                ReactionEntity(name = "reaction2")
            ),
        )

        sf.withTransaction { session -> session.persist(entity).flatMap { session.flush() } }.await().indefinitely()

        val result = sf.withSession { session -> session.find(PostEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(result.id, 1L)
        Assertions.assertNotNull(result.optionalChild)
        Assertions.assertEquals(2, result.comments.size)
        Assertions.assertEquals(2, result.reactions.size)
    }

    @Test
    fun shouldReturnOnlyNotDeletedComments() {
        val entity = PostEntity(
            name = "parent",
            child = DetailsEntity(
                name = "child"
            ),
            optionalChild = OptionalDetailsEntity(
                name = "optionalChild"
            ),
            comments = mutableListOf(
                CommentEntity(name = "comment"),
                CommentEntity(name = "comment2")
            ),
            reactions = mutableListOf(
                ReactionEntity(name = "reaction"),
                ReactionEntity(name = "reaction2")
            ),
        )
        sf.withTransaction { session -> session.persist(entity) }.await().indefinitely()
        sf.removeBy(entityClass = CommentEntity::class, attributeName = "id", value = 1L).await().indefinitely()

        val result = sf.withSession { session -> session.find(PostEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(1, result.comments.size)
        Assertions.assertEquals(2, result.reactions.size)
    }

    @Test
    fun shouldReturnOnlyNotDeletedReactions() {
        val entity = PostEntity(
            name = "parent",
            child = DetailsEntity(
                name = "child"
            ),
            optionalChild = OptionalDetailsEntity(
                name = "optionalChild"
            ),
            comments = mutableListOf(
                CommentEntity(name = "comment"),
                CommentEntity(name = "comment2")
            ),
            reactions = mutableListOf(
                ReactionEntity(name = "reaction"),
                ReactionEntity(name = "reaction2")
            ),
        )
        sf.withTransaction { session -> session.persist(entity) }.await().indefinitely()
        sf.removeBy(entityClass = ReactionEntity::class, attributeName = "id", value = 1L).await().indefinitely()

        val result = sf.withSession { session -> session.find(PostEntity::class.java, 1L) }.await().indefinitely()

        Assertions.assertEquals(2, result.comments.size)
        Assertions.assertEquals(1, result.reactions.size)
    }

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