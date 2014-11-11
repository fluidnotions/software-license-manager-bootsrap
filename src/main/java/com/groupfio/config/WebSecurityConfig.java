package com.groupfio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		   http
		   .csrf().disable()
           .authorizeRequests()
            .antMatchers("/wscon").permitAll()
            .antMatchers("/app/**").permitAll()
            .antMatchers("/user**").permitAll()
           	.antMatchers("/resources/**").permitAll()
               .anyRequest().authenticated()
               .and()
            .formLogin()
               .defaultSuccessUrl("/fiolicense")
               .loginPage("/login.html")
               .failureUrl("/login.html?error")
               .permitAll()
               .and()
            .logout()
				.logoutSuccessUrl("/login.html?logout")
				.logoutUrl("/logout.html")
				.permitAll();
		   /*.and()
			    .requiresChannel()
			    .antMatchers("/login").requiresSecure()
			    .anyRequest().requiresInsecure();*/
		   
	}
	


	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin")
				.roles("ADMIN", "USER");
	}

}