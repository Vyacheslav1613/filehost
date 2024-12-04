package ru.netology.diplom;


import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class AuthorizeRequestsSecurityConfiguration {


    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder().username("adm").password(encoder.encode("admin")).roles("admin").build();
        UserDetails user = User.builder().username("us").password(encoder.encode("user")).roles("user").build();
        UserDetails newUs = User.builder().username("new").password(encoder.encode("user")).roles("user").build();

        return new InMemoryUserDetailsManager(admin, user, newUs);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain authorizeRequestsFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.GET, "/admin").hasRole("admin")
                        .requestMatchers("/upload").authenticated()
                        .requestMatchers("/upload/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/").authenticated()
                        .requestMatchers(HttpMethod.GET, "/file/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/delete/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/files/*").authenticated()
                        .anyRequest().authenticated())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll).build();
    }


}