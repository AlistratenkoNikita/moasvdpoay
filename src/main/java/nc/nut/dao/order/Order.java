/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.nut.dao.order;

import nc.nut.dao.entity.OperationStatus;

import java.util.Objects;

/**
 * @author Alistratenko Nikita
 */
public class Order {

    private Integer id;
    private Integer productId;
    private Integer userId;
    private OperationStatus currentStatus;


    public Order(Integer id, Integer productId, Integer userId, OperationStatus currentStatus) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.currentStatus = currentStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public OperationStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(OperationStatus current_status) {
        this.currentStatus = current_status;
    }

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" + "id=" + id
                + ", productId=" + productId
                + ", userId=" + userId
                + ", currentStatus=" + currentStatus + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId()) &&
                Objects.equals(getProductId(), order.getProductId()) &&
                Objects.equals(getUserId(), order.getUserId()) &&
                getCurrentStatus() == order.getCurrentStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProductId(), getUserId(), getCurrentStatus());
    }
}
