package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AIWordDefinitionResponse;

public interface AIDefinitionService {

    AIWordDefinitionResponse getDefinition(String word);
}
