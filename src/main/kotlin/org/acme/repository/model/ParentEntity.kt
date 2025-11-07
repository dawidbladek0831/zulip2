package org.acme.repository.model

import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType.TIMESTAMP

@Entity
@Table(name = "parent_entity")
@SoftDelete(columnName = "deleted_date_time", strategy = TIMESTAMP)
internal class ParentEntity(
    val name: String,

    @OneToOne(mappedBy = "parent", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val child: ChildEntity,

    @OneToOne(mappedBy = "parent", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var optionalChild: OptionalChildEntity? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    init {
        linkRelations()
    }

    fun linkRelations() {
        child.parent = this
        optionalChild?.parent = this
    }
}
