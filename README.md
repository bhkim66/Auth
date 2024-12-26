# Spring Security, 프로젝트 구성

### 인증 흐름

![image](https://github.com/user-attachments/assets/ab708cb6-7ea2-4ccc-9ab6-e7062d8de0f4)

- Refresh Token 값을 저장하기 위해 메모리 저장소인 `Redis`를 사용했다
- 재발급 API 요청 시 일반 DB에서 유저 정보를 가져오는 대신 Redis에서 가져온다
- **장점**:
    - **짧은 응답속도, 빠른 처리 속도**
    - **TTL(Time-to-Live) 관리**
        - Refresh Token의 만료 시간을 정해 유효기간 관리가 편리
    - **Stateless 인증 구현**
        - Refresh Token을 Redis에 저장하면 서버가 “상태를 가지지 않는” 형태로 구현하기 적합하다
        - 중앙 저장소 역활 수행
    - 로그인 된 유저 파악 및 비정상 회원 관리 가능

### Redis

- 메모리 기반의 데이터 저장소이다
- `Key - Value` 데이터 구조에 기반한 다양한 형태의 자료 구조를 제공하며, 데이터들을 저장할 수 있는 저장소이다
- `Redis` 는 메모리에 데이터를 저장하기 때문에 저장 공간에 제약이 있어 보조 데이터 저장소로 사용되나 **메모리를 사용하기 때문에 빠른 처리 속도가 장점**이다

---

기본적으로 HTTP 통신을 통한 요청/응답을 처리하고 있기 때문에 `Stateless` 한 토큰 방식의 인증 방식을 채택, JWT 토큰 방식을 사용 할 것이다

- 위 글에서 확인 했듯이 토큰은 DB에 따로 저장하지 않아도 되고 `Stateless` 한 특성을 가지고 있어 장점이 많다
- 하지만 토큰이 탈취될 경우 탈취한 공격자가 탈취한 토큰 정보를 가지고 피해자 행세를 할 수 있기 때문에 토큰에는 만료시간을 지정해서 관리할 필요가 있다
- `AccessToken` 같은 경우는 인증을 위해 주로 사용되는 토큰이기 때문에 짧은 시간을 지정하는 것이 좋고 `RefreshToken`은 토큰을 재발급 받기 위한 토큰이기 때문에 좀 더 여유로운 시간을 지정해도 괜찮다
    - 이 프로젝트에서는 `AccessToken`은 30분 `RefreshToken`은 24시간만 유효하게 설정했다
    - Redis에도 24시간 뒤면 Expire 되게 설정했다
        
        

### 스프링 시큐리티 설정

**SecurityConfig**

```java
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // csrf 관련 정책 비활성화
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
								// 1.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 2. URL에 따라 인증/인가 설정 
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                .requestMatchers("/auth/**", "/public/**").permitAll()
                                .requestMatchers("/admin/**").hasAnyAuthority(ADMIN.name())
//                        .requestMatchers("/admin/**").access(new UserAuthorizationManger())
                                .anyRequest().authenticated() // 모든 요청은 인증 필요
                )
                //3. 인증되지 않은 요청 처리 
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .formLogin(AbstractHttpConfigurer::disable)
                // 4. FIlter 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("*");
        configuration.addAllowedOriginPattern("http://localhost:*");
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 5. 암호화에 필요한 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

1. `corsConfigurationSource()` : `CORS` 허용 범위를 설정하기 위한 설정이다
    - CORS 허용 범위를 와일드카드 `*` 로 설정하면 보안에 취약하기 때문에 직접 허용할 출처를 세팅하는 방법이 좋다
2. `"/auth"`, `"/public"` 으로 들어오는 요청은 모두 permitAll(), 접근을 허용해주었다 
    - 권한 없이 실행 되야하거나, 인증이 따로 필요 없는 API들이다. 그 외는 모두 인증을 받아야 한다
    - `"/admin/”` 으로 시작하는 요청은 `ADMIN` 권한이 있어야 허용이 된다
3. 인증되지 않은 즉 `UNAUTHORIZED` Exception을 처리하기 위한 클래스를 지정했다 
4. `UsernamePasswordAuthenticationFilter`는 유저가 로그인을 시도할 때 보내지는 요청에서 아이디와 패스워드 데이터를 가져온 후 **인증을 위한 토큰을 생성 후 인증을 다른 쪽에 위임하는 역할**을 하는 필터이다
    - `UsernamePasswordAuthenticationFilter`가 실행되기 전에 `OncePerRequestFilter`을 상속하여 구현한 `JwtAuthenticationFilter`가 먼저 실행된다
        - `OncePerRequestFilter`은 Http Request의 한 번의 요청에 대해 한 번만 실행하는 Filter이다
        - 포워딩으로 인해 Filter Chain이 다시 동작하여 여러번 Filter가 발생하는 불상사를 막아준다
        - `OncePerRequestFilter`을 구현하여 Request에 담겨져 있는 토큰값의 유효성 검사를 통해 인증/인가를 한번만 거치고 다음 로직을 수행할 수 있다
5. 암호화에 필요한 `PasswordEncoder` Bean으로 등록했다 

**JwtAuthenticationEntryPoint**

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        setErrorResponse(response, authException);
    }

    private void setErrorResponse(HttpServletResponse res, RuntimeException e) throws IOException {
        ApiResponseResult<String> result = ApiResponseResult.failure(UNAUTHORIZED_EXCEPTION);
        ObjectMapper objectMapper = new ObjectMapper();
        String resultSrt = objectMapper.writeValueAsString(result);

        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(resultSrt);
        res.setStatus(SC_UNAUTHORIZED);
        res.flushBuffer();
    }
}
```

- 인증이 되지않은 유저가 요청을 했을때 동작한다

**JwtAuthenticationFilter**

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider; //1. JwtTokenProvider 의존성 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //2. Request Header 에서 JWT 토큰 추출
        Optional<String> extractToken = extractToken(request);

        try {
            if(extractToken.isPresent()) {
                String token = extractToken.get();
                //3. 토큰 유효성 검사
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                //4. SecurityContext에 Authentication 객체 저장 
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            log.error("e message : {} ", e.getMessage());
            throw new ApiException(MEMBER_REQUIRED);
        }
        filterChain.doFilter(request, response);
    }
    
    //2. Request Header 에서 JWT 토큰 추출
    private Optional<String> extractToken(ServletRequest request) {
       return Optional.ofNullable(((HttpServletRequest) request).getHeader(KEY_HEADER_AUTHORIZATION));
    }
}
```

