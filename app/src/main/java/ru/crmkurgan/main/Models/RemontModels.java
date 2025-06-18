package ru.crmkurgan.main.Models;

public class RemontModels {

        private String RemontId;
        private String RemontName;
        private int RemontSelected;

        public String getRemontId() {
            return RemontId;
        }

        public void setRemontId(String id) {
            this.RemontId = id;
        }

        public String getRemontName() {
            return RemontName;
        }

        public void setRemontName(String name) {
            this.RemontName = name;
        }

    public int getRemontSelected() {
        return RemontSelected;
    }

    public void setRemontSelected(int selected) {
        this.RemontSelected = selected;
    }
    }
