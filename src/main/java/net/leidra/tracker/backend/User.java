package net.leidra.tracker.backend;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"username"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "users_role")
    private Role role = Role.create(Role.RoleDefinition.CENTRO);
    private String password;
    private String username;
    private boolean enabled;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("start ASC")
    private Set<Assistance> assistances = new HashSet();

    public User() {
    }

    public User(Role roles, String password, String username, boolean enabled) {
        this.role = roles;
        this.password = password;
        this.username = username;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRoles(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private static SortedSet<Role> sortroles(
            Collection<? extends Role> roles) {
        Assert.notNull(roles, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getroles() contract and SEC-717)
        SortedSet<Role> sortedroles = new TreeSet<Role>(
                new AuthorityComparator());

        for (Role grantedAuthority : roles) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedroles.add(grantedAuthority);
        }

        return sortedroles;
    }

    private static class AuthorityComparator implements Comparator<Role>,
            Serializable {
        private static final Long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(Role g1, Role g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    /**
     * Returns {@code true} if the supplied object is a {@code User} instance with the
     * same {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username, representing
     * the same principal.
     */
    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof org.springframework.security.core.userdetails.User) {
            return username.equals(((User) rhs).username);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");

        if (role != null) {
            sb.append("Granted roles: ");

            sb.append(role);
       }
        else {
            sb.append("Not granted any roles");
        }

        return sb.toString();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Assistance> getAssistances() {
        return assistances;
    }

    public void setAssistances(Set<Assistance> assistances) {
        this.assistances = assistances;
    }
}