package com.cslibrary.library.config

import com.cslibrary.library.security.JWTTokenProvider
import com.cslibrary.library.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
class SecurityConfig(private val jwtTokenProvider: JWTTokenProvider) : WebSecurityConfigurerAdapter() {

    // Register authenticationManagerBean.
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .authorizeRequests()
            .antMatchers(
                "/api/v1/seat",
                "/api/v1/state",
                "/api/v1/report",
                "/api/v1/user/time"
            ).hasRole("USER")
            .antMatchers(
                "/api/v1/admin/**"
            ).hasRole("ADMIN")
            .antMatchers("/**").permitAll()
            .and()
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

    }
}