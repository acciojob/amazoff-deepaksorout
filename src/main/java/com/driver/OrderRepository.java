package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    private HashMap<String,Order> orderHashMap;
    private HashMap<String,DeliveryPartner> deliveryPartnerHashMap;
    private HashMap<String, List<String>> orderPartnerPair;
    private HashMap<String,String> ordertopartner;

    public OrderRepository() {
        orderHashMap=new HashMap<>();
        deliveryPartnerHashMap=new HashMap<>();
        orderPartnerPair=new HashMap<>();
        ordertopartner=new HashMap<>();
    }

    public void addOrder(Order order){
        orderHashMap.put(order.getId(),order);
    }
    public void addPartner(String partnerId){
        deliveryPartnerHashMap.put(partnerId, new DeliveryPartner(partnerId));
    }


    public void addOrderPartnerPair(String orderId, String partnerId){
        if(!orderPartnerPair.containsKey(partnerId)){
            List<String> list=new ArrayList<>();
            list.add(orderId);

            orderPartnerPair.put(partnerId,list);
        }
        else{
            List<String> list=orderPartnerPair.get(partnerId);
            list.add(orderId);

            orderPartnerPair.put(partnerId,list);
        }
        DeliveryPartner curr=deliveryPartnerHashMap.get(partnerId);
        curr.setNumberOfOrders(orderPartnerPair.get(partnerId).size());
        ordertopartner.put(orderId,partnerId);
    }
    public Order getOrderById(String orderId){
        if(!orderHashMap.containsKey(orderId)){
            return null;
        }
        return orderHashMap.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        if(!deliveryPartnerHashMap.containsKey(partnerId)){
            return null;
        }
        return deliveryPartnerHashMap.get(partnerId);
    }
    public Integer getOrderCountByPartnerId(String partnerId){
        if(!orderPartnerPair.containsKey(partnerId)){
            return 0;
        }
        return orderPartnerPair.get(partnerId).size();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        if(!orderPartnerPair.containsKey(partnerId)){
            return null;
        }
        return orderPartnerPair.get(partnerId);
    }
    public List<String> getallOrders(){
        List<String> list=new ArrayList<>();
        for(String s:orderHashMap.keySet()){
            list.add(s);
        }
        return list;
    }
    public Integer getCountOfUnassignedOrders(){
        Integer count=0;
        for(String s:orderHashMap.keySet()){
            if(!ordertopartner.containsKey(s)){
                count+=1;
            }
        }
        return count;
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer t=Integer.valueOf(time.substring(0,2))* 60 + Integer.valueOf(time.substring(3));
        Integer count=0;

        if(orderPartnerPair.containsKey(partnerId)) {
            List<String> list=orderPartnerPair.get(partnerId);
            for (String s : list) {
                if (orderHashMap.containsKey(s)) {
                    Order curr = orderHashMap.get(s);
                    if (t < curr.getDeliveryTime()) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time=0;
        if(orderPartnerPair.containsKey(partnerId)) {
            List<String> list=orderPartnerPair.get(partnerId);
            for (String s : list) {
                if (orderHashMap.containsKey(s)) {
                    Order curr = orderHashMap.get(s);
                    time = Math.max(time, curr.getDeliveryTime());
                }
            }
        }
        Integer hour=time/60;
        Integer min=time%60;

        String h=String.valueOf(hour);
        String m=String.valueOf(min);

        if(h.length()==1){
            h="0"+h;
        }
        if(m.length()==1){
            m="0"+m;
        }
        return h+":"+m;
    }
    public void deletePartnerById(String partnerId){
        if(deliveryPartnerHashMap.containsKey(partnerId)){
            deliveryPartnerHashMap.remove(partnerId);
        }
        if(orderPartnerPair.containsKey(partnerId)) {
            for (String s : orderPartnerPair.get(partnerId)) {
                if (ordertopartner.containsKey(s)) {
                    ordertopartner.remove(s);
                }
            }
            orderPartnerPair.remove(partnerId);
        }
        return;
    }
    public void deleteOrderById(String orderId){
        if(orderHashMap.containsKey(orderId)){
            orderHashMap.remove(orderId);
        }
        if(ordertopartner.containsKey(orderId)){
            String currPartner=ordertopartner.get(orderId);
            orderPartnerPair.get(currPartner).remove(orderId);
            DeliveryPartner curr=deliveryPartnerHashMap.get(currPartner);
            curr.setNumberOfOrders(orderPartnerPair.get(currPartner).size());
        }
        return ;
    }

}