1. Jwt 토큰 관련 처리를 도와줄 클래스를 주입 받는다
2. Request에 `“Authorization”` Key 값으로 넘어온 Header 값을 리턴한다
3. 토큰의 값이 있을 경우 토큰의 유효성 검사를 실시한다
4. 토큰으로 부터 꺼내온 `Authentication` 객체를  `SecurityContext`에 저장한다. 
    - 저장된`Authentication` 값은 어디서든지 접근할 수 있다. `SecutiryContextHolder`가  ****`SecurityContext`을 스레드 로컬 변수에 저장하기 때문이다

**JwtTokenProvider** 

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtHandler jwtHandler; //1. Jwt 토큰 생성 및 파싱 

    @Getter
    @AllArgsConstructor
    public static class PrivateClaims { //2. Claims 클래스
        private String userId;
        private RoleEnum role;
    }

    public String generateToken(PrivateClaims privateClaims, Long expireTime) {
        return jwtHandler.createJwt(Map.of(USER_ID, privateClaims.getUserId(), ROLE, privateClaims.getRole()), expireTime);
    }

    //3. Refresh Token 유효 확인
    public PrivateClaims parseRefreshToken(String refreshToken, String redisRefreshToken) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new ApiException(INVALID_TOKEN_VALUE);
        }
        return jwtHandler.parseClaims(refreshToken).map(this::convert).orElseThrow();

    }

    //4. 토큰 검증
    public boolean validateToken(String token){
        try {
            Optional<Claims> claims = jwtHandler.parseClaims(token);
//            existSignOutMemberInRedis(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        throw new ApiException(INVALID_TOKEN_VALUE_ERROR);
    }

    //5. JWT 토큰을 복호화 Authentication 객체 생성 
    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = jwtHandler.parseClaims(token).orElseThrow();
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" +
                claims.get(ROLE)));

        User user = User.builder()
                .id((String) claims.get(USER_ID))
                .role(RoleEnum.valueOf((String) claims.get(ROLE)))
                .build();
        // UserDetails 객체를 만들어서 Authentication 리턴
        CustomUserDetail principal = new CustomUserDetail(user);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

		//6. 헤더에 있는 RefreshToken을 가져옴
   public String getTokenInHeader() {
        String refreshToken = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader(GET_HEADER_REFRESH_TOKEN);
        return validateToken(refreshToken) ? refreshToken : "";
    }

    private PrivateClaims convert(Claims claims) {
        return new PrivateClaims(claims.get(USER_ID, String.class), RoleEnum.valueOf(claims.get(ROLE, String.class)));
    }
}
```

1. `Jwt 토큰` 생성 및 파싱을 위한 클래스 의존성을 주입한다
2. Jwt 토큰에 들어갈 Claim을 내부클래스로 구현했다 
3. Request의 `RefreshToken`과 Redis에 저장된 `RefreshToken`값을 비교한다
4. Token 값의 유효성 검사를 한다 
    - 값이 유효하지 않거나 유효기간이 지났거나 jwt 토큰 형식이 아닐 경우 예외가 발생한다
    - 토큰은 유효하지 않을 경우 보안상 위험이 있기 때문에 DB를 접근하는 것 보다 토큰을 버리고 다시 **재생성 하는 것이 더 효율적**이다
5. 토큰 값을 복호화 하여 다시 `Authentication` 객체를 만들어 리턴한다
    - 리턴한 `Authentication` 객체를 `SecurityContext`에 저장한다
6. 토큰 재발급을 할 때 RefreshToken 값을 Header에 넣어 전달한다.
    - Header에 있는 RefreshToken 값을 꺼내 전달한다

**JwtHandler**

```java
@Component
public class JwtHandler {
    private static final String BEARER_TYPE = "Bearer";
    private final Key key;

