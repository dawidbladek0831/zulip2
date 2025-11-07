package org.acme

import jakarta.persistence.*
import org.acme.base.BaseEntity
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SoftDelete

@Entity
@Table(name = "comment")
@SoftDelete(columnName = "deleted")
internal class CommentEntity(
    val name: String
) : BaseEntity<Long>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.EXCEPTION)
    var post: PostEntity? = null


}
