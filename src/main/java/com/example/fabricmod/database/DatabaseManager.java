package com.example.fabricmod.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

@Slf4j
public class DatabaseManager {
    private static EntityManagerFactory emf;

    public static void initialize(String jdbcUrl, String username, String password) {
        try {
            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", jdbcUrl);
            configuration.setProperty("hibernate.connection.username", username);
            configuration.setProperty("hibernate.connection.password", password);
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");

            configuration.addAnnotatedClass(MessageEntity.class);

            emf = configuration.buildSessionFactory();

            log.info("Database initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize database", e);
        }
    }

    public static void saveMessage(UUID playerUuid, String text) {
        if (emf == null) {
            log.error("EntityManagerFactory is not initialized");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            MessageEntity message = new MessageEntity(playerUuid, text);
            em.persist(message);

            em.getTransaction().commit();
            log.info("Message saved: {} from player {}", text, playerUuid);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.error("Failed to save message", e);
        } finally {
            em.close();
        }
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("Database connection closed");
        }
    }
}
