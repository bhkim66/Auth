package com.bhkim.auth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
    basePackages = "net.kpnp.jpa.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTxManager"
)
//@PropertySource("classpath:/application.properties")
public class DBConfig {

    @Bean(name = "hikariConfig")
    @ConfigurationProperties(prefix="spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    /*
    * datasource
    */
    @Bean(name= "dataSource")
    public HikariDataSource dataSource(@Qualifier("hikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name= "jpaDataSource")
    public HikariDataSource jpaDataSource(@Qualifier("hikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    /**
     * sessionfactory
     */
//    @Bean(name= "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        sessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:*/mapper/*.xml")); //mapper path
//        sessionFactoryBean.setTypeAliasesPackage("net.kpnp.domain");
//        Objects.requireNonNull(sessionFactoryBean.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true); //camelCase
//        Objects.requireNonNull(sessionFactoryBean.getObject()).getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
//        Objects.requireNonNull(sessionFactoryBean.getObject()).getConfiguration().setCallSettersOnNulls(true);
//
//        return sessionFactoryBean.getObject();
//    }

    /**
     * sqlsession
     */
//    @Bean(name= "sqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }

    /**
     * jpa entityManagerFactory
     */
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory(@Qualifier("jpaDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.bhkim.auth.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.show_sql", false);  // sql은 log4j로 출력 org.hibernate.SQL=DEBUG
        properties.put("hibernate.globally_quoted_identifiers", true);  // 예약어 컬럼명 사용 허용
        properties.put("hibernate.use_sql_comments", true);
        properties.put("hibernate.highlight_sql", true);
        properties.put("hibernate.default_batch_fetch_size", 100);
        em.setJpaPropertyMap(properties);
        em.afterPropertiesSet();

        return em.getObject();
    }

    /**
     * transaction manager
     */
    @Bean(name= "txManager")
    @Primary
    public PlatformTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true); // nested

        return dataSourceTransactionManager;
    }

    @Bean(name= "jpaTxManager")
    public PlatformTransactionManager jpaTxManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);

        return jpaTransactionManager;
    }

}
