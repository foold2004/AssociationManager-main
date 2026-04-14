package com.rabbiter.association.service;

import com.rabbiter.association.entity.InteractionComments;

import java.util.List;
import java.util.Map;

public interface InteractionCommentsService extends BaseService<InteractionComments, String> {

    List<Map<String, Object>> getListByInteractionId(String interactionId);
}