    public JwtHandler() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //1.key 생성
    }

    //2. 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public String createJwt(Map<String, Object> privateClaims, Long expireTime) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setIssuedAt(now) // 토큰 발급 시간
                .setIssuer("auth.bhkim.com") // 토큰 발급자
                .setClaims(privateClaims)
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key) // 키 셋팅
                .compact();
    }

		//3. token을 파싱하여 Claims를 추출한다 
    public Optional<Claims> parseClaims(String token) {
        return Optional.of(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }
}
```

1. Jwt 토큰 암호화를 위해  `io.jsonwebtoken.security.Keys` 유틸 클래스는 강력하고 간단한 키를 제공한다 
    - 기존 문자열을 받아 바이트 배열로 값을 받았지만 안전하지 않고 적당하지 않은 키를 인수로 사용했기에 변경되었다
2. `PrivateClaims` 클래스로 만든 사용자 정보를 토큰 `Claims`에 넣는다. 토큰 파싱을 통해 값을 다시 꺼낼 수 있다 
    - `setExpiration()` 메소드를 통해서 토큰의 유효기간을 정할 수 있다
3. 인자값으로 넘어온 `Token`을 파싱하여 `Claims`를 반환해준다 

**CustomUserDetail**

```java
public class CustomUserDetail implements UserDetails {
    private User user;

    public CustomUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getValue()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    public String getId() {
        return user.getId();
    }

    public RoleEnum getRole() {
        return user.getRole();
    }
}
```

- Spring Security에서 제공해주는 UserDetail 인터페이스를 구현했다
- 앞으로 `AuthenticationToken`을 만들때 `CustomUserDetail`의 형태로 `principal`이 생성된다
- `getAuthorities()`은 유저가 가진 권한을 리턴해주는 메소드이다
    - 지금은 Role만 있기 때문에 회원이 가진 Role 값을 전달해준다
    - `SimpleGrantedAuthority`은 `GrantedAuthority`의 간단한 구현체로써 String으로 Role 값을 받는다

**CustomUserDetailsService** 

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다"));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private CustomUserDetail createUserDetails(User user) {
       return new CustomUserDetail(user);
    }
}
```

- **`CustomUserDetailsService`**은 ****`UserDetail`을 구현한 **** `CustomUserDetail`을 반환한다
- `loadUserByUsername()`  userId로 DB를 조회해서  유저를 찾아오고 `createUserDetails()` 메소드를 통해서  `CustomUserDetail` 형태로 반환한다
- 로그인 요청이 들어왔을 때 `CustomUserDetailsService` 에서 만든 `loadUserByUsername` 메서드가 실행된다
    - 이를 통해 `principal`을 `CustomUserDetail` 형태로 강제할 수 있다

