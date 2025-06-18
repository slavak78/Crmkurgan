package ru.crmkurgan.main.Models;

public class PlanirModels {

        private String PlanirId;
        private String PlanirName;
        private int PlanirSelected;

        public String getPlanirId() {
            return PlanirId;
        }

        public void setPlanirId(String id) {
            this.PlanirId = id;
        }

        public String getPlanirName() {
            return PlanirName;
        }

        public void setPlanirName(String name) {
            this.PlanirName = name;
        }

    public int getPlanirSelected() {
        return PlanirSelected;
    }

    public void setPlanirSelected(int selected) {
        this.PlanirSelected = selected;
    }
    }
