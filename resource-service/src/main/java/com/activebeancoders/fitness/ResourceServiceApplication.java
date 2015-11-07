package com.activebeancoders.fitness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Dan Barrese
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableRedisHttpSession
public class ResourceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServiceApplication.class, args);
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
    // We need this to prevent the browser from popping up a dialog on a 401
//        http.httpBasic().disable();

    // Allow "USER" role to change data.
//        http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").hasRole("USER").anyRequest().authenticated();
//    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
            // @formatter:off
			auth.inMemoryAuthentication()
				.withUser("user").password("password").roles("USER")
			.and()
				.withUser("admin").password("admin").roles("USER", "ADMIN", "READER", "WRITER")
			.and()
				.withUser("audit").password("audit").roles("USER", "ADMIN", "READER");
            // @formatter:on
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.httpBasic().disable();
			http
				.httpBasic()
			.and()
				.logout()
			.and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/**")
                    .hasRole("USER").anyRequest().authenticated()
			.and()
				.csrf().csrfTokenRepository(csrfTokenRepository())
			.and()
				.addFilterBefore(csrfHeaderFilter(), CsrfFilter.class);
			// @formatter:on
        }

        private Filter csrfHeaderFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                    if (csrf != null) {
                        Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                        String token = csrf.getToken();
                        if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                            cookie = new Cookie("XSRF-TOKEN", token);
                            cookie.setPath("/");
                            System.out.println("ADDING COOKIE TO RESPONSE");
                            response.addCookie(cookie);
                        }
                    }
                    boolean cookieAlreadyExists = false;
                    for (Cookie cookie : request.getCookies()) {
                        if (cookie.getName().equals("XSRF-TOKEN")) {
                            cookieAlreadyExists = true;
                            break;
                        }
                    }
                    if (!cookieAlreadyExists) {
                        System.out.println("COOKIE DOES NOT EXIST ON REQUEST");
                    }
                    filterChain.doFilter(request, response);
                }
            };
        }

        private CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }
    }

}