**JwtAuthenticationEntryPoint**

```java
/**
 * 인증되지 않는 요청 오류 처리
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        setErrorResponse(response, authException);
    }

    private void setErrorResponse(HttpServletResponse res, RuntimeException e) throws IOException {
        ApiResponseResult<String> result = ApiResponseResult.failure(UNAUTHORIZED_EXCEPTION);
        ObjectMapper objectMapper = new ObjectMapper();
        String resultSrt = objectMapper.writeValueAsString(result);
        
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(resultSrt);
        res.setStatus(SC_UNAUTHORIZED);
        res.flushBuffer();
    }
}
```

- 인증되지 않는 요청에 관해서 처리하는 클래스이다
- `setErrorResponse()` 동일한 결과 형태를 반환하기 위해 커스텀한 `response` 형태로 반환해준다


- 인증 예외 발생 후 `AuthenticationEntryPoint`를 구현해서 오류 처리를 한다

**JwtExceptionFilter**

```java
/**
 * Security 인증/인가 과정에서 생기는 오류 처리
 */
@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void setErrorResponse(HttpServletResponse res, ApiException e) throws IOException {
        ApiResponseResult<String> result = ApiResponseResult.failure(e.getException());
        String resultSrt = objectMapper.writeValueAsString(result);

        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write(resultSrt);
        res.setStatus(SC_BAD_REQUEST);
        res.flushBuffer();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response); // go to 'JwtAuthenticationFilter'
        } catch (ApiException e) {
            log.error("ApiException : {}", e.getMessage());
            setErrorResponse(response, e);
        }
    }
}
```

- `filterChain.doFilter()`에서 발생하는 모든 오류처리를 하는 필터를 추가했다
- `setErrorResponse()` 동일한 결과 형태를 반환하기 위해 커스텀한 `response` 형태로 반환해준다


인증/권한 서버에서 크게 필요한 기능들을 구현해야 할 메소드들을 인터페이스로 추상화 했다

**AuthService**

```java
public interface AuthService {
    /**
     * 로그인
     * POST
     *
     * @param signup id, pw 로그인 정보
     * @return TOKEN 값
     */
    AuthResponseDTO.Token signIn(AuthRequestDTO.SignIn signup);
    /**
     * 회원가입
     * POST
     *
     * @param signup 회원가입 필요한 정보
     */
    ApiResponseResult<Void> signUp(AuthRequestDTO.Signup signup);
    /**
     * ID 중복 체크
     *
     * @param id the id
     * @return 중복 여부
     */
    ApiResponseResult<Boolean> checkDuplicateId(String id);
}
```

**UserService**

```java
/**
 * 인증/인가 체크가 필요한 서비스
 */
public interface UserService {
    /**
     * 멤버 정보 조회
     * GET
     *
     * @return 멤버 INFO 값
     */
    UserResponseDTO.UserInfo getMemberInfo(String userId);
    /**
     * 로그아웃
     * GET
     * Redis에 값 소멸, 부여한 TOKEN 값의 유효성 사라짐
     *
     * @return 성공 여부
     */
    ApiResponseResult<Void> signOut();
    /**
     * 토큰 재발행
     * POST
     * @return TOKEN 값
     */
    AuthResponseDTO.Token reissueToken();
    /**
     * 유저 정보 변경
     * PUT
     *
     * @param updateUserInfo 유저 정보 변경 정보
     * @return 성공 여부
     */
    ApiResponseResult<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, String userId);
    /**
     * 비밀번호 변경
     * PUT
     *
     * @param rawPassword 회원가입 필요한 정보
     * @return 성공 여부
     */
    ApiResponseResult<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword, String userId);
}
```

- 앞서 Config 파일에서 url 기반의 인증/인가 검사를 하기 때문에 `AuthService`는 인증이 필요 없는 `UserService`는 인증/인가가 필요한 기능들로 구성했다
    
    

**User**

```java
@Getter
@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "USER_SEQ")
    private Long seq;

    @NotBlank
    @Column(name = "ID", nullable = false, unique = true)
    private String id;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "AGE", nullable = false)
    private int age;

    @Column(name = "SEX", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeEnum sex;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private TypeEnum status;
    
    @Column(name = "ROLE")
    private RoleEnum role;
}
```

