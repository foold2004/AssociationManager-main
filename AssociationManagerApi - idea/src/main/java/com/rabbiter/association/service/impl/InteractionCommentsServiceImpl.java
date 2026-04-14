package com.rabbiter.association.service.impl;

import com.rabbiter.association.dao.InteractionCommentsDao;
import com.rabbiter.association.entity.InteractionComments;
import com.rabbiter.association.service.InteractionCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("interactionCommentsService")
public class InteractionCommentsServiceImpl implements InteractionCommentsService {

    @Autowired
    private InteractionCommentsDao interactionCommentsDao;

    @Override
    @Transactional
    public void add(InteractionComments interactionComments) {
        interactionCommentsDao.insert(interactionComments);
    }

    @Override
    @Transactional
    public void update(InteractionComments interactionComments) {
        interactionCommentsDao.updateById(interactionComments);
    }

    @Override
    @Transactional
    public void delete(InteractionComments interactionComments) {
        interactionCommentsDao.deleteById(interactionComments);
    }

    @Override
    public InteractionComments getOne(String id) {
        return interactionCommentsDao.selectById(id);
    }

    @Override
    public List<Map<String, Object>> getListByInteractionId(String interactionId) {
        return interactionCommentsDao.qryListByInteractionId(interactionId);
    }
}
