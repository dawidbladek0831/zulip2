package org.acme.repository.model

import jakarta.persistence.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity
internal class ChildEntity(
    val name: String
) {
    @Id
    val id: Long? = null

    @Transient
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    var parent: ParentEntity? = null
}
