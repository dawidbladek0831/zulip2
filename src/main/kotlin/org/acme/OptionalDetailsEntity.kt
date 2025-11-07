package org.acme

import jakarta.persistence.*
import org.acme.base.BaseEntity
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SoftDelete

@Entity
@Table(name = "optional_child")
@SoftDelete(columnName = "deleted")
internal class OptionalDetailsEntity(
    val name: String
) : BaseEntity<Long>() {
    @Id
    override val id: Long? = null

    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.EXCEPTION)
    var post: PostEntity? = null
}
