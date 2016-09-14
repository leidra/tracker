package net.leidra.tracker.backend;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Role implements GrantedAuthority {
    public enum RoleDefinition {
        ADMIN("ADMIN"), CENTRO("CENTRO"), DOMICILIO("DOMICILIO");
        private String name;

        RoleDefinition(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Name is required")
    private RoleDefinition name;

    public Role() {
    }

    public Role(RoleDefinition definition) {
        setName(definition);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name.toString();
    }

    public RoleDefinition getRol() {
        return name;
    }

    public void setName(RoleDefinition name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = RoleDefinition.valueOf(name);
    }

    @Override
    public String getAuthority() {
        return name.toString();
    }

    public static Role create(RoleDefinition definition) {
        return new Role(definition);
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o) == 0;
    }
}
