package com.braidsencurls.event_bot.repositories;

import java.util.List;
import java.util.Set;

public interface EventSubscribersRepository {
    void save(Long chatId);
    boolean delete(Long chatId);
    Set<Long> findAll();
}
