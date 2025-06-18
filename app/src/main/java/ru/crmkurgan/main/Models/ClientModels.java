package ru.crmkurgan.main.Models;

public class ClientModels {

        private String ClientId;
        private String ClientName;
        private int ClientSelected;

        public String getClientId() {
            return ClientId;
        }

        public void setClientId(String id) {
            this.ClientId = id;
        }

        public String getClientName() {
            return ClientName;
        }

        public void setClientName(String name) {
            this.ClientName = name;
        }

    public int getClientSelected() {
        return ClientSelected;
    }

    public void setClientSelected(int selected) {
        this.ClientSelected = selected;
    }
    }
