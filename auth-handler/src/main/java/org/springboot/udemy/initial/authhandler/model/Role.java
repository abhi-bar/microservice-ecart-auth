package org.springboot.udemy.initial.authhandler.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20,name = "role_name")
    private AppRoleCategory appRoleCategory;

    public Role(AppRoleCategory appRoleCategory) {
        this.appRoleCategory = appRoleCategory;
    }
}
