package com.space.service.impl;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.*;



class ShipSpec implements Specification<Ship> {

    private final List<SearchCriteria>  list;

    private Integer getYear(Object o){
        try {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime((Date) o);
            return dateCalendar.get(Calendar.YEAR);
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public ShipSpec(Map<String, String> criteria) {
        list = new ArrayList<>();

        add( "name", SearchOperation.LIKE, criteria.get("name"));
        add( "planet", SearchOperation.LIKE, criteria.get("planet"));
        add( "speed", SearchOperation.GREATER, criteria.get("minSpeed"));
        add( "speed", SearchOperation.LESS, criteria.get("maxSpeed"));
        add( "crewSize", SearchOperation.GREATER, criteria.get("minCrewSize"));
        add( "crewSize", SearchOperation.LESS, criteria.get("maxCrewSize"));
        add( "rating", SearchOperation.GREATER, criteria.get("minRating"));
        add( "rating", SearchOperation.LESS, criteria.get("maxRating"));

        try {
            if (criteria.containsKey("isUsed")){
                add("isUsed", SearchOperation.EQUAL, Boolean.valueOf(criteria.get("isUsed")));
            }

            if (criteria.containsKey("shipType")){
                add("shipType", SearchOperation.EQUAL, ShipType.valueOf(criteria.get("shipType")));
            }
            if (criteria.containsKey("after")){
                add( "prodDate", SearchOperation.GREATER_DATA, new Date(Long.parseLong(criteria.get("after"))));
            }

            if (criteria.containsKey("before")){
                add( "prodDate", SearchOperation.LESS_DATA,  new Date(Long.parseLong(criteria.get("before"))));
            }
        }
        catch (Exception  e){
            e.printStackTrace();
        }



    }

    private void add(String nameField, SearchOperation operation, Object value) {
        list.add(new SearchCriteria(nameField, operation, value) );
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> result = new ArrayList<>();
        for (SearchCriteria criteria : list) {
            if (criteria.getValue() == null) continue;
            if (criteria.getOperation().equals(SearchOperation.GREATER)) {
                result.add(builder.greaterThanOrEqualTo(root.get(criteria.getNameField()),
                        criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.LESS)) {
                result.add(builder.lessThanOrEqualTo(root.get(criteria.getNameField()),
                        criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                result.add(builder.equal(root.get(criteria.getNameField()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.LIKE)) {
                result.add(builder.like(builder.lower(root.get(criteria.getNameField())),
                        "%" + criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_DATA)) {
                result.add(builder.greaterThanOrEqualTo(
                        builder.function("year", Integer.class, root.get(criteria.getNameField())),
                        getYear(criteria.getValue())));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_DATA)) {
                result.add(builder.lessThanOrEqualTo(
                        builder.function("year", Integer.class, root.get(criteria.getNameField())),
                        getYear(criteria.getValue())));
            }
        }
            return builder.and(result.toArray(new Predicate[0]));
    }


}
