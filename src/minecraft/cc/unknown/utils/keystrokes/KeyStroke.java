package cc.unknown.utils.keystrokes;

public enum KeyStroke {
	instance;
    private int xPosition;
    private int yPosition;

    KeyStroke() {
        xPosition = 0;
        yPosition = 0;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
}