package ru.crmkurgan.main.Models;

public class PereplanModels {

        private String PereplanId;
        private String PereplanName;
        private int PereplanSelected;

        public String getPereplanId() {
            return PereplanId;
        }

        public void setPereplanId(String id) {
            this.PereplanId = id;
        }

        public String getPereplanName() {
            return PereplanName;
        }

        public void setPereplanName(String name) {
            this.PereplanName = name;
        }

    public int getPereplanSelected() {
        return PereplanSelected;
    }

    public void setPereplanSelected(int selected) {
        this.PereplanSelected = selected;
    }
    }
