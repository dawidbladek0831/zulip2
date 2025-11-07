package org.acme

import jakarta.persistence.*
import org.acme.base.BaseEntity
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType.TIMESTAMP

@Entity
@Table(name = "parent_entity")
@SoftDelete(columnName = "deleted_date_time", strategy = TIMESTAMP)
internal class PostEntity(
    val name: String,

    @OneToOne(mappedBy = "post", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val child: DetailsEntity,

    @OneToOne(mappedBy = "post", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var optionalChild: OptionalDetailsEntity? = null,

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<CommentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val reactions: MutableList<ReactionEntity> = mutableListOf()
) : BaseEntity<Long>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null

    init {
        linkRelations()
    }

    fun linkRelations() {
        child.post = this
        optionalChild?.post = this
        comments.forEach { it.post = this }
        reactions.forEach { it.post = this }
    }
}
