package com.hospital.doctor.config;


import com.hospital.doctor.config.JwtValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

@Autowired
private  JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/api/doctors/getalldoctors",
                                        "/api/doctors/getbyid/*",                             // Replaced {id} with *
                                        "/api/doctors/get-doctor/**",
                                        "/api/doctors/profile-image/**",
                                        "/api/doctors/schedule-dates/**",
                                        "/api/doctors/available/slots/timings/**",
                                        "/api/doctors/book-slot",
                                        "/api/doctors/find-schedule-id"

                                ).permitAll()

                                // Admin-only endpoints
                                .requestMatchers(
                                        "/api/doctors/creating",
                                        "/api/doctors/update/**",
                                        "/api/doctors/delete/**",
                                        "/api/doctors/update-user-id"

                                ).hasAuthority("ADMIN")

                                // Doctor-only endpoints
                                .requestMatchers("/api/doctors/*/set-availability",
                                        "/api/doctors/doctor/*/set-availability" ,
                                        "/api/doctors/reschedule/*/*",
                                         "/api/doctors/data/*",
                               "/api/doctors/delete-slots/*/*"
                                ).hasAuthority("DOCTOR")

                        .requestMatchers(
                                "/api/doctors/user/**",
                                "/api/doctors/email/**","/api/doctors/slots-status/**").hasAnyAuthority("ADMIN","DOCTOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

