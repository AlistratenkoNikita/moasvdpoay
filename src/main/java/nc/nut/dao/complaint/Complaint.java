/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.nut.dao.complaint;

import java.util.Calendar;

/**
 * @author Alistratenko Nikita
 */
public class Complaint {

    private int id;
    private Calendar creation_date;
    private String description;
    private String status;
    private int csr_id;
    private int order_id;

    public Complaint() {
    }

    public Complaint(int id, Calendar creation_date, String description, String status, int csr_id, int order_id) {
        this.id = id;
        this.creation_date = creation_date;
        this.description = description;
        this.status = status;
        this.csr_id = csr_id;
        this.order_id = order_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Calendar creation_date) {
        this.creation_date = creation_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus_id(String status) {
        this.status = status;
    }

    public int getCsr_id() {
        return csr_id;
    }

    public void setCsr_id(int csr_id) {
        this.csr_id = csr_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    @Override
    public String toString() {
        return "Complaint{" + "id=" + id
                + ", creation_date=" + creation_date
                + ", description=" + description
                + ", status=" + status
                + ", csr_id=" + csr_id
                + ", order_id=" + order_id + '}';
    }

}