package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/rest/ships")
class ShipController {
    private final String PAGE_DEFAULT = "0";
    private final String PAGE_SIZE = "3";
    private final String PAGE_SORT = "ID";

    private ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public ResponseEntity<List<Ship>> getShips(@RequestParam Map<String, String> criteria,
            @RequestParam(value = "order" , defaultValue = PAGE_SORT) ShipOrder order,
            @RequestParam(value = "pageNumber" , defaultValue = PAGE_DEFAULT) int pageNumber,
            @RequestParam(value = "pageSize" , defaultValue = PAGE_SIZE) int pageSize){

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        List<Ship> ships = shipService.getAllShips(criteria, pageable);
        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @GetMapping(value = "/count")
    public ResponseEntity<Integer>  getCount(@RequestParam Map<String, String> criteria) {
        Integer count = shipService.getCount(criteria);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        Ship result = shipService.insert(ship);
        return result != null ? new ResponseEntity<>(result, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String string_id) {
        Long id = getId(string_id);
        if (id <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.findById(id);
        return ship != null ? new ResponseEntity<>(ship, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Ship> delShip(@PathVariable(value = "id") String string_id) {
        Long id = getId(string_id);
        if (id <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return shipService.deleteById(id) ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<Ship> updateShip(@PathVariable(value = "id") String string_id, @RequestBody Ship ship){
        Long id = getId(string_id);
        if (id <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//400
        }

        if (shipService.findById(id) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);//404
        }

        Ship result = shipService.update(id, ship);
        return result != null ? new ResponseEntity<>(result, HttpStatus.OK)
                              : new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    private long getId(String id) {
        try {
            return id == null ? -1 : Long.parseLong(id);
        }
        catch (Exception e){
            return -1;
        }
    }
}