### **AuthServiceImpl**

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Override
    public AuthResponseDTO.Token signIn(AuthRequestDTO.SignIn signIn) {
        //1. Login ID/PW 를 기반으로 Authentication 객체 생성, 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = signIn.toAuthentication();
        //2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        Authentication authentication = null;
        try {
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new ApiException(BAD_CREDENTIALS_EXCEPTION);
        }

        CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
        //3. 인증 정보를 기반으로 JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()), ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtTokenProvider.generateToken(new JwtTokenProvider.PrivateClaims(user.getId(), user.getRole()), REFRESH_TOKEN_EXPIRE_TIME);

        //5.레디스에 token 값 넣기
        Map<String, Object> hashMap = RedisDTO.Token.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .expiredDateTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build().convertMap();
        redisHandler.setHashData(user.getId(), hashMap, REFRESH_TOKEN_EXPIRE_TIME);
				//4. DTO로 반환한다				
        return AuthResponseDTO.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public ApiResponseResult<Void> signUp(AuthRequestDTO.Signup signup) {
        signup.setPasswordEncoding(passwordEncoder); //5.비밀번호를 인코딩한다 
        User savedUser = userRepository.save(signup.toUserEntity()); 
        if(savedUser.getSeq() < 0) {
            throw new ApiException(DATABASE_INSERT_ERROR); //6.반환받은 User의 seq가 0보다 작을시 저장오류 발생
        }
        return ApiResponseResult.success();
    }

    @Override
    public ApiResponseResult<Boolean> checkDuplicateId(String id) {
        boolean existUser = userRepository.existsById(id);
        if(existUser) {
            throw new ApiException(DUPLICATION_VALUE_IN_DATABASE_ERROR);
        }
        return ApiResponseResult.success(true);
    }
}
```

### 로그인 구현

**AuthRequestDTO**

```java
public class AuthRequestDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignIn {
        @NotBlank
        @Pattern(regexp = "^[a-z]+[a-z0-9]{5,19}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
        private String userId;

        @NotBlank(message = "비밀번호를 입력 해주세요")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(userId, password);
        }
    }
  }
```

1. userId와 password로 `UsernamePasswordAuthenticationToken` 객체를 반환해준다  
2. `AuthenticationManagerBuilder` 는 따로 설정을 해주지 않아도 어플리케이션 구동 시 Spring의 auto-configuration을 통해 자동으로 의존성이 주입되 간편한 사용이 가능하다
    - `AuthenticationManagerBuilder.getObject()`메서드를 통해 `AuthenticationManager`로 실제 인증을 수행한다
        
        ![image](https://github.com/user-attachments/assets/c190bd0a-cf81-4fbc-bf8d-98685cdf9b8f)

        
        - `AuthenticationManger`을 구현한 `ProviderManger`가 검증을 시도한다
        - `DaoAuthenticationProvider`가 검증을 위해 `loadUserbyUsername`() 메소드를 호출한다
        - ![image](https://github.com/user-attachments/assets/4adf12a3-e1a4-4c33-8eb6-1f4570f5e1d3)

        
        - `CustomUserDetailsService`에서 재구현한 `loadUserByUsername()`을 호출하여 DB로 부터 User 정보를 가져간다
4. `jwtTokenProvider.generateToken()` 내부클래스인 `PrivateClaims` 클래스 형태로 인자값을 넘겨Jwt 토큰 형태의 `AccessToken`과  `RefreshToken`을 반환 받는다 
5. DTO 형태로 AccessToken, RefreshToken, 발급 시간을 반환한다 
    - **결과값을 DTO로 변환하는 이유**
        - 데이터를 반환할 때 DB에서 받은 Entity값을 그대로 반환하게 되면 외부에서 어플리케이션의 내부 구조가 *노출되는 위험성*이 있다. 또한 Entity가 내부 결정에 따라 변경되면 클라이언트 입장에서도 변경해야 하는 불편함이 있기 때문에 **DTO로 일정한 형태를 전달해주는 것이 좋다**
6. 로그인에 성공한 유저의 RefreshToken 값을 `Redis`에 저장한다

**RedisHandler**

```java
@Component
@RequiredArgsConstructor
public class RedisHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setData(String key, String value, Long expiredTime){
        redisTemplate.opsForValue().set(key, value, expiredTime, MILLISECONDS);
    }

    public void setHashData(String key, Map<String, Object> value, Long expiredTime){
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key, value);
        redisTemplate.expire(key, expiredTime, MILLISECONDS);
    }

    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getHashData(String key, String hashKey){
        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}

