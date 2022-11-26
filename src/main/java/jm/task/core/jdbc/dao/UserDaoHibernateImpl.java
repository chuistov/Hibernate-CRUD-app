package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.model.User;
import org.hibernate.Transaction;
import java.util.List;

import static jm.task.core.jdbc.util.HibernateUtil.getSessionFactory;

public class UserDaoHibernateImpl implements UserDao {

    private static final UserDaoHibernateImpl userDaoHibernate = new UserDaoHibernateImpl();

    private UserDaoHibernateImpl() {
    }

    public static UserDaoHibernateImpl getUserDaoHibernate() {
        return userDaoHibernate;
    }

    @Override
    public void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS repository.user (
                    id bigint auto_increment primary key,
                    name varchar(100),
                    last_name varchar(100),
                    age tinyint);
                """;
        Transaction tx = null;
        try (var session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createSQLQuery(sql)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Error when creating table", e);
        }
    }

    @Override
    public void dropUsersTable() {
        String sql = """
                DROP TABLE IF EXISTS repository.user;
                """;
        Transaction tx = null;
        try (var session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createSQLQuery(sql)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Error when dropping table", e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        var user = new User(name, lastName, age);
        Transaction tx = null;
        try (var session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Error when saving user", e);
        }
        System.out.println("User с именем " + name + " добавлен в базу данных");
    }

    @Override
    public void removeUserById(long id) {
        String saveUserHql = """
                delete User where id = :param
                """;
        Transaction tx = null;
        try (var session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(saveUserHql)
                    .setParameter("param", id)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Error when removing user", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        String getAllUsersHql = """
                from User
                """;
        try (var session = getSessionFactory().openSession()) {
            return session.createQuery(getAllUsersHql, User.class)
                    .list();
        } catch (Exception e) {
            throw new DaoException("Error when getting users from table", e);
        }
    }

    @Override
    public void cleanUsersTable() {
        String sql = """
                TRUNCATE TABLE repository.user;
                """;
        Transaction tx = null;
        try (var session = getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createSQLQuery(sql)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new DaoException("Error when truncating table", e);
        }
    }
}
