package org.acme

import jakarta.persistence.*
import org.acme.base.BaseEntity
import org.hibernate.annotations.SoftDelete

@Entity
@Table(name = "child_entity")
@SoftDelete(columnName = "deleted")
internal class DetailsEntity(
    val name: String
) : BaseEntity<Long>() {
    @Id
    override val id: Long? = null

    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    var post: PostEntity? = null
}
