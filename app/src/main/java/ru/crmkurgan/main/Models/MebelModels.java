package ru.crmkurgan.main.Models;

public class MebelModels {

        private String MebelId;
        private String MebelName;
        private int MebelSelected;

        public String getMebelId() {
            return MebelId;
        }

        public void setMebelId(String id) {
            this.MebelId = id;
        }

        public String getMebelName() {
            return MebelName;
        }

        public void setMebelName(String name) {
            this.MebelName = name;
        }

    public int getMebelSelected() {
        return MebelSelected;
    }

    public void setMebelSelected(int selected) {
        this.MebelSelected = selected;
    }
    }
