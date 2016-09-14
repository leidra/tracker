package net.leidra.tracker;

import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@SpringBootApplication
@EnableJpaRepositories
@Configuration
@EnableWebSecurity
public class Application extends WebSecurityConfigurerAdapter {
    @Resource(name = "authService")
    private UserDetailsService userDetailsService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")).accessDeniedPage("/accessDenied")
                .and().authorizeRequests()
                .antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login/**", "/error/**", "/accessDenied/**", "/vaadinServlet/**").permitAll()
                .antMatchers("/user", "/**").hasAnyAuthority(Role.RoleDefinition.CENTRO.toString(),
                                                            Role.RoleDefinition.DOMICILIO.toString(),
                                                            Role.RoleDefinition.ADMIN.toString())
                .antMatchers("/admin", "/**").hasAuthority(Role.RoleDefinition.ADMIN.toString());
    }

    @Bean
    public DaoAuthenticationProvider createDaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

@Component
class ApplicationInit implements CommandLineRunner {
    @Autowired
    private UserRepository repository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new User(new Role(Role.RoleDefinition.CENTRO), passwordEncoder.encode("centro"), "centro", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("domicilio"), "domicilio", true));
        repository.save(new User(new Role(Role.RoleDefinition.ADMIN), passwordEncoder.encode("admin"), "admin", true));
    }
}