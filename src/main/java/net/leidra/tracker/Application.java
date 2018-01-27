package net.leidra.tracker;

import net.leidra.tracker.backend.Role;
import net.leidra.tracker.backend.User;
import net.leidra.tracker.backend.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@SpringBootApplication
@EnableJpaRepositories
@Configuration
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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

    public static void main(String... args) {
        System.out.println(new BCryptPasswordEncoder().encode("acufade"));
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

        addPresentationUsers();
    }

    private void addPresentationUsers() {
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43836363L"), "ADAY_PADILLA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78574353M"), "ALICIA_DIAZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("45981212A"), "ARIADNA_MARTÍN", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43831975R"), "BETSABET_GARCIA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78615754Y"), "CARMEN_DOLORES", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78573805P"), "CECILIA_MARTIN", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("54046019K"), "CRISTINA_SANTOS", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43832253A"), "DÁCIL_GARCIA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78850863D"), "DANIEL_DELGADO", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("79087423Z"), "ELENA_GUIÉRREZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78631365T"), "ESTEFANIA_CORREA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("45981393T"), "ESTELA_MEDINA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78408287E"), "EVA_FARIÑA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43818291W"), "FATIMA_ARMAS", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78852714C"), "GEMA_CHUECA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78701496G"), "IDAIRA_CABRERA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43832292L"), "IRENE_PEREZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43830255Y"), "ITAHISA_COELLO", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78644104B"), "JACOBO_GONZÁLEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78637134L"), "JEZABEL_HERNANDEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43381904V"), "JOSUE_HERNANDEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("54050704Z"), "JUDITH_HERNÁNDEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("46297645W"), "M_BELEN_SANCHEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("42098171Y"), "M_ELENA_FELIPE", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("45980375V"), "MAGALI_FRANCHY", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("42065693G"), "MARÍA_CRISTINA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43366258B"), "MARÍA_DOLORES", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78645055M"), "MARÍA_MARTÍN", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78723248K"), "NATALIA_GARCIA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("44787509T"), "NOELIA_PEÑA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("54051676C"), "PRISCILA_CORREA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("44789320V"), "RAFAEL_BECERRA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78555196F"), "ROSA_SAN_NICOLAS", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78710010P"), "SANDRA_GARCIA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("42220217Z"), "SARAY_GONZÁLEZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("79062811N"), "VIOLETA_AFRICA", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("43382595H"), "YASMINA_DIAZ", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("45851703F"), "YERAY_CASTRO", true));
        repository.save(new User(new Role(Role.RoleDefinition.DOMICILIO), passwordEncoder.encode("78728845Y"), "YUMARA_PADILLA", true));
    }
}