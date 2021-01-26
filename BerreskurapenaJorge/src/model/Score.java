/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jorge
 */
public class Score {
    private Double score;
    private String type;

    public Score() {
    }

    public Score(Double score, String type) {
        this.score = score;
        this.type = type;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Score --> [" + score + ", " + type + ']';
    }
  
}