```

- Redis에 값을 쉽게 저장, 수정 및 삭제를 하기 위해 `RedisTemplate`을 사용했다
- 데이터를 저장 및 조회하기 위한 메소드를 구현했다

**로그인 테스트**

**AuthServiceTest**

```java
@Slf4j
@SpringBootTest
@Transactional
class AuthServiceTest {
    @Autowired
    AuthServiceImpl authService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User newUser = User.builder()
                .id("bhkim62")
                .password(passwordEncoder.encode("test1234"))
                .name("박병호")
                .role(USER)
                .age(35)
                .sex(M)
                .build();
        em.persist(newUser); 
        em.flush(); //1.테스트가 실행되기 전에 user을 DB에 저장한다 
    }

    @AfterEach
    void afterEach() {
        em.clear(); //4.테스트가 끝날 때 마다 영속성 컨텍스트를 초기화한다 
    }

    @Test
    void 로그인에_정상적으로_성공() {
        //given
        SignInRequest request = new SignInRequest("bhkim62", "test1234");
        AuthRequestDTO.SignIn loginUser = AuthRequestDTO.SignIn.builder()
                .userId(request.id())
                .password(request.password())
                .build(); //2.로그인을 요청하는 request를 만들어 로그인을 시도한다
        //when
        AuthResponseDTO.Token result = authService.signIn(loginUser);

        //then
        assertThat(result).extracting("accessToken").isNotNull(); //3.전달 받은 accessToken과 refreshToken이 있는지 확인한다
        assertThat(result).extracting("refreshToken").isNotNull();
    }
```

1. 테스트가 실행되기 전에 가상의 User 객체를 만들고 DB에 저장했다 
2. 로그인을 요청하는 request를 만들어 `authService.signIn()`인자값으로 넘겨준다
3. 전달 받은 accessToken과 refreshToken이 있는지 확인한다
4. 한 테스트가 끝날 때마다 영속성 컨텍스트를 초기화한다 

### 회원가입 구현

**AuthServiceImpl**

```java
public class AuthRequestDTO {
		@Getter
	  @Builder
	  @NoArgsConstructor
	  @AllArgsConstructor
	  public static class Signup {
	      @NotBlank //1.Validation 검사
	      @Pattern(regexp = "^[A-Za-z\\d]{6,18}$", message = "영문과 숫자를 혼합한 6~18자리를 입력해야 합니다")
	      private String userId;
	
	      @NotBlank(message = "비밀번호를 입력 해주세요")
	      private String password;
	
	      @NotBlank
	      @Pattern(regexp = "^[a-zA-Zㄱ-힣][a-zA-Zㄱ-힣 ]*$", message = "이름은 영문 한글로 이뤄져야 합니다")
	      private String name;
	
	      @Min(value = 1, message = "나이는 1살 이상 150세 이하이여야 합니다")
	      @Max(value= 150, message = "나이는 1살 이상 150세 이하이여야 합니다")
	      private int age;
	
	      @NotNull
	      private TypeEnum sex;
	
	      private String phoneNumber;
	
	      public User toUserEntity() { //2.DTO로 전달 받은 데이터를 Entity 형태로 변환한다
	          return User.builder()
	                  .id(this.userId)
	                  .name(this.name)
	                  .password(this.password)
	                  .sex(this.sex)
	                  .age(this.age)
	                  .phoneNumber(this.phoneNumber)
	                  .role(USER)
	                  .status(PENDING)
	                  .build();
	      }
				
