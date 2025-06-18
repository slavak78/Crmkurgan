package ru.crmkurgan.main.Models;

public class PostroyModels {

        private String PostroyId;
        private String PostroyName;
        private int PostroySelected;

        public String getPostroyId() {
            return PostroyId;
        }

        public void setPostroyId(String id) {
            this.PostroyId = id;
        }

        public String getPostroyName() {
            return PostroyName;
        }

        public void setPostroyName(String name) {
            this.PostroyName = name;
        }

    public int getPostroySelected() {
        return PostroySelected;
    }

    public void setPostroySelected(int selected) {
        this.PostroySelected = selected;
    }
    }
