package com.activebeancoders.fitness.data.hib.dao;

import com.activebeancoders.fitness.data.dao.ActivityDao;
import com.activebeancoders.fitness.data.entity.Activity;
import com.activebeancoders.fitness.data.search.ActivitySearchCriteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for moving Activity data in/out of MySQL.
 *
 * @author Dan Barrese
 */
@Repository
@Primary
public class ActivityDaoMySqlImpl implements ActivityDao {

    private static final Logger log = LoggerFactory.getLogger(ActivityDaoMySqlImpl.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Activity> findMostRecentActivities(int size) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Activity.QUERY_FIND_MOST_RECENT);
        query.setMaxResults(size);
        return query.list();
    }

    @Override
    @Transactional
    public Activity get(Object id) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Activity.QUERY_FIND_BY_ID);
        query.setParameter("id", Long.valueOf((String) id));
        Object result = query.uniqueResult();
        return result == null ? null : (Activity) result;
    }

    @Override
    @Transactional
    public boolean save(Activity activity) {
        sessionFactory.getCurrentSession().saveOrUpdate(activity);
        return true;
    }

    @Override
    @Transactional
    public boolean update(Activity activity, Class<?> jsonView) {
        sessionFactory.getCurrentSession().update(activity);
        return true;
    }

    @Override
    @Transactional
    public List<Activity> search(ActivitySearchCriteria activitySearchCriteria) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Activity.QUERY_FULL_TEXT_SEARCH);
        query.setParameter("criteria", activitySearchCriteria.getFullText());
        List<Object> results = query.list();
        if (results == null) {
            return null;
        }
        List<Activity> typedResults = results.stream().map(o -> (Activity) o).collect(Collectors.toList());
        return typedResults;
    }

    @Override
    @Transactional
    public Long findMaxId() {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Activity.QUERY_FIND_MAX_ID);
        Object id = query.uniqueResult();
        if (id == null) {
            return 0L;
        } else {
            Long maxId = ((BigInteger) id).longValue();
            return maxId;
        }
    }

}
