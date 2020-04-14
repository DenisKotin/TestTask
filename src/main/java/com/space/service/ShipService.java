package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Map;

public interface ShipService {
    List<Ship> getAllShips(Map<String, String> criteria, Pageable pageable);
    Integer getCount(Map<String, String> criteria);
    Ship insert(Ship ship);
    Ship findById(Long id);
    boolean deleteById(Long id);
    Ship update(Long id, Ship ship);
}

