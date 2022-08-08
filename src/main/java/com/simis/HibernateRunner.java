package com.simis;

import com.simis.entity.User;
import com.simis.entity.Word;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class HibernateRunner {

    public void voidAddUser(int id, String name, String login, String password) {
        Configuration config = new Configuration();
        config.addAnnotatedClass(User.class);
        config.configure();

        try (SessionFactory sessionFactory = config.buildSessionFactory();
        ) {
            Session session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            User admin = User.builder()
                    .id(id)
                    .name(name)
                    .login(login)
                    .password(password)
                    .build();
            session.persist(admin);
            session.getTransaction().commit();
        }
    }

    public static int getTableRowCount(Class tableEntity) {
        Configuration config = new Configuration();
        config.addAnnotatedClass(tableEntity);
        config.configure();

        try (SessionFactory sessionFactory = config.buildSessionFactory();
             Session session = sessionFactory.openSession();
        ) {
            session.beginTransaction();
            Query query = session.createNativeQuery(
                    "SELECT count(*) FROM sorted_words", Integer.class
            );
            return ((Number) query.getSingleResult()).intValue();
        }
    }

    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Word.class);
        config.configure();

        try (SessionFactory sessionFactory = config.buildSessionFactory();
             Session session = sessionFactory.openSession();
        ) {
            session.beginTransaction();

            int randomId = (int) (Math.random() * getTableRowCount(Word.class));
            Word word = session.get(Word.class, randomId);
            System.out.println(word.getName());

            session.getTransaction().commit();
        }
    }
}
