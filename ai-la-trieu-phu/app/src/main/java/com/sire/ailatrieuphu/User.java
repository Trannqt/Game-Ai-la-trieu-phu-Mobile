package com.sire.ailatrieuphu;

public class User implements Comparable<User> {
    private String user;
    private int score;

    public User() {}

    public User(String name, int score) {
        this.user = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String name) {
        this.user = name;
    }

    public String toString() {
        return this.user + " - " + this.score;
    }

    // Sắp xếp
    @Override
    public int compareTo(User o) {
        int comparedUser = o.getScore();
        // Giảm dần
        return comparedUser - this.getScore();
        // Tăng dần
        //return Integer.parseInt(this.getScore()) - comparedUser;
    }
}
