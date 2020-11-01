package org.rmc.buscaminas;

public class ButtonCell {

    private int x;
    private int y;
    private int number;
    private boolean mine;
    private boolean hidden;
    private boolean inAlert;

    public ButtonCell() {
    }

    public ButtonCell(int x, int y, int number, boolean mine) {
        this.x = x;
        this.y = y;
        this.number = number;
        this.mine = mine;
        this.hidden = true;
        this.inAlert = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNumber() {
        return number;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isInAlert() {
        return inAlert;
    }

    public void setInAlert(boolean inAlert) {
        this.inAlert = inAlert;
    }
}

