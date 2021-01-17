package com.blog.blog_project.security;

import com.blog.blog_project.security.jwt.AuthEntryPointJwt;
import com.blog.blog_project.security.jwt.AuthTokenFilter;
import com.blog.blog_project.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
//allows spring to find and automatically apply the class to the global web security
@EnableWebSecurity
//provides AOP security on methods. it enables @PreAuthorize, @PostAuthorize, it also supports JSR-250
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Spring Security will load User details to perform authentication & authorization. So it has UserDetailsService
     * interface that we need to implement
     */

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){ return new AuthTokenFilter();}

    /**
     * The implementation of UserDetailsService will be used for configuring DaoAuthenticationProvider by
     * AuthentictionManagerBuilder.userDetailsService() method
     *
     * Essenially configure AuthenticationManagerBuilder to use the UserDetailsService interface t check for user information
     */

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder.userDetailsService(userDetailsService);
    }

    /**
     *  Defined a bean for AUthenticationManager
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    /**
     * PasswordEncoder is needed for the DaoAuthenticationProvider. If this is not specified, it will use plain text
     */
    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();
    }

    /**
     * We override the configure(HttpSecurity http) method from WebSecurityConfigureAdapter intergace.
     * It tells Spring Security how we configure CORS and CSRF, when we want to require all users to be authticated
     * or not, which filter (AuthTokenFilter) and when we want it to work (filter BEFORE UsernamePasswordAuthenticationFilter),
     * which Exception Handler is chose (AuthEntryPointJwt)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/auth/signup").permitAll()
                .antMatchers("/api/auth/signin").permitAll()
                .antMatchers("/api/auth/refresh/token").permitAll()
                .antMatchers("api/auth/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/refresh/token").permitAll()
                .antMatchers(HttpMethod.POST,"/api/auth/logout").permitAll()
                .antMatchers(HttpMethod.GET,"/posts").permitAll()
                .antMatchers(HttpMethod.GET,"/posts/**").permitAll()
                .antMatchers(HttpMethod.POST, "/image/uploadS3").permitAll()
                .antMatchers(HttpMethod.GET, "/tags/**").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    //added from SO
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
