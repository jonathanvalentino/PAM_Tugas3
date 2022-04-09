package com.jonathan.pam_tugas3.model;

public class Order {

        private String id, name, date;

        public Order(String name, String date){
            this.name = name;
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String email) {
            this.date = date;
        }
}
