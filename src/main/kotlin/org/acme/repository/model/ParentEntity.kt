package org.acme.repository.model

import jakarta.persistence.*
import kotlinx.serialization.Serializable

@Serializable
@Entity
internal class ParentEntity(
    val name: String,

    @OneToOne(mappedBy = "parent", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val child: ChildEntity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    init {
        linkRelations()
    }

    fun linkRelations() {
        child.parent = this
    }
}
