package net.leidra.tracker;

import net.leidra.tracker.backend.Role;
import net.leidra.tracker.frontend.utils.Features;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import javax.annotation.Resource;

@SpringBootApplication
@EnableJpaRepositories
@Configuration
public class Application extends SpringBootServletInitializer {
    public static final String ENCRYPT = "ENCRYPT";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] strings) {
        if(ArrayUtils.isNotEmpty(strings)) {
            switch(strings[0].toUpperCase()) {
                case ENCRYPT:
                    if(strings.length == 2) {
                        System.out.println(Features.valueOf(strings[1]).encode());
                    }
                    break;
            }
        } else {
            SpringApplication.run(Application.class, strings);
        }
    }

}

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Resource(name = "authService")
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//                .addFilter(new SecurityContextPersistenceFilter())
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/accessDenied")
                .and().authorizeRequests()
                .antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login/**", "/error/**", "/accessDenied/**", "/vaadinServlet/**").permitAll()
                .antMatchers("/user", "/**").hasAnyAuthority(Role.RoleDefinition.CENTRO.toString(),
                Role.RoleDefinition.DOMICILIO.toString(),
                Role.RoleDefinition.ADMIN.toString())
                .antMatchers("/admin", "/**").hasAuthority(Role.RoleDefinition.ADMIN.toString())
        ;//.and().sessionManagement().sessionFixation().newSession();
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