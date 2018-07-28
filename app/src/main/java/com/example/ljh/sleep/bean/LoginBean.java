package com.example.ljh.sleep.bean;

public class LoginBean {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String username;
    private String password;

    public class LoginResponse{
        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public int getReasonCode() {
            return reasonCode;
        }

        public void setReasonCode(int reasonCode) {
            this.reasonCode = reasonCode;
        }

        private int reasonCode;
    }

    public static class LoginCheckBox{
        public boolean isRemember() {
            return remember;
        }

        public void setRemember(boolean remember) {
            this.remember = remember;
        }

        public boolean isAuto() {
            return auto;
        }

        public void setAuto(boolean auto) {
            this.auto = auto;
        }

        private boolean remember;
        private boolean auto;
    }
}
