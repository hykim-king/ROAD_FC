package com.pcwk.ehr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
<<<<<<< HEAD
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
=======
>>>>>>> origin/sungmin
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf((csrf) -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/email/**"),
                    new AntPathRequestMatcher("/member/checkUsername"),
                    new AntPathRequestMatcher("/email/mailSend"),
                    new AntPathRequestMatcher("/email/mailauthCheck")
                )
            )
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/email/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/member/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
            )
            .headers((headers) -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )
            .formLogin((formLogin) -> formLogin
                .loginPage("/member/login")
                .defaultSuccessUrl("/member/profile")
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/member/login")
                .invalidateHttpSession(true)
            );

        return http.build();
    }

	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();	
	}
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		//모든 요청에 대해 허용
		
		//XFrameOptions 헤더를 DENY 대신 SAMEORIGIN으로 변경
		//SAMEORIGIN : 프레임에 포함된 웹페이지가 동일한 사이트에서 제공할 때에만 사용 허가
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
				)
		.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
				)
		.headers((headers) -> headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
		.formLogin((formLogin) -> formLogin
			.loginPage("/member/login")
			.defaultSuccessUrl("/member/list")
		)
		.logout((logout) -> logout
			.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
			.logoutSuccessUrl("/member/list")
			.invalidateHttpSession(true)
		)
		;
	
		return http.build();

	}


}
