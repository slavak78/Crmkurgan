package ru.crmkurgan.main.Models;

public class CategoryModels {

        private String CategoryId;
        private String CategoryName;
        private String CategoryTabl;
        private int CategorySelected;

        public String getCategoryId() {
            return CategoryId;
        }

        public void setCategoryId(String id) {
            this.CategoryId = id;
        }


        public String getCategoryTabl() {
            return CategoryTabl;
        }

        public void setCategoryTabl(String tabl) {
            this.CategoryTabl = tabl;
        }

        public String getCategoryName() {
            return CategoryName;
        }

        public void setCategoryName(String name) {
            this.CategoryName = name;
        }

    public int getCategorySelected() {
        return CategorySelected;
    }

    public void setCategorySelected(int selected) {
        this.CategorySelected = selected;
    }

    }
