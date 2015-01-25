package br.com.webbudget.domain.entity.users;

import br.com.webbudget.application.components.validators.MatchFields;
import br.com.webbudget.domain.entity.PersistentEntity;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0
 * @since 1.0, 06/10/2013
 */
@Entity
@Table(name = "users")
@ToString(callSuper = true, of = {"email", "username"})
@EqualsAndHashCode(callSuper = true, of = {"email", "username"})
@MatchFields(
        first = "unsecurePassword", 
        second = "unsecurePasswordConfirmation", 
        message = "{user-account.password-not-match}")
public class User extends PersistentEntity implements UserDetails {

    @Getter 
    @Setter
    @NotEmpty(message = "{user-account.name}")
    @Column(name = "name", length = 90, nullable = false)
    private String name;
    @Getter 
    @Setter
    @Email(message = "{user-account.email}")
    @NotEmpty(message = "{user-account.email}")
    @Column(name = "email", length = 90, nullable = false)
    private String email;
    @Setter
    @NotEmpty(message = "{user-account.username}")
    @Column(name = "username", length = 45, nullable = false)
    private String username;
    @Getter
    @Setter
    @Column(name = "password", length = 64, nullable = false)
    private String password;
    @Getter 
    @Setter
    @Column(name = "blocked")
    private boolean blocked;
    
    /**
     * as permissoes do usuario, eager pq se for lazy quando o spring solicitar
     * as authoritys vai bater no proxy do hibernate e estourar um lazyinitiexp
     */
    @Getter
    @Setter
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Permission> permissions;
    
    @Getter 
    @Setter
    @Transient
    private boolean selected;
    @Getter 
    @Setter
    @Transient
    @NotEmpty(message = "{user-account.password}")
    @Length(min = 5, max = 64, message = "{user-account.password-gt-5}")
    private String unsecurePassword;
    @Getter 
    @Setter
    @Transient
    private String unsecurePasswordConfirmation;

    /**
     * Junta todas as permissoes do usuario vindas pela ralacao user-permission
     * e devolve elas no metodo padrao do spring para saber quais as authorities
     * do usuario
     * 
     * @return a lista de authorities do usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isAccountNonExpired() {
        return !this.blocked;
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isAccountNonLocked() {
        return !this.blocked;
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return !this.blocked;
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isEnabled() {
        return !this.blocked;
    }
}
