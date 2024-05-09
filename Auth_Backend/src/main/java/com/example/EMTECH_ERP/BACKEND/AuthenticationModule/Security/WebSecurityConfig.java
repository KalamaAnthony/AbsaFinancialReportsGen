package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security.Jwt.AuthEntryPointJwt;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security.Jwt.AuthTokenFilter;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security.Services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(// securedEnabled = true,// jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${users.app.client.origin_url}")
    private String origin;
    @Value("${users.app.client.origin_ip}")
    private String client_origin_ip;
    @Value("${users.app.client.origin_52_ip}")
    private String client_origin_52_ip;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .maximumSessions(1) // Allow only one session per user
                .maxSessionsPreventsLogin(true) // Prevent login when maximum sessions is reached
                .expiredUrl("/login?expired")
                .and()
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-ui/**",
         "/swagger-ui/",
         "/v3/api-docs/**",
         "/v2/api-docs",
         "/v3/api-docs",

         "/configuration/ui",
         "/swagger=resources",
         "/swagger-resources/**",
         "/configuration/security",
         "/webjars/**",
         "/swagger-ui.html",
         "/swagger-ui/index.html",
         "/swagger/**", "/swagger-ui.html", "/swagger-ui/index.html","/swagger-ui/#/").permitAll()
                .antMatchers("/api/v1/auth/**").permitAll()
             .antMatchers("/api/v1/role/**").permitAll()
         // .antMatchers("/api/v1/role/**").hasRole("SUPERUSER")
               .antMatchers("/api/v1/auth/refreshtoken/**").permitAll()
               .antMatchers("/api/v1/auth/otp/**").permitAll()
               .antMatchers("/api/v1/auth/reset-password/**").permitAll()
               .antMatchers("/api/v1/auth/forgot-password/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
               .antMatchers("/api/v1/auth/userSignIn").permitAll()
                .antMatchers("/users/audit/all").permitAll()
                .antMatchers("/users/audit/getAllByloginTime").permitAll()
//               .antMatchers("/api/v1/auth/signup/**").hasRole(SUPERUSER.name())


                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.logout()
                .logoutUrl("/api/v1/auth/logout") // Configure the logout URL
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    // Implement custom logic here, e.g., update UserAudit entity
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

    }
    @Bean
    CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin(origin);
        config.addAllowedOrigin(client_origin_ip);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
