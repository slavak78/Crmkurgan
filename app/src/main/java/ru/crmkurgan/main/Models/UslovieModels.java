package ru.crmkurgan.main.Models;

public class UslovieModels {

        private String UslovieId;
        private String UslovieName;
        private int UslovieSelected;

        public String getUslovieId() {
            return UslovieId;
        }

        public void setUslovieId(String id) {
            this.UslovieId = id;
        }

        public String getUslovieName() {
            return UslovieName;
        }

        public void setUslovieName(String name) {
            this.UslovieName = name;
        }

    public int getUslovieSelected() {
        return UslovieSelected;
    }

    public void setUslovieSelected(int selected) {
        this.UslovieSelected = selected;
    }
    }