	      public void setPasswordEncoding(PasswordEncoder passwordEncoder) {
	          this.password = passwordEncoder.encode(password);
	      }
  }
```

1. 클라리언트로 받은 데이터 유효성 검사를 위해 `jakarta.validation` 패키지를 사용했다 
2. DTO 형태로 전달받은 request를 DB에 저장하기 위해 User Entity 형태로 변환한다 

**회원가입 TEST**

```java
 @Test
void 회원가입() {
    //given
    SignUpRequest signup = new SignUpRequest("bhkim63", "test1234", "김병호", 30, M, "01029292020");
    AuthRequestDTO.Signup signupDTO = AuthRequestDTO.Signup.builder()
            .userId(signup.id())
            .password(signup.password())
            .name(signup.name())
            .age(signup.age())
            .sex(signup.sex())
            .phoneNumber(signup.phoneNumber())
            .build();
    
    // when
    authService.signUp(signupDTO);
    User user = userRepository.findById(signup.id()).orElseThrow();

    // then
    // 반환된 결과와 id가 같은지 비교
    assertThat(user).extracting("id").isEqualTo(signup.id());
}
```

- 회원가입 메소드 실행 후 `userRepository.findById()`를 통해서 request으로 보낸 userId 값으로 DB에 조회한다


### UserServiceImpl

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;
    private final AuthFacade authFacade;

    @Override
    public UserResponseDTO.UserInfo getMemberInfo(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        return user.toDto(user);
    }

    @Override
    public ApiResponseResult<Void> signOut() {
        //ATK에 문제가 없을 때 redis에 값 삭제
        String userId = authFacade.getCurrentUserId(); //1.요청자의 id를 반환받는다
        redisHandler.deleteData(userId); //2.Redis에 저장되어 있는 회원 정보를 삭제한다
        SecurityContextHolder.clearContext();
        return ApiResponseResult.success(null);
    }

    @Override
    public AuthResponseDTO.Token reissueToken() {
        String userId = authFacade.getCurrentUserId();
        String refreshToken = jwtTokenProvider.getTokenInHeader(); //3.Header로 전달된 Refresh 값을 받는다
        // refreshToken in Redis
        String redisRefreshToken = redisHandler.getHashData(userId, REDIS_KEY_REFRESH_TOKEN); //4.redis에 저장된 Refresh 값을 받는다

        JwtTokenProvider.PrivateClaims privateClaims = jwtTokenProvider.parseRefreshToken(refreshToken, redisRefreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(privateClaims, ACCESS_TOKEN_EXPIRE_TIME);
        String newRefreshToken = jwtTokenProvider.generateToken(privateClaims, REFRESH_TOKEN_EXPIRE_TIME);
        //5.재발급 받은 토큰값을 Redis에 저장하고 반환한다 
        Map<String, Object> hashMap = RedisDTO.Token.builder()
                .userId(userId)
                .refreshToken(newRefreshToken)
                .expiredDateTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build().convertMap();
        redisHandler.setHashData(userId, hashMap, REFRESH_TOKEN_EXPIRE_TIME);
        return AuthResponseDTO.Token.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public ApiResponseResult<Boolean> updateUser(UserRequestDTO.UpdateUserInfo updateUserInfo, String userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        findUser.update(findUser.toEntity(updateUserInfo));

        return ApiResponseResult.success(true);
    }

    @Override
    @Transactional
    public ApiResponseResult<Boolean> changePassword(UserRequestDTO.UpdatePassword rawPassword, String userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ApiException(ILLEGAL_ARGUMENT_ERROR));
        // 이전 패스워드와 같은지 체크
        if (passwordEncoder.matches(rawPassword.getPassword(), findUser.getPassword())) {
            throw new ApiException(ILLEGAL_PASSWORD);
        }

        String encodePassword = passwordEncoder.encode(rawPassword.getPassword());
        findUser.updatePassWord(encodePassword);
        return ApiResponseResult.success(true);
    }
}
```

### 로그아웃
    - `SecurityContext`에 `Authentication`가 존재할 경우 userId 값을 반환받는다.
1. 반환 받은 id를 key 값으로 `Redis`에 저장되어 있는 회원 값을 삭제하고 `SecurityContext` 또한 초기화 한다

### 토큰 재발급

- 토큰이 유효하지 않을 경우 예외를 반환하고 예외를 반환 받은 클라이언트는 **토큰 재발급 API를 요청한다**
- 최초 로그인시 저장했던 `RefreshToken`을 서버로 보내 서버에서는 `RefreshToken`의 진위 여부 판단 후 새로운 토큰값을 반환한다
1. Header로 전달된 Refresh 값을 추출한다 
    - body에 값을 받을 수 있지만 이전 `AccessToken`과 마찬가지로 통일성을 위해서 Header 값을 전달 받게 했다
2. `Redis`에 저장된 RefreshToken 값을 조회한다
    - Redis에서 꺼내온 Token 값과 클라이언트가 전달해준 Token 값이 다르면 예외가 발생한다
    - **JwtTokenProvider.java**
    
