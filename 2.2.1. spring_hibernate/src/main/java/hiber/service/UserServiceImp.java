package hiber.service;

import hiber.dao.UserDao;
import hiber.model.Car;
import hiber.model.User;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.hibernate5.HibernateTransactionManager;


import javax.persistence.Query;
import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @Override
    public void add(User user) {
        userDao.add(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Override
    public User findUserByCar(String model, int series) {
        Session session = sessionFactory.openSession();
        Transaction tx1 = session.beginTransaction();
        Query query = session.createQuery("from Car where model = :Model and series = :Series");
        query.setParameter("Model", model);
        query.setParameter("Series", series);

        Car singleResult = (Car) query.getSingleResult();
        session.close();
        return singleResult.getUser();

    }

    @Override
    public void cleanUsersTable() {
        Session session = sessionFactory.openSession();
        Transaction tx1 = session.beginTransaction();
        try {
            session.createSQLQuery("DELETE FROM users WHERE ID > 0;").executeUpdate();
            session.createSQLQuery("DELETE FROM car WHERE ID > 0;").executeUpdate();
            tx1.commit();
        } catch (HibernateException e) {
            tx1.rollback();
        } finally {
            session.close();
        }
    }


}
