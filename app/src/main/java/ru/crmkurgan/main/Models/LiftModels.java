package ru.crmkurgan.main.Models;

public class LiftModels {

        private String LiftId;
        private String LiftName;
        private int LiftSelected;

        public String getLiftId() {
            return LiftId;
        }

        public void setLiftId(String id) {
            this.LiftId = id;
        }

        public String getLiftName() {
            return LiftName;
        }

        public void setLiftName(String name) {
            this.LiftName = name;
        }

    public int getLiftSelected() {
        return LiftSelected;
    }

    public void setLiftSelected(int selected) {
        this.LiftSelected = selected;
    }
    }
