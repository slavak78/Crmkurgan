package ru.crmkurgan.main.Models;

public class BalkonModels {

        private String BalkonId;
        private String BalkonName;
        private int BalkonSelected;

        public String getBalkonId() {
            return BalkonId;
        }

        public void setBalkonId(String id) {
            this.BalkonId = id;
        }

        public String getBalkonName() {
            return BalkonName;
        }

        public void setBalkonName(String name) {
            this.BalkonName = name;
        }

    public int getBalkonSelected() {
        return BalkonSelected;
    }

    public void setBalkonSelected(int selected) {
        this.BalkonSelected = selected;
    }
    }
