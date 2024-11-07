package com.utng.integradora.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@Slf4j
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "dbEntityManagerFactory", transactionManagerRef = "dbTransactionManager", basePackages = {"com.utng.integradora.repository"})

public class DataSourceConfig {
    @Value("${spring.primary.datasource.username}")
    private String usuarioDB;
    @Value("${spring.primary.datasource.password}")
    private String passwordDB;
    @Value("${spring.primary.datasource.url}")
    private String url08;
    @Value("${db-connection-maxidle}")
    private Integer maxIdle;
    @Value("${db-connection-minidle}")
    private Integer minIdle;

@Bean(name = {"dbDataSource"})
@ConfigurationProperties(prefix = "db.datasource")
public DataSource dbDataSoruce(){
    HikariConfig config = new HikariConfig();
    try {
        config.setJdbcUrl(url08);
        config.setUsername(usuarioDB);
        config.setPassword(passwordDB);
        // Ajustes de tamaño en pool
            config.setMaximumPoolSize(maxIdle); //número máximo de conexiones en el pool
            config.setMinimumIdle(minIdle); //Número minimo de conexiones inactivas en el pool
        //AJUSTES DE TIEMPO
            config.setIdleTimeout(30000); //Tiempo de milisegundo que una conexión puede estar inactiva antes de ser cerrada
            config.setMaxLifetime(1800000); //Tiempo maximo de vida de una conexion en milisegundos (30 minutos). Establece un limite para evitar problemas con conexiones demasiado viejas
        //AJUSTES DE CONEXION
            config.setConnectionTestQuery("SELECT 1"); //Consulta simple para validar conexiones
            config.setPoolName("DataSourceConfig");
        //AJUSTES OPCIONALES
            config.setConnectionTimeout(30000); //Tiempo maximo en milisegundos para obtener una conexion con el pool
            config.setLeakDetectionThreshold(15000); //Tiempo en milisegundos para detectar conexiones con posibles fugas
            } catch (Exception e){
                log.error("Error connecting to the database DataSource: " + e.getMessage());
        }
        return new HikariDataSource(config);
    }

    @Primary
    @Bean(name = "dbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dbEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        try{
            em.setDataSource(dbDataSoruce());
            em.setPackagesToScan("com.utng.integradora.entity");
            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            em.setJpaVendorAdapter(vendorAdapter);
            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.hbm2ddl.auto", "none");
            properties.put("hibernate.show-sql", false);
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("jakarta.persistence.query.timeout", 680000);
            em.setJpaPropertyMap(properties);
        } catch (Exception e){
            log.error("Error connecting to the database ofDataSource -- ofEntityManagerFactory: " + e.getMessage());
        }
        return em;

    }

    @Bean(name = {"dbTransactionManager"})
    public PlatformTransactionManager dbTransactionManager(@Qualifier("dbEntityManagerFactory")EntityManagerFactory dbEntityManagerFactory) {
        return new JpaTransactionManager(dbEntityManagerFactory);
    }
}