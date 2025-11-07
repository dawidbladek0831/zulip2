package org.acme.repository.model

import jakarta.persistence.*
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SoftDelete

@Entity
@Table(name = "optional_child_entity")
@SoftDelete(columnName = "deleted")
internal class OptionalChildEntity(
    val name: String
) {
    @Id
    val id: Long? = null

    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.EXCEPTION)
    var parent: ParentEntity? = null
}
