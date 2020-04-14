package com.space.service.impl;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShipServiseImpl implements ShipService {

    final private int MAX_STR_LENGTH = 50;
    final private long MIN_DATA_PROD = 2800;
    final private long MAX_DATA_PROD = 3019;
    final private long MIN_CREW_SIZE = 1;
    final private long MAX_CREW_SIZE = 9999;
    final private double MIN_SPEED = 0.01;
    final private double MAX_SPEED = 0.99;
    final private int ROUND_TO = 2;
    final private int SHIP_COEF = 80;



    @Autowired
    private ShipRepository shipRepository;

    private boolean checkParam(String value) {
        if (value == null) return false;
        return value.trim().length() != 0  && value.length() <= MAX_STR_LENGTH;
    }

    private boolean checkParam(Double value){
        if (value == null) return false;
        long val = Math.round(value*Math.pow(10, ROUND_TO));
        return  val >= MIN_SPEED*Math.pow(10, ROUND_TO) &&
                val <= MAX_SPEED*Math.pow(10, ROUND_TO) ;
    }

    private boolean checkParam(Number val, Long minVal, Long maxVal){
        if (val == null) return false;
        Long value = val.longValue();
        return  (minVal == null || minVal <=  value) &&  (maxVal == null || maxVal >=  value);
    }

    private Double calcRating(Ship ship){
       double res = (SHIP_COEF * ship.getSpeed()* (ship.getUsed()? 0.5:1))/(MAX_DATA_PROD - getYear(ship) +1);
       return Math.round(res * Math.pow(10, ROUND_TO)) / Math.pow(10, ROUND_TO);
    }

    private int getYear(Ship ship){
        if (ship.getProdDate() == null) return -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        return  calendar.get(Calendar.YEAR);
    }

    @Override
    public List<Ship> getAllShips(Map<String, String> criteria, Pageable pageable) {
        return shipRepository.findAll(new ShipSpec(criteria), pageable).getContent();
    }

    @Override
    public Integer getCount(Map<String, String> criteria) {
        return  shipRepository.findAll(new ShipSpec(criteria)).size();
    }

    @Override
    public Ship findById(Long id) {
        Optional<Ship> optionalShip = shipRepository.findById(id);
        return  optionalShip.isPresent() ? optionalShip.get() : null;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = findById(id) != null;
        if(result) {
            shipRepository.deleteById(id);
        }
        return result;
    }

    @Override
    public Ship insert(Ship ship) {
        if (checkParam(ship.getName()) && checkParam(ship.getPlanet()) &&
                ship.getShipType() != null && checkParam(ship.getSpeed()) &&
                checkParam(ship.getCrewSize(),MIN_CREW_SIZE, MAX_CREW_SIZE ) &&
                checkParam(getYear(ship), MIN_DATA_PROD, MAX_DATA_PROD)){
            if (ship.getUsed() == null){
                ship.setUsed(false);
            }
            ship.setRating(calcRating(ship));
            shipRepository.saveAndFlush(ship);
            return ship;
        }
        return null;
    }

    @Override
    public Ship update(Long id, Ship ship) {
        Ship newShip = findById(id);

        if (newShip == null) {
            return null;
        }

        if (checkParam(ship.getName()) ){
            newShip.setName(ship.getName());
        } else if (ship.getName() != null) return null;

        if(checkParam(ship.getPlanet())){
            newShip.setPlanet(ship.getPlanet());
        } else if (ship.getPlanet() != null) return null;

        if (ship.getShipType() != null){
            newShip.setShipType(ship.getShipType());
        }

        if(checkParam(ship.getCrewSize(),MIN_CREW_SIZE, MAX_CREW_SIZE)){
            newShip.setCrewSize(ship.getCrewSize());
        } else if (ship.getCrewSize() != null) return null;

        if (checkParam(getYear(ship), MIN_DATA_PROD, MAX_DATA_PROD)){
            newShip.setProdDate(ship.getProdDate());
        } else if (ship.getProdDate() != null) return null;

        if(checkParam(ship.getSpeed())){
            newShip.setSpeed(ship.getSpeed());
        }
        if (ship.getUsed() != null){
            newShip.setUsed(ship.getUsed());
        }

        newShip.setRating(calcRating(newShip));
        shipRepository.saveAndFlush(newShip);
        return newShip;
    }
}