    ```java
    //토큰 재발급에서 쓰임 - Refresh Token이 유효한지 확인
    public PrivateClaims parseRefreshToken(String refreshToken, String redisRefreshToken) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new ApiException(INVALID_TOKEN_VALUE);
        }
        return jwtHandler.parseClaims(refreshToken).map(this::convert).orElseThrow();
    }
    
     private PrivateClaims convert(Claims claims) {
            return new PrivateClaims(claims.get(USER_ID, String.class), RoleEnum.valueOf(claims.get(ROLE, String.class)));
    ```
    
    - JwtHandler.java
    
    ```java
    public Optional<Claims> parseClaims(String token) {
      return Optional.of(Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody());
    }
    ```
    
    - token으로 Claims을 파싱한다
    - 이후 `PrivateClaims` 형태로 반환한다
3. 새로 만들어진 Token값을 Redis에 저장하고 클라이언트에 반환한다 

**토큰 재발급 TEST**

```java
@Test
void 토큰재발급_성공() throws Exception {
    // given
    SignInRequest request = new SignInRequest("bhkim62", "test1234");
    AuthRequestDTO.SignIn loginUser = AuthRequestDTO.SignIn.builder()
            .userId(request.id())
            .password(request.password())
            .build();

    AuthResponseDTO.Token token = authService.signIn(loginUser);  //1.
    String accessToken = token.getAccessToken();
    String refreshToken = token.getRefreshToken();
		//2.accessToken으로 Authentication 값을 가져온다
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserRequestDTO.RefreshToken rt = UserRequestDTO.RefreshToken.builder()
            .refreshToken(refreshToken)
            .build();
		//3.헤더에 토큰 설정
    // MockHttpServletRequest 생성
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    // 헤더 값 추가
    mockRequest.addHeader("Authorization", refreshToken);

    // MockHttpServletRequest를 ServletRequestAttributes에 설정
    ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);

    // RequestContextHolder에 해당 요청 속성 설정
    RequestContextHolder.setRequestAttributes(attributes, true);
		//4.토큰은 생성을 위한 시간 차이 
    Thread.sleep(1000);

    // when
    AuthResponseDTO.Token reissueToken = userService.reissueToken();

    // then
    assertThat(accessToken).isNotEqualTo(reissueToken.getAccessToken());
    assertThat(refreshToken).isNotEqualTo(reissueToken.getRefreshToken());
	  System.out.println("accessToken = " + accessToken);
    System.out.println("reissueToken.getAccessToken() = " + reissueToken.getAccessToken());
}
```

1. 토큰을 재발급 받기 위해서 최초 로그인을 했을 때 전달 받는 토큰 값이 있어야 하기 때문에 `authService`에서 구현한 `signIn()` 메소드를 호출한다
2. accessToken을 인자값으로 전달해 `Authentication` 객체를 반환받는다
    
    ```java
    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = jwtHandler.parseClaims(token).orElseThrow();
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" +
                claims.get(ROLE)));
    
        User user = User.builder()
                .id((String) claims.get(USER_ID))
                .role(RoleEnum.valueOf((String) claims.get(ROLE)))
                .build();
        // UserDetails 객체를 만들어서 Authentication 리턴
        CustomUserDetail principal = new CustomUserDetail(user);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    ```
    
    - 인자로 넘어온 token값을 파싱을 통해 claims을 추출한다
    - `UsernamePasswordAuthenticationToken`가 `Authentication`을 상속하고 있기 때문에 `Authentication`의 인자값인 principal, credentials, authorities을 각각 전달한다
3. `RefreshToken`을 헤더에서 추출해오기 때문에 강제로 Header에 넣어야한다
    - `RequestContextHolder`는 Spring 컨텍스트에서 `HttpServletRequest`에 직접 접근 할 수 있도록 도와주는 역할을 한다.
4. 토큰은 현재 시간값으로 생성되는 값이 다르기 때문에 1초 여유를 두고 `reissueToken()` 메소드를 실행한다 


- 테스트 결과 기존 AccessToken값과 새로 받은 값이 다른것을 볼 수 있다
